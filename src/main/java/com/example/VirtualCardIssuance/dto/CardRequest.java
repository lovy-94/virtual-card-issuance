package com.example.VirtualCardIssuance.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardRequest {
    @NotNull(message = "Card Holder Name should not be null")
    private String cardHolderName;
    @NotNull(message = "Initial Balance should not be null")
    @DecimalMin(value="0.0",inclusive = false, message = "Initial Balance should not be negative")
    private BigDecimal initialBalance;
}
