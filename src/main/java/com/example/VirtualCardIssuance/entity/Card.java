package com.example.VirtualCardIssuance.entity;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String cardholderName;
    private BigDecimal balance;
    @Enumerated(EnumType.STRING)
    private CardStatus status;
    private LocalDateTime createdAt;
    @Version
    private Long version;

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", cardholderName='" + cardholderName + '\'' +
                ", balance=" + balance +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", version=" + version +
                '}';
    }
}


