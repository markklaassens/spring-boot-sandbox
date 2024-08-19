package com.example.api.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import lombok.val;
import org.junit.jupiter.api.Test;

/**
 * ErrorDto is tested separately since it has an extra constructor for dealing with single validation messages.
 */
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

  @Test
  void shouldConstructErrorDtoWithMultipleMessages() {
    val errorDto = new ErrorDto(
        List.of("Error message", "Error message2"),
        1
    );
    assertEquals("Error message", errorDto.message().get(0));
    assertEquals("Error message2", errorDto.message().get(1));
    assertEquals(1, errorDto.statusCode());
  }
}
