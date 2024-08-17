package com.example.exceptions;

public class ProjectNotFoundException extends RuntimeException {

  public ProjectNotFoundException(String msg) {
    super(msg);
  }
}
