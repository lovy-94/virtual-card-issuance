package com.example.VirtualCardIssuance.controller;

import com.example.VirtualCardIssuance.dto.*;
import com.example.VirtualCardIssuance.service.CardService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cards")
@Slf4j
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService){
        this.cardService=cardService;
    }
    @PostMapping("/createNewCard")
    public ResponseEntity<String> createNewCard(@Valid @RequestBody CardRequest cardRequest){
        log.info("Create new card: cardRequest{}",cardRequest);
            cardService.createNewCard(cardRequest);
            return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @PostMapping("/spend")
    public ResponseEntity<String> spendFromCard(@RequestHeader("idempotency-key") String idempotencyKey
                                                ,@Valid @RequestBody SpendRequest spendRequest){
        log.info("Spend From Card: idempotencyKey {}, spendRequest{}",idempotencyKey,spendRequest);
            cardService.spendFromCard(spendRequest,idempotencyKey);
            return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/topUp")
    public ResponseEntity<String> topUpCard(@RequestHeader("idempotency-key") String idempotencyKey,
                                            @Valid @RequestBody TopupRequest topupRequest){
        log.info("Top Up Card: idempotencyKey {}, topupRequest{}",idempotencyKey,topupRequest);
            cardService.topUp(topupRequest,idempotencyKey);
            return new ResponseEntity<>(HttpStatus.OK);
    }

   @GetMapping("/{cardId}")
    public ResponseEntity<CardResponse> cardDetails(@PathVariable Long cardId){
       log.info("card Details: cardId {}",cardId);
            CardResponse cardResponse = cardService.cardDetails(cardId);
            return new ResponseEntity<>(cardResponse, HttpStatus.OK);
        }


}
