package com.example.VirtualCardIssuance.exception;

public class ConcurrentUpdateException extends RuntimeException {
    public ConcurrentUpdateException(String s) {
        super(s);
    }
}
