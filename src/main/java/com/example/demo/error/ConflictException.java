package com.example.demo.error;

public class ConflictException extends RuntimeException {
  public ConflictException(String message) {
    super(message);
  }
}
