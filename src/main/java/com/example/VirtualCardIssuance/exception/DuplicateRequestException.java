package com.example.VirtualCardIssuance.exception;

public class DuplicateRequestException extends RuntimeException {
    public DuplicateRequestException(String duplicateRequest) {
        super(duplicateRequest);
    }
}
