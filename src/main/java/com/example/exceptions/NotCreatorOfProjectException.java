package com.example.exceptions;

public class NotCreatorOfProjectException extends RuntimeException {

  public NotCreatorOfProjectException(String msg) {
    super(msg);
  }
}
