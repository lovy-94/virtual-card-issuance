package com.example.VirtualCardIssuance.metrics;

import com.example.VirtualCardIssuance.entity.TransactionType;
import com.example.VirtualCardIssuance.event.CardOperationEvent;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

public class CardOperationMetricsListener {
    private final MeterRegistry meterRegistry;

    public CardOperationMetricsListener(MeterRegistry meterRegistry){
        this.meterRegistry=meterRegistry;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION)
    public void onCardOperation(CardOperationEvent event){
        String metricName = event.getTransactionType()== TransactionType.SPEND?"card.spend":"card.topUp";
        meterRegistry.counter(metricName,"result",event.getReason()).increment();
    }
}
