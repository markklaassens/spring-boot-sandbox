package com.example.exceptions;

public class ProjectAlreadyExistsException extends RuntimeException {

  public ProjectAlreadyExistsException(String msg) {
    super(msg);
  }
}
