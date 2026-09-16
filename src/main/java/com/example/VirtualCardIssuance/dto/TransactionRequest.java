package com.example.VirtualCardIssuance.dto;

import com.example.VirtualCardIssuance.entity.TransactionType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {
    @NotNull(message = "card id should not be null")
    private Long cardId;
}
