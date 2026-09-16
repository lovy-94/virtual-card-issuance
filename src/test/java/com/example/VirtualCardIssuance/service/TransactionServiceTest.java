package com.example.VirtualCardIssuance.service;

import com.example.VirtualCardIssuance.dto.TransactionResponse;
import com.example.VirtualCardIssuance.entity.Transaction;
import com.example.VirtualCardIssuance.entity.TransactionStatus;
import com.example.VirtualCardIssuance.entity.TransactionType;
import com.example.VirtualCardIssuance.exception.CardNotFoundException;
import com.example.VirtualCardIssuance.repository.TransactionRepository;
import com.example.VirtualCardIssuance.validation.CardValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Incubating;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    private List<Transaction> transactionList;
    @InjectMocks
    private TransactionService transactionService;
    @BeforeEach
    void setUp() {
        Transaction trans1 =  new Transaction(1L, 1L, TransactionType.SPEND, BigDecimal.valueOf(6000), LocalDateTime.now().minusHours(60), TransactionStatus.SUCCESSFUL, "abc-121",0L);
        Transaction trans2 =  new Transaction(2L, 1L, TransactionType.SPEND, BigDecimal.valueOf(16000), LocalDateTime.now(), TransactionStatus.DECLINED, "abc-122",1L);
        Transaction trans3 =  new Transaction(3L, 1L, TransactionType.SPEND, BigDecimal.valueOf(7000), LocalDateTime.now().minusHours(10), TransactionStatus.PENDING, "def-123",2L);
        Transaction trans4 =  new Transaction(4L, 1L, TransactionType.TOPUP, BigDecimal.valueOf(6000), LocalDateTime.now(), TransactionStatus.DECLINED, "def-124",3L);
        Transaction trans5 =  new Transaction(5L, 1L, TransactionType.TOPUP, BigDecimal.valueOf(2000), LocalDateTime.now().minusHours(40),TransactionStatus.SUCCESSFUL,"gfk-125",4L);
        Transaction trans6 =  new Transaction(6L, 1L, TransactionType.TOPUP, BigDecimal.valueOf(6000), LocalDateTime.now().minusHours(10),TransactionStatus.PENDING, "ghj-126",5L);
        transactionList = Arrays.asList(trans1,trans2,trans3,trans4,trans5,trans6);
    }

    @Test
    public void testRetrieveTransactions(){

        Mockito.when(transactionRepository.findByCardId(1L)).thenReturn(transactionList);
        List<TransactionResponse> txResp =
                transactionService.retrieveTransactions(1L);
        assertEquals(6, txResp.size());

        assertEquals(1L, txResp.get(0).getId());
        assertEquals(1L, txResp.get(1).getCardId());
        assertEquals(TransactionType.SPEND, txResp.get(2).getTransactionType());
        assertEquals(TransactionStatus.DECLINED, txResp.get(3).getTransactionStatus());

        assertEquals(5L, txResp.get(4).getId());
        assertEquals(TransactionType.TOPUP, txResp.get(5).getTransactionType());
        assertEquals(TransactionStatus.PENDING, txResp.get(5).getTransactionStatus());
    }

    @Test
    public void testRetrieveTransactionsCardWithNoTransactions(){
        Mockito.when(transactionRepository.findByCardId(1L)).thenReturn(new ArrayList<>());
        assertThrows(CardNotFoundException.class,()->transactionService.retrieveTransactions(1L));
    }
}
