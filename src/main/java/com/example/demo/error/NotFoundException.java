package com.example.demo.error;

public class NotFoundException extends BusinessException {
    public NotFoundException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
