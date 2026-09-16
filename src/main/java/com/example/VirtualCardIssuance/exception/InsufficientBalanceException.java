package com.example.VirtualCardIssuance.exception;



public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String ex){
        super(ex);
    }
}
