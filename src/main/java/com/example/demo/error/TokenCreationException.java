package com.example.demo.error;

public class TokenCreationException extends RuntimeException {
  public TokenCreationException(String message) {
    super(message);
  }
}
