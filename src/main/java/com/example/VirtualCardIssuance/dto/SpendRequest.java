package com.example.VirtualCardIssuance.dto;

import jakarta.validation.constraints.DecimalMin;
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
public class SpendRequest extends TransactionRequest{
    @NotNull(message = "Debit Amount should not be null")
    @DecimalMin(value="0.0", message = "Debit Amount should not be negative")
    private BigDecimal debitAmount;
}
