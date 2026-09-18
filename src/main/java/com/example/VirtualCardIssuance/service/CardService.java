package com.example.VirtualCardIssuance.service;

import com.example.VirtualCardIssuance.dto.CardRequest;
import com.example.VirtualCardIssuance.dto.CardResponse;
import com.example.VirtualCardIssuance.dto.SpendRequest;
import com.example.VirtualCardIssuance.dto.TopupRequest;

public interface CardService {
    void createNewCard(CardRequest cardRequest);
    void spendFromCard(SpendRequest spendRequest, String idempotencyKey);
    void topUp(TopupRequest topupRequest, String idempotencyKey);
    CardResponse cardDetails(Long cardId);
}
