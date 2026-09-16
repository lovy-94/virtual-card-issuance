package com.example.VirtualCardIssuance.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
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
