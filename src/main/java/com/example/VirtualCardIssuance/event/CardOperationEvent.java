package com.example.VirtualCardIssuance.event;

import com.example.VirtualCardIssuance.entity.TransactionType;

import java.awt.image.Raster;

public class CardOperationEvent {
    private enum Outcome{SUCCESS,FAILURE}

    private  final Long cardId;
    private final TransactionType transactionType;
    private final Outcome outcome;
    private final String reason;

    public CardOperationEvent(Long cardId, TransactionType transactionType, Outcome outcome, String reason) {
        this.cardId = cardId;
        this.transactionType = transactionType;
        this.outcome = outcome;
        this.reason = reason;
    }

    public static CardOperationEvent success(Long cardId, TransactionType transactionType){
        return  new CardOperationEvent(cardId,transactionType,Outcome.SUCCESS,"success");
    }
    public static CardOperationEvent failure(Long cardId, TransactionType transactionType){
        return new CardOperationEvent(cardId,transactionType,Outcome.FAILURE,"failure");
    }

    public Long getCardId() {
        return cardId;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public Outcome getOutcome() {
        return outcome;
    }

    public String getReason() {
        return reason;
    }
}
