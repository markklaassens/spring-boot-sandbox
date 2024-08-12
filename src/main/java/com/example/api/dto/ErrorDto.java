package com.example.api.dto;

import java.util.List;

public record ErrorDto(List<String> message, Integer statusCode) {

  public ErrorDto(String message, Integer statusCode) {
    this(List.of(message), statusCode);
  }
}
