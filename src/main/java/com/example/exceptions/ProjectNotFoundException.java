package com.example.exceptions;

public class ProjectNotFoundException extends RuntimeException {

  public ProjectNotFoundException(final String msg) {
    super(msg);
  }
}
