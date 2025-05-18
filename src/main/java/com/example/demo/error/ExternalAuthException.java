package com.example.demo.error;

public class ExternalAuthException extends RuntimeException {
  public ExternalAuthException(String message) {
    super(message);
  }
}
