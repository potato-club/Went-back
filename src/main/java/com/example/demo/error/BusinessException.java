package com.example.demo.error;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
  private final ErrorCode errorCode;

  public BusinessException(String message, ErrorCode errorCode) {
    super(message);
    this.errorCode = errorCode;
  }
}