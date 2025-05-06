package com.example.demo.error;

public class TokenCreationException extends BusinessException {
    public TokenCreationException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}