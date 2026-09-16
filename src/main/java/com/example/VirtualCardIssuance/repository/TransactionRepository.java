package com.example.VirtualCardIssuance.repository;

import com.example.VirtualCardIssuance.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByCardId(Long cardId);
    Optional<Transaction> findByIdempotencyKey(String idempotencyKey);
}
