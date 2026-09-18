package com.example.VirtualCardIssuance.service;

import com.example.VirtualCardIssuance.dto.*;
import com.example.VirtualCardIssuance.entity.*;
import com.example.VirtualCardIssuance.event.CardOperationEvent;
import com.example.VirtualCardIssuance.exception.*;
import com.example.VirtualCardIssuance.repository.CardRepository;
import com.example.VirtualCardIssuance.validation.CardValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.ToIntBiFunction;


@Service
@Slf4j
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final TransactionServiceImpl transactionService;
    private final CardValidation cardValidation;
    private final ApplicationEventPublisher eventPublisher;

    public CardServiceImpl(CardRepository cardRepository, TransactionServiceImpl transactionService,
                           CardValidation cardValidation, ApplicationEventPublisher eventPublisher){
        this.cardRepository=cardRepository;
        this.transactionService = transactionService;
        this.cardValidation = cardValidation;
        this.eventPublisher = eventPublisher;
    }
    @Override
    @Transactional
    public void createNewCard(CardRequest cardRequest){
        log.info("Create New Card: cardRequest {}",cardRequest);
        Card card = new Card();
        card.setCardholderName(cardRequest.getCardHolderName());
        card.setBalance(cardRequest.getInitialBalance());
        card.setCreatedAt(LocalDateTime.now());
        card.setStatus(CardStatus.ACTIVE);
        Card persistedCard = cardRepository.save(card);
        log.info("New card is created : persistedCard {}",persistedCard);
    }

    @Override
    @Transactional
    public void spendFromCard(SpendRequest spendRequest, String idempotencyKey){
        processRequest(spendRequest.getCardId(),spendRequest.getDebitAmount(),TransactionType.SPEND, idempotencyKey,
                (card,amount)->cardValidation.spendCardValidations(card,spendRequest),
                (Long,BigDecimal)->cardRepository.debitAmount(spendRequest.getCardId(),spendRequest.getDebitAmount()));
    }

    private void processRequest(Long cardId, BigDecimal amount, TransactionType type, String idempotencyKey,
                                BiConsumer<Card,BigDecimal> validate, ToIntBiFunction<Long, BigDecimal> balanceUpdater) {
        log.info("Spend from card : cardId {}, idempotencyKey {}", cardId, idempotencyKey);
        checkIdempotency(idempotencyKey);
        Transaction transaction = buildPendingTransaction(cardId, amount, type, idempotencyKey);
        try{

            transactionService.saveTransaction(transaction);


            Card card = cardRepository.findById(cardId).orElse(null);
            validate.accept(card,amount);

            int rowsUpdated = balanceUpdater.applyAsInt(cardId,amount);
            if (rowsUpdated == 1) {
                markSuccessfull(transaction);
                eventPublisher.publishEvent(CardOperationEvent.success(cardId,type));
                log.info("Spend from card : type {}, cardId {}, amount{} ", type, cardId,amount);
                return;
            }

            throw new ConcurrentUpdateException("Spend request failed or two requests trying to modify together " + cardId +
                    " " + amount);

        }
        catch(CardNotFoundException | InactiveCardException
        | InsufficientBalanceException | ConcurrentUpdateException ex){
            updateFailedTransaction(transaction);
            eventPublisher.publishEvent(CardOperationEvent.failure(cardId,type));
            throw ex;
        }
        catch (DataIntegrityViolationException | DuplicateRequestException ex){
            eventPublisher.publishEvent(CardOperationEvent.failure(cardId,type));
            throw  new DuplicateRequestException("Duplicate request for idempotency key"+ idempotencyKey);
        }
    }

    private void markSuccessfull(Transaction transaction) {
        transaction.setStatus(TransactionStatus.SUCCESSFUL);
        transactionService.saveTransaction(transaction);
    }

    private static Transaction buildPendingTransaction(Long cardId, BigDecimal amount, TransactionType type, String idempotencyKey) {
        Transaction transaction = new Transaction();
        transaction.setCardId(cardId);
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setIdempotencyKey(idempotencyKey);
        transaction.setStatus(TransactionStatus.PENDING);
        return transaction;
    }

    public void checkIdempotency(String idempotencyKey) {

            Optional<Transaction>  duplicateTxnExists= transactionService.findByIdempotencyKey(idempotencyKey);
            if(duplicateTxnExists.isPresent()){
                throw new DuplicateRequestException("Duplicate Request found {}"+idempotencyKey);
            }

    }

    @Override
    @Transactional
    public void topUp(TopupRequest topupRequest, String idempotencyKey){
        log.info("Top up card : topupRequest {}, idempotencyKey {} ",topupRequest, idempotencyKey);
        processRequest(topupRequest.getCardId(),topupRequest.getCreditAmount(),TransactionType.TOPUP,idempotencyKey,
                (card,BigDecimal)->cardValidation.topUpValidations(card,topupRequest),
                (Long,BigDecimal)->cardRepository.creditAmount(topupRequest.getCardId(), topupRequest.getCreditAmount()));
    }

    private void updateFailedTransaction(Transaction transaction) {
        transaction.setStatus(TransactionStatus.DECLINED);
        transactionService.saveFailedTransaction(transaction);
    }

    @Override
    public CardResponse cardDetails(Long cardId) {
        log.info("cardDetails : cardId {}",cardId);
        Card card = cardRepository.findById(cardId).orElseThrow(()-> new CardNotFoundException("Card Not found, Card Id"+cardId));

        CardResponse cardResponse = new CardResponse(card.getId(),card.getCardholderName(),card.getBalance(),card.getStatus(),
                card.getCreatedAt());
        log.info("cardDetails : cardResponse {}",cardResponse);
        return cardResponse;
    }
}
