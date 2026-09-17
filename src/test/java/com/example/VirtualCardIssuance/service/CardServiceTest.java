package com.example.VirtualCardIssuance.service;

import com.example.VirtualCardIssuance.dto.CardRequest;
import com.example.VirtualCardIssuance.dto.CardResponse;
import com.example.VirtualCardIssuance.dto.SpendRequest;
import com.example.VirtualCardIssuance.dto.TopupRequest;
import com.example.VirtualCardIssuance.entity.*;
import com.example.VirtualCardIssuance.exception.*;
import com.example.VirtualCardIssuance.repository.CardRepository;
import com.example.VirtualCardIssuance.validation.CardValidation;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private TransactionService transactionService;


    private CardValidation cardValidation;
    private  CardService cardService;
    private CardRequest cardRequest;
    private SpendRequest spendRequest;
    private Card card;
    private TopupRequest topupRequest;
    private Transaction existingTransaction;
    private MeterRegistry meterRegistry;

    @BeforeEach
    void setUp() {
        cardValidation = new CardValidation();
meterRegistry=new SimpleMeterRegistry();
        cardService = new CardService(
                cardRepository,
                transactionService,
                cardValidation,
                meterRegistry
        );

         cardRequest = new CardRequest("Amy", BigDecimal.valueOf(5000));
         spendRequest = new SpendRequest(BigDecimal.valueOf(500));
         spendRequest.setCardId(1L);
         card = new Card(1L, "Amy", BigDecimal.valueOf(5000), CardStatus.ACTIVE, LocalDateTime.now(),1L);
         topupRequest = new TopupRequest(BigDecimal.valueOf(500));
         topupRequest.setCardId(1L);
        existingTransaction= new Transaction();
        existingTransaction.setId(100L);
        existingTransaction.setCardId(1L);
        existingTransaction.setAmount(BigDecimal.valueOf(500));
        existingTransaction.setType(TransactionType.SPEND);
        existingTransaction.setStatus(TransactionStatus.SUCCESSFUL);
        existingTransaction.setIdempotencyKey("abc-121");
    }
    @Test
    public void testCreateNewCard(){
        cardService.createNewCard(cardRequest);
        verify(cardRepository, times(1)).save(any());
    }

    @Test
    public void testPositiveSpendFromCard(){
        when(transactionService.findByIdempotencyKey("abc-121"))
                .thenReturn(Optional.empty());
        when(cardRepository.findById(spendRequest.getCardId()))
                .thenReturn(Optional.of(card));
        when(cardRepository.debitAmount(1L,BigDecimal.valueOf(500))).thenReturn(1);
        cardService.spendFromCard(spendRequest,"abc-121");
        verify(transactionService, Mockito.times(2)).saveTransaction(any());
    }

    @Test
    public void testCardNotFoundSpendFromCard(){
        when(transactionService.findByIdempotencyKey("abc-121"))
                .thenReturn(Optional.empty());
        when(cardRepository.findById(spendRequest.getCardId()))
                .thenReturn(Optional.empty());
        assertThrows(
                CardNotFoundException.class,
                () -> cardService.spendFromCard(spendRequest,"abc-121")
        );
        verify(transactionService, Mockito.times(1)).saveFailedTransaction(any());
    }

    @Test
    public void testInactiveCardSpendFromCard(){
        card.setStatus(CardStatus.BLOCKED);
        when(transactionService.findByIdempotencyKey("abc-121"))
                .thenReturn(Optional.empty());
        when(cardRepository.findById(spendRequest.getCardId()))
                .thenReturn(Optional.of(card));
        assertThrows(
                InactiveCardException.class,
                () -> cardService.spendFromCard(spendRequest,"abc-121")
        );
        verify(transactionService,Mockito.times(1)).saveFailedTransaction(any());
    }

    @Test
    public void testInsufficientBalanceSpendFromCard(){
        spendRequest.setDebitAmount(BigDecimal.valueOf(5001));
        when(transactionService.findByIdempotencyKey("abc-121"))
                .thenReturn(Optional.empty());
        when(cardRepository.findById(spendRequest.getCardId()))
                .thenReturn(Optional.of(card));
        assertThrows(
                InsufficientBalanceException.class,
                () -> cardService.spendFromCard(spendRequest,"abc-121")
        );
        verify(transactionService,Mockito.times(1)).saveFailedTransaction(any());
    }

    @Test
    public void testPositiveTopup(){
        when(transactionService.findByIdempotencyKey("abc-121"))
                .thenReturn(Optional.empty());
        when(cardRepository.findById(topupRequest.getCardId()))
                .thenReturn(Optional.of(card));
        when(cardRepository.creditAmount(1L,BigDecimal.valueOf(500))).thenReturn(1);
        cardService.topUp(topupRequest,"abc-121");
        verify(transactionService,Mockito.times(2)).saveTransaction(any());
    }

    @Test
    public void testCardNotFoundTopup(){
        when(transactionService.findByIdempotencyKey("abc-121"))
                .thenReturn(Optional.empty());
        when(cardRepository.findById(topupRequest.getCardId()))
                .thenReturn(Optional.empty());
        assertThrows(CardNotFoundException.class, ()->cardService.topUp(topupRequest,"abc-121"));
        verify(transactionService,Mockito.times(1)).saveFailedTransaction(any());
    }
    @Test
    public void testInactiveCardTopup(){
        card.setStatus(CardStatus.CLOSED);
        when(transactionService.findByIdempotencyKey("abc-121"))
                .thenReturn(Optional.empty());
        when(cardRepository.findById(topupRequest.getCardId())).thenReturn(Optional.of(card));
        assertThrows(InactiveCardException.class, ()->cardService.topUp(topupRequest,"abc-121"));
        verify(transactionService,Mockito.times(1)).saveFailedTransaction(any());
    }

    @Test
    public void testCardDetails(){
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        CardResponse cardResponse = cardService.cardDetails(1L);
        assertEquals(CardStatus.ACTIVE,cardResponse.getStatus());
        assertEquals(BigDecimal.valueOf(5000),cardResponse.getBalance());
    }
    @Test
    public void testNegativeCardDetails(){
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(CardNotFoundException.class, ()->cardService.cardDetails(1L));
    }

    @Test
    public void testDuplicateTransactionsForSpend() {
        when(transactionService.findByIdempotencyKey("abc-121"))
                .thenReturn(Optional.of(existingTransaction));

        assertThrows(
                DuplicateRequestException.class,
                () -> cardService.spendFromCard(spendRequest, "abc-121")
        );

        verify(transactionService, Mockito.never()).saveTransaction(any());
    }

    @Test
    public void testDuplicateTransactionsForTopup() {
        when(transactionService.findByIdempotencyKey("abc-121"))
                .thenReturn(Optional.of(existingTransaction));

        assertThrows(
                DuplicateRequestException.class,
                () -> cardService.topUp(topupRequest, "abc-121")
        );
        verify(transactionService, Mockito.never()).saveTransaction(any());
    }
}
