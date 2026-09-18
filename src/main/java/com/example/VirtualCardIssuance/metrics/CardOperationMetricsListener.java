package com.example.VirtualCardIssuance.metrics;

import com.example.VirtualCardIssuance.entity.TransactionType;
import com.example.VirtualCardIssuance.event.CardOperationEvent;
import io.micrometer.core.instrument.MeterRegistry;

public class CardOperationMetricsListener {
    private final MeterRegistry meterRegistry;

    public CardOperationMetricsListener(MeterRegistry meterRegistry){
        this.meterRegistry=meterRegistry;
    }

    public void onCardOperation(CardOperationEvent event){
        String metricName = event.getTransactionType()== TransactionType.SPEND?"card.spend":"card.topUp";
    }
}
