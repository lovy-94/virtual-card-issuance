package com.example.VirtualCardIssuance.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long cardId;
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
    @Column(nullable = false, unique = true)
    private String idempotencyKey;
    @Version
    private Long version;

    public static Transaction pending(Long cardId, BigDecimal amount,TransactionType type, String idempotencyKey){
        Transaction transaction = new Transaction();
        transaction.cardId=cardId;
        transaction.amount = amount;
        transaction.type = type;
        transaction.createdAt = LocalDateTime.now();
        transaction.idempotencyKey = idempotencyKey;
        transaction.status = TransactionStatus.PENDING;
        return transaction;
    }

    public void markSuccesful(){
        requirePending();
        this.status=TransactionStatus.SUCCESSFUL;
    }
    public void markFailure(){
        requirePending();
        this.status=TransactionStatus.DECLINED;
    }

    private void requirePending() {
        if(this.status!=TransactionStatus.PENDING){
            throw new IllegalStateException("Cannnot transistion transaction "+id+" from terminal status "+status);
        }
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", cardId=" + cardId +
                ", type=" + type +
                ", amount=" + amount +
                ", createdAt=" + createdAt +
                ", status=" + status +
                ", idempotencyKey='" + idempotencyKey + '\'' +
                ", version=" + version +
                '}';
    }
}
