package com.example.VirtualCardIssuance.validation;

import com.example.VirtualCardIssuance.dto.SpendRequest;
import com.example.VirtualCardIssuance.dto.TopupRequest;
import com.example.VirtualCardIssuance.entity.Card;
import com.example.VirtualCardIssuance.entity.CardStatus;
import com.example.VirtualCardIssuance.exception.CardNotFoundException;
import com.example.VirtualCardIssuance.exception.InactiveCardException;
import com.example.VirtualCardIssuance.exception.InsufficientBalanceException;
import com.example.VirtualCardIssuance.exception.NegativeAmountException;
import com.example.VirtualCardIssuance.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;


@Component
@Slf4j
public class CardValidation  {

    private final TransactionService transactionService;

    public CardValidation(TransactionService transactionService){
        this.transactionService=transactionService;
    }


    public void spendCardValidations(Card card,SpendRequest spendRequest) {
        log.info("topUpValidations card {}, spendRequest {} ",card,spendRequest);
        commonValidation(card,spendRequest,null);

        if (spendRequest != null &&
                spendRequest.getDebitAmount().compareTo(BigDecimal.ZERO) <= 0) {

            throw new NegativeAmountException(
                    "Amount should be greater than ZERO, Debit Amount is "
                            + spendRequest.getDebitAmount()
            );
        }
        if(card.getBalance().compareTo(spendRequest.getDebitAmount()) < 0){
            throw new InsufficientBalanceException("Insufficient Balance, is lower than Debit amount "+spendRequest.getDebitAmount());
        }

    }

    public void topUpValidations(Card card, TopupRequest topupRequest ) {

        log.info("topUpValidations card {}, topupRequest {} ",card,topupRequest);

        commonValidation(card,null,topupRequest);
        if (topupRequest != null &&
                topupRequest.getCreditAmount().compareTo(BigDecimal.ZERO) <= 0) {

            throw new NegativeAmountException(
                    "Amount should be greater than ZERO, Credit Amount is "
                            + topupRequest.getCreditAmount()
            );
        }
    }

    public void commonValidation(Card card, SpendRequest spendRequest,
                              TopupRequest topupRequest   ){
        log.info("commonValidation card {}, spendRequest {}, topupRequest {} ",card,spendRequest, topupRequest);
        if(card==null){
            if(spendRequest!=null) {
                throw new CardNotFoundException("Card Not found cardId " + spendRequest.getCardId());
            }
            else if(topupRequest!=null) {
                throw new CardNotFoundException("Card Not found cardId " + topupRequest.getCardId());
            }
        }
        if(!CardStatus.ACTIVE.equals(card.getStatus())){
            throw new InactiveCardException("Inactive Card, Card is "+card.getStatus());
        }
    }
}
