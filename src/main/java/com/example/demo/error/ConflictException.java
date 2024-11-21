package com.example.demo.error;

public class ConflictException extends BusinessException {
    public ConflictException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
