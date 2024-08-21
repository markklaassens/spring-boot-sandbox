package com.example.exceptions;

public class ProjectAlreadyExistsException extends RuntimeException {

  public ProjectAlreadyExistsException(final String msg) {
    super(msg);
  }
}
