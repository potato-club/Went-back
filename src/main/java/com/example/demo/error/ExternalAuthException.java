package com.example.demo.error;

public class ExternalAuthException extends BusinessException {
    public ExternalAuthException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
