package com.example.VirtualCardIssuance.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Error {

    private String message;
    private LocalDateTime createdAt;
    private HttpStatus status;
}
