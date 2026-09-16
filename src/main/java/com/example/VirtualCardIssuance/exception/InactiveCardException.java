package com.example.VirtualCardIssuance.exception;

public class InactiveCardException extends RuntimeException{
    public InactiveCardException(String ex){
        super(ex);
    }
}
