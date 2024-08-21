package com.example.exceptions;

public class UserRoleNotFoundException extends RuntimeException {

  public UserRoleNotFoundException(final String msg) {
    super(msg);
  }
}
