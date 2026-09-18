package com.example.VirtualCardIssuance.service;

import com.example.VirtualCardIssuance.dto.TransactionResponse;

import java.util.List;

public interface TransactionService {
    public List<TransactionResponse>  retrieveTransactions(Long cardId);
}
