package com.example.exceptions;

public class UserRoleNotFoundException extends RuntimeException {

  public UserRoleNotFoundException(String msg) {
    super(msg);
  }
}
