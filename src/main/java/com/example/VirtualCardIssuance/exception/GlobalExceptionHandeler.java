package com.example.VirtualCardIssuance.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandeler {

    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<Error> cardNotFound(CardNotFoundException cardNotFoundException){
        Error error = new Error();
        error.setMessage(cardNotFoundException.getMessage());
        error.setStatus(HttpStatus.NOT_FOUND);
        error.setCreatedAt(LocalDateTime.now());
        log.info("Message: " + cardNotFoundException.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InactiveCardException.class)
    public ResponseEntity<Error> inactiveCardException(InactiveCardException inactiveCardException){
        Error error = new Error();
        error.setMessage(inactiveCardException.getMessage());
        error.setStatus(HttpStatus.BAD_REQUEST);
        error.setCreatedAt(LocalDateTime.now());
        log.info("Message: " + inactiveCardException.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<Error> insufficientBalanceException(InsufficientBalanceException insufficientBalanceException){
        Error error = new Error();
        error.setMessage(insufficientBalanceException.getMessage());
        error.setStatus(HttpStatus.BAD_REQUEST);
        error.setCreatedAt(LocalDateTime.now());
        log.info("Message: " + insufficientBalanceException.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(DuplicateRequestException.class)
    public ResponseEntity<Error> duplicateRequestException(DuplicateRequestException duplicateRequestException){
        Error error = new Error();
        error.setMessage(duplicateRequestException.getMessage());
        error.setStatus(HttpStatus.BAD_REQUEST);
        error.setCreatedAt(LocalDateTime.now());
        log.info("Message: " + duplicateRequestException.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Error> handleValidationException(
            MethodArgumentNotValidException methodArgumentNotValidException) {

        Error error = new Error();
        error.setMessage(methodArgumentNotValidException.getMessage());
        error.setStatus(HttpStatus.BAD_REQUEST);
        error.setCreatedAt(LocalDateTime.now());
        log.info("Message: " + methodArgumentNotValidException.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ConcurrentUpdateException.class)
    public ResponseEntity<Error> concurrentUpdateException(
            ConcurrentUpdateException concurrentUpdateException) {

        Error error = new Error();
        error.setMessage(concurrentUpdateException.getMessage());
        error.setStatus(HttpStatus.BAD_REQUEST);
        error.setCreatedAt(LocalDateTime.now());
        log.info("Message: " + concurrentUpdateException.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

}
