package com.example.demo.error;

public class InvalidTokenException extends BusinessException {
    public InvalidTokenException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}