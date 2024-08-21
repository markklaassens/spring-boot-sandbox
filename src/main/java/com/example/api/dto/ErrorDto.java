package com.example.api.dto;

import java.util.List;

public record ErrorDto(List<String> message, Integer statusCode) {

  public ErrorDto(final String message, final Integer statusCode) {
    this(List.of(message), statusCode);
  }
}
