package com.example.demo.error;

// ErrorCode와 함께 던지는 구조
// 비검사 예외로 처리하기 위해 RuntimeException 상속받기!

import lombok.Getter;
@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;

    public BusinessException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
