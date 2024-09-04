package com.example.api.dto;

import java.util.List;
import lombok.NonNull;

public record ErrorDto(@NonNull List<String> message, int statusCode) {

  public ErrorDto {
    message = List.copyOf(message);
  }

  public ErrorDto(@NonNull final String message, final int statusCode) {
    this(List.of(message), statusCode);
  }
}
