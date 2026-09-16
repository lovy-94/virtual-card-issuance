package com.example.VirtualCardIssuance.exception;

public class CardNotFoundException extends RuntimeException{

    public CardNotFoundException(String ex){
       super(ex);
    }
}
