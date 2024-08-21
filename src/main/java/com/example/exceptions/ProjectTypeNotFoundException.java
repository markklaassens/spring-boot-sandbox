package com.example.exceptions;

public class ProjectTypeNotFoundException extends RuntimeException {

  public ProjectTypeNotFoundException(final String msg) {
    super(msg);
  }
}
