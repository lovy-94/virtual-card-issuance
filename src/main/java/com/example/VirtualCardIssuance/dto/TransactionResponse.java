package com.example.VirtualCardIssuance.dto;

import com.example.VirtualCardIssuance.entity.TransactionStatus;
import com.example.VirtualCardIssuance.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse {
    public Long id;
    public Long cardId;
    public BigDecimal transactionAmount;
    public LocalDateTime createdAt;
    public TransactionType transactionType;
    public TransactionStatus transactionStatus;
}
