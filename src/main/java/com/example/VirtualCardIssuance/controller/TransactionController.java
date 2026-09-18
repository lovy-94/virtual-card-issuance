package com.example.VirtualCardIssuance.controller;

import com.example.VirtualCardIssuance.dto.TransactionResponse;
import com.example.VirtualCardIssuance.service.TransactionServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/transactions")
@Slf4j
public class TransactionController {

    private final TransactionServiceImpl transactionService;

    public TransactionController(TransactionServiceImpl transactionService){
        this.transactionService=transactionService;
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<List<TransactionResponse>> retrieveTransactions(@PathVariable Long cardId){
            return new ResponseEntity<>(transactionService.retrieveTransactions(cardId), HttpStatus.OK);
    }
}
