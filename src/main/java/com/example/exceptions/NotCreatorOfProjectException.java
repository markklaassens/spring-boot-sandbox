package com.example.exceptions;

public class NotCreatorOfProjectException extends RuntimeException {

  public NotCreatorOfProjectException(final String msg) {
    super(msg);
  }
}
