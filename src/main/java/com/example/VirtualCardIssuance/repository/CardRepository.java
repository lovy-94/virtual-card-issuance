package com.example.VirtualCardIssuance.repository;

import com.example.VirtualCardIssuance.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface CardRepository extends JpaRepository<Card,Long> {
    @Modifying
    @Query("UPDATE Card c SET c.balance=c.balance-:debitAmount, c.version=c.version+1 " +
            "WHERE c.id=:cardId AND c.status=com.example.VirtualCardIssuance.entity.CardStatus.ACTIVE AND c.balance>=:debitAmount")
    int debitAmount(@Param("cardId") Long cardId, @Param("debitAmount") BigDecimal debitAmount);

    @Modifying
    @Query("UPDATE Card c SET c.balance=c.balance+:creditAmount, c.version=c.version+1 " +
            "WHERE c.id=:cardId AND c.status=com.example.VirtualCardIssuance.entity.CardStatus.ACTIVE")
    int creditAmount(@Param("cardId")Long cardId, @Param("creditAmount")BigDecimal creditAmount);
}
