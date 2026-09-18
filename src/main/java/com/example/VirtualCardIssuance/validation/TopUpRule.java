package com.example.VirtualCardIssuance.validation;

import com.example.VirtualCardIssuance.entity.Card;

import java.math.BigDecimal;

public interface TopUpRule {
    public void validate(Card card, BigDecimal amount);
}
