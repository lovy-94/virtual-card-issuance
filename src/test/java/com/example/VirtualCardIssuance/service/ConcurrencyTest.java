package com.example.VirtualCardIssuance.service;

import com.example.VirtualCardIssuance.dto.SpendRequest;
import com.example.VirtualCardIssuance.dto.TopupRequest;
import com.example.VirtualCardIssuance.entity.Card;
import com.example.VirtualCardIssuance.entity.CardStatus;
import com.example.VirtualCardIssuance.repository.CardRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ConcurrencyTest {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private CardService cardService;

    @Test
    void concurrentSpendUpdateTest() throws Exception {
        Card card = new Card();
        card.setCardholderName("Amy");
        card.setBalance(BigDecimal.valueOf(5000));
        card.setStatus(CardStatus.ACTIVE);
        card.setCreatedAt(LocalDateTime.now());

        Card persistedCard = cardRepository.save(card);

        Long cardId = persistedCard.getId();


        ExecutorService executorService = Executors.newFixedThreadPool(2);
        var res1 =  executorService.submit(()->{
        SpendRequest spendRequest1 = new SpendRequest(BigDecimal.valueOf(4800));

        spendRequest1.setCardId(cardId);
        try{
            cardService.spendFromCard(spendRequest1,UUID.randomUUID().toString());
            return "SUCCESS";
        } catch (Exception e) {
            return e.getClass().getSimpleName();
        }
        });

        var res2 = executorService.submit(()->{
            SpendRequest spendRequest2 = new SpendRequest(BigDecimal.valueOf(4800));

            spendRequest2.setCardId(cardId);
            try{
                cardService.spendFromCard(spendRequest2, UUID.randomUUID().toString());
                return "SUCCESS";
            } catch (Exception e) {
                return e.getClass().getSimpleName();
            }
        });

        String test1 = res1.get();
        String test2 = res2.get();;

        executorService.shutdown();
        System.out.println("test1 = " + test1);
        System.out.println("test2 = " + test2);

        assertTrue(test1.equals("SUCCESS")^test2.equals("SUCCESS"));
        Card finalCard = cardRepository.findById(cardId).orElse(null);
        assertEquals(0, BigDecimal.valueOf(200).compareTo(finalCard.getBalance()));


    }

    @Test
    void concurrentTopupUpdateTest() throws Exception {
        Card card = new Card();
        card.setCardholderName("Amy");
        card.setBalance(BigDecimal.valueOf(5000));
        card.setStatus(CardStatus.ACTIVE);
        card.setCreatedAt(LocalDateTime.now());

        Card persistedCard = cardRepository.save(card);

        Long cardId = persistedCard.getId();

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        var res1 =  executorService.submit(()->{
            TopupRequest topupRequest1 = new TopupRequest(BigDecimal.valueOf(200));

            topupRequest1.setCardId(cardId);
            try{
                cardService.topUp(topupRequest1,UUID.randomUUID().toString());
                return "SUCCESS";
            } catch (Exception e) {
                return e.getClass().getSimpleName();
            }
        });

        var res2 = executorService.submit(()->{
            TopupRequest topupRequest2 = new TopupRequest(BigDecimal.valueOf(200));

            topupRequest2.setCardId(cardId);
            try{
                cardService.topUp(topupRequest2,UUID.randomUUID().toString());
                return "SUCCESS";
            } catch (Exception e) {
                return e.getClass().getSimpleName();
            }
        });

        String test1 = res1.get();
        String test2 = res2.get();;

        executorService.shutdown();

        assertEquals("SUCCESS",test1);
        assertEquals("SUCCESS",test2);
        Card finalCard = cardRepository.findById(cardId).orElse(null);
        assertEquals(0, BigDecimal.valueOf(5400).compareTo(finalCard.getBalance()));


    }
    }
