package com.example.VirtualCardIssuance.validation;

import com.example.VirtualCardIssuance.entity.Card;
import com.example.VirtualCardIssuance.exception.InsufficientBalanceException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
@Component
public class SufficientBalanceRule implements SpendRule{
    @Override
    public void validate(Card card, BigDecimal amount) {
        if(card.getBalance().compareTo(amount)<0){
            throw new InsufficientBalanceException("Insufficient Balance, lower than debit amount"+amount);
        }
    }
}
