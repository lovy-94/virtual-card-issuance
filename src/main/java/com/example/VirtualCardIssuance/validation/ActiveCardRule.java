package com.example.VirtualCardIssuance.validation;

import com.example.VirtualCardIssuance.entity.Card;
import com.example.VirtualCardIssuance.entity.CardStatus;
import com.example.VirtualCardIssuance.exception.InactiveCardException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
@Component
public class ActiveCardRule implements SpendRule,TopUpRule{

    @Override
    public void validate(Card card, BigDecimal amount) {
        if(!card.canTransact()){
            throw new InactiveCardException("Inacive card , Card status is "+card.getStatus());
        }
    }
}
