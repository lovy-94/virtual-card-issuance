package com.example.VirtualCardIssuance.exception;

public class NegativeAmountException extends RuntimeException{

    public NegativeAmountException(String ex){
        super(ex);
    }
}
