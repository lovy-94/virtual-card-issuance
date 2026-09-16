package com.example.VirtualCardIssuance.dto;

import com.example.VirtualCardIssuance.entity.CardStatus;
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
public class CardResponse {
    private Long id;
    private String cardholderName;
    private BigDecimal balance;
    private CardStatus status;
    private LocalDateTime createdAt;


}
