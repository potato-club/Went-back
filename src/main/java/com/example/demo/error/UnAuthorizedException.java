package com.example.demo.error;

public class UnAuthorizedException extends BusinessException {
  public UnAuthorizedException(String message, ErrorCode errorCode) {
    super(message, errorCode);
  }
}