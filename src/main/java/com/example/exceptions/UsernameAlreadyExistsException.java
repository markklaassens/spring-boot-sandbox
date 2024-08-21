package com.example.exceptions;

public class UsernameAlreadyExistsException extends RuntimeException {

  public UsernameAlreadyExistsException(final String msg) {
    super(msg);
  }
}
