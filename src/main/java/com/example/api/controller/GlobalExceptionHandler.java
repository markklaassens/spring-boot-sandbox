package com.example.api.controller;

import com.example.api.dto.ErrorDto;
import com.example.exceptions.ProjectAlreadyExistsException;
import com.example.exceptions.ProjectTypeNotFoundException;
import java.util.Comparator;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Handles {@link MethodArgumentNotValidException} and returns a 400 error with corresponding message(s).
   *
   * @param exception the validation exception
   * @return a 400 {@link ResponseEntity} with error details
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorDto> handleValidationErrors(
      MethodArgumentNotValidException exception) {

    val messages = exception.getBindingResult().getFieldErrors()
        .stream()
        .map(FieldError::getDefaultMessage)
        .sorted(Comparator.nullsFirst(Comparator.naturalOrder()))
        .toList();
    val error = new ErrorDto(messages, HttpStatus.BAD_REQUEST.value());

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .contentType(MediaType.APPLICATION_JSON)
        .body(error);
  }

  /**
   * Handles project-related exceptions and returns a 400 error response with corresponding message.
   *
   * @param exception the exception that occurred
   * @return a 400 {@link ResponseEntity} with error details
   */
  @ExceptionHandler({
      ProjectAlreadyExistsException.class,
      ProjectTypeNotFoundException.class
  })
  public ResponseEntity<ErrorDto> handleBadRequest(RuntimeException exception) {
    val error = new ErrorDto(exception.getMessage(), HttpStatus.BAD_REQUEST.value());

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .contentType(MediaType.APPLICATION_JSON)
        .body(error);
  }
}
