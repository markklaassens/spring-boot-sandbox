package com.example.exceptions;

public class ProjectTypeNotFoundException extends RuntimeException {

  public ProjectTypeNotFoundException(String msg) {
    super(msg);
  }
}
