package com.example.demo.error;

public class BadRequestException extends BusinessException {
    public BadRequestException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}