package com.example.VirtualCardIssuance.service;

import com.example.VirtualCardIssuance.dto.*;
import com.example.VirtualCardIssuance.entity.*;
import com.example.VirtualCardIssuance.exception.*;
import com.example.VirtualCardIssuance.repository.CardRepository;
import com.example.VirtualCardIssuance.validation.CardValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;


@Service
@Slf4j
public class CardService {

    private final CardRepository cardRepository;
    private final TransactionService transactionService;
    private final CardValidation cardValidation;

    public CardService(CardRepository cardRepository, TransactionService transactionService, CardValidation cardValidation){
        this.cardRepository=cardRepository;
        this.transactionService = transactionService;
        this.cardValidation = cardValidation;
    }

    @Transactional
    public void createNewCard(CardRequest cardRequest){
        log.info("Create New Card: cardRequest {}",cardRequest);
        if (cardRequest.getInitialBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeAmountException("Initial balance is negative");
        }
        Card card = new Card();
        card.setCardholderName(cardRequest.getCardHolderName());
        card.setBalance(cardRequest.getInitialBalance());
        card.setCreatedAt(LocalDateTime.now());
        card.setStatus(CardStatus.ACTIVE);
        Card persistedCard = cardRepository.save(card);
        log.info("New card is created : persistedCard {}",persistedCard);
    }

    @Transactional
    public void spendFromCard(SpendRequest spendRequest, String idempotencyKey){
        log.info("Spend from card : spendRequest {}, idempotencyKey {}",spendRequest, idempotencyKey);
        Transaction transaction = new Transaction();
        transaction.setCardId(spendRequest.getCardId());
        transaction.setAmount(spendRequest.getDebitAmount());
        transaction.setType(TransactionType.SPEND);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setIdempotencyKey(idempotencyKey);

        checkIdempotency(idempotencyKey);
        try {
            Card card = cardRepository.findById(spendRequest.getCardId()).orElse(null);
            cardValidation.spendCardValidations(card, spendRequest);

            int debitted = cardRepository.debitAmount(spendRequest.getCardId(), spendRequest.getDebitAmount());
            if (debitted == 1) {
                transaction.setStatus(TransactionStatus.SUCCESSFUL);
                transactionService.saveTransaction(transaction);
                log.info("Spend from card : debitted {}, transaction {} ", debitted, transaction);
                return;
            }
            updateFailedTransaction(transaction);
            throw new RuntimeException("Spend request failed or two requests trying to modify together " + spendRequest.getCardId() +
                    " " + spendRequest.getDebitAmount());

        }
        catch(CardNotFoundException | InactiveCardException | NegativeAmountException
        | InsufficientBalanceException ex){
            updateFailedTransaction(transaction);
            throw ex;
        }
    }

    private void checkIdempotency(String idempotencyKey) {
        Optional<Transaction> existingTxn = transactionService.findByIdempotencyKey(idempotencyKey);

        if(existingTxn.isPresent()){
            throw new DuplicateRequestException("DuplicateRequest Trannsaction is already present "+ idempotencyKey);
        }
    }

    @Transactional
    public void topUp(TopupRequest topupRequest, String idempotencyKey){
        log.info("Top up card : topupRequest {}, idempotencyKey {} ",topupRequest, idempotencyKey);
        Transaction transaction = new Transaction();
        transaction.setCardId(topupRequest.getCardId());
        transaction.setAmount(topupRequest.getCreditAmount());
        transaction.setType(TransactionType.TOPUP);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setIdempotencyKey(idempotencyKey);

        checkIdempotency(idempotencyKey);

        Card card = cardRepository.findById(topupRequest.getCardId()).orElse(null);
    try {
        cardValidation.topUpValidations(card, topupRequest);

    int creditted = cardRepository.creditAmount(topupRequest.getCardId(), topupRequest.getCreditAmount());
    if (creditted == 1) {
        transaction.setStatus(TransactionStatus.SUCCESSFUL);
        transactionService.saveTransaction(transaction);
        log.info("Top up card : creditted {}, transaction {}", creditted, transaction);
        return;
    }
        updateFailedTransaction(transaction);
        throw new RuntimeException("Topup failed or two requests trying to modify together " + topupRequest.getCardId() +
            " " + topupRequest.getCreditAmount());
}
  catch(CardNotFoundException | InactiveCardException | NegativeAmountException
      | InsufficientBalanceException ex){
      updateFailedTransaction(transaction);
      throw ex;
}
    }

    private void updateFailedTransaction(Transaction transaction) {
        transaction.setStatus(TransactionStatus.DECLINED);
        transactionService.saveFailedTransaction(transaction);
    }

    public CardResponse cardDetails(Long cardId) {
        log.info("cardDetails : cardId {}",cardId);
        Card card = cardRepository.findById(cardId).orElseThrow(()-> new CardNotFoundException("Card Not found, Card Id"+cardId));

        CardResponse cardResponse = new CardResponse(card.getId(),card.getCardholderName(),card.getBalance(),card.getStatus(),
                card.getCreatedAt());
        log.info("cardDetails : cardResponse {}",cardResponse);
        return cardResponse;
    }
}
