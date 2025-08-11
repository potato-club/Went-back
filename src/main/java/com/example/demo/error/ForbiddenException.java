package com.example.demo.error;

public class ForbiddenException extends BusinessException {
    public ForbiddenException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}