package com.example.exceptions;

public class UsernameAlreadyExistsException extends RuntimeException {

  public UsernameAlreadyExistsException(String msg) {
    super(msg);
  }
}
