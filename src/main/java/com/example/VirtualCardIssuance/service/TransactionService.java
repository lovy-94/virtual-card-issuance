package com.example.VirtualCardIssuance.service;

import com.example.VirtualCardIssuance.dto.TransactionResponse;
import com.example.VirtualCardIssuance.entity.Transaction;
import com.example.VirtualCardIssuance.exception.CardNotFoundException;
import com.example.VirtualCardIssuance.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;


    public TransactionService(TransactionRepository transactionRepository){
        this.transactionRepository = transactionRepository;
    }

    public void saveTransaction(Transaction transaction){
        transactionRepository.save(transaction);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveFailedTransaction(Transaction transaction){
        transactionRepository.save(transaction);
    }

    public List<TransactionResponse> retrieveTransactions(Long cardId){
        log.info("Retrieve Transactions cardId{}", cardId);
        List<Transaction> transactions =  transactionRepository.findByCardId(cardId);
        if(transactions.isEmpty()){
            throw new CardNotFoundException("Card not found for cardID "+cardId);
        }
        List<TransactionResponse> txResp = new ArrayList<>();
        if(!transactions.isEmpty()) {
            txResp = transactions.stream().map(t -> new TransactionResponse(t.getId(), t.getCardId(), t.getAmount(),
                    t.getCreatedAt(), t.getType(), t.getStatus())).collect(Collectors.toList());
        }
        log.info("Retrieve Transactions txResp{}", txResp);
           return txResp;
    }

    public Optional<Transaction> findByIdempotencyKey(String idempotencyKey) {
        return transactionRepository.findByIdempotencyKey(idempotencyKey);

    }
}
