package com.example.VirtualCardIssuance.validation;

import com.example.VirtualCardIssuance.dto.SpendRequest;
import com.example.VirtualCardIssuance.dto.TopupRequest;
import com.example.VirtualCardIssuance.entity.Card;
import com.example.VirtualCardIssuance.exception.CardNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@Slf4j
public class CardValidation  {
    private final List<SpendRule> spendRule;
    private final List<TopUpRule> topUpRule;

    public CardValidation(List<SpendRule> spendRule, List<TopUpRule> topUpRule){
        this.spendRule=spendRule;
        this.topUpRule=topUpRule;
    }
    public void spendCardValidations(Card card,SpendRequest spendRequest) {
        log.info("topUpValidations card {}, spendRequest {} ",card,spendRequest);
        if(card==null){
            throw new CardNotFoundException("Card Not found cardId " + spendRequest.getCardId());
        }

        spendRule.forEach(rule-> rule.validate(card,spendRequest.getDebitAmount()));
    }

    public void topUpValidations(Card card, TopupRequest topupRequest ) {

        log.info("topUpValidations card {}, topupRequest {} ",card,topupRequest);

        if(card==null){
            throw new CardNotFoundException("Card Not found cardId " + topupRequest.getCardId());
        }

        topUpRule.forEach(rule-> rule.validate(card,topupRequest.getCreditAmount()));
    }
}
