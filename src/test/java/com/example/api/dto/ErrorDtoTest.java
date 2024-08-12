package com.example.api.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import lombok.val;
import org.junit.jupiter.api.Test;

public class ErrorDtoTest {

  @Test
  void shouldConstructErrorDtoWithSingleMessage() {
    val errorDto = new ErrorDto(
        "Error message",
        1
    );
    assertEquals("Error message", errorDto.message().get(0));
    assertEquals(1, errorDto.statusCode());
  }
}
