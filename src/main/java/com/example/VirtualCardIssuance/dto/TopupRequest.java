package com.example.VirtualCardIssuance.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TopupRequest extends TransactionRequest{
    @NotNull(message = "Credit Amount should not be null")
    @DecimalMin(value="0.0", message = "Credit Amount should not be negative")
    private BigDecimal creditAmount;
}
