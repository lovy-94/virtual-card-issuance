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
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
public class ConcurrencyTest {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private CardService cardService;

    @Test
    void concurrentSpendUpdateTest() throws Exception {

        // Arrange
        Card card = new Card();
        card.setCardholderName("Amy");
        card.setBalance(BigDecimal.valueOf(5000));
        card.setStatus(CardStatus.ACTIVE);
        card.setCreatedAt(LocalDateTime.now());

        Card persistedCard = cardRepository.saveAndFlush(card);
        Long cardId = persistedCard.getId();

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch startLatch = new CountDownLatch(1);

        Callable<String> spendTask = () -> {

            startLatch.await();

            SpendRequest request =
                    new SpendRequest(BigDecimal.valueOf(4800));

            request.setCardId(cardId);

            try {
                cardService.spendFromCard(
                        request,
                        UUID.randomUUID().toString()
                );

                return "SUCCESS";

            } catch (Exception e) {
                e.printStackTrace();
                return e.getClass().getSimpleName();
            }
        };

        Future<String> result1 = executor.submit(spendTask);
        Future<String> result2 = executor.submit(spendTask);

        // Start both threads at the same time
        startLatch.countDown();

        String test1 = result1.get();
        String test2 = result2.get();

        executor.shutdown();

        System.out.println("test1 = " + test1);
        System.out.println("test2 = " + test2);

        // One transaction must succeed and the other must fail
        assertTrue(
                test1.equals("SUCCESS") ^ test2.equals("SUCCESS"),
                "Exactly one spend should succeed"
        );

        // Final balance must be 200
        Card finalCard = cardRepository.findById(cardId)
                .orElseThrow();

        assertEquals(
                0,
                BigDecimal.valueOf(200)
                        .compareTo(finalCard.getBalance())
        );
    }

    @Test
    void concurrentTopupUpdateTest() throws Exception {

        // Arrange
        Card card = new Card();
        card.setCardholderName("Amy");
        card.setBalance(BigDecimal.valueOf(5000));
        card.setStatus(CardStatus.ACTIVE);
        card.setCreatedAt(LocalDateTime.now());

        Card persistedCard = cardRepository.saveAndFlush(card);
        Long cardId = persistedCard.getId();

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch startLatch = new CountDownLatch(1);

        Callable<String> topupTask = () -> {

            startLatch.await();

            TopupRequest request =
                    new TopupRequest(BigDecimal.valueOf(200));

            request.setCardId(cardId);

            try {
                cardService.topUp(
                        request,
                        UUID.randomUUID().toString()
                );

                return "SUCCESS";

            } catch (Exception e) {
                e.printStackTrace();
                return e.getClass().getSimpleName();
            }
        };

        Future<String> result1 = executor.submit(topupTask);
        Future<String> result2 = executor.submit(topupTask);

        // Start both threads at the same time
        startLatch.countDown();

        String test1 = result1.get();
        String test2 = result2.get();

        executor.shutdown();

        System.out.println("test1 = " + test1);
        System.out.println("test2 = " + test2);

        assertEquals("SUCCESS",test1);
        assertEquals("SUCCESS",test2);

        Card finalCard = cardRepository.findById(cardId)
                .orElseThrow();

        assertEquals(
                0,
                BigDecimal.valueOf(5400)
                        .compareTo(finalCard.getBalance())
        );
    }
    }
