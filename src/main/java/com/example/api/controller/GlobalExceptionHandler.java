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
   * Handles validation errors triggered by {@link MethodArgumentNotValidException}, which is typically thrown when a method
   * argument annotated with {@code @Valid} fails validation.
   *
   * <p>This method extracts the validation error messages from the exception, sorts them, and encapsulates
   * them in an {@link ErrorDto} object, along with the appropriate HTTP status code.
   *
   * <p>The response is returned as a {@link ResponseEntity} with a status of {@code 400 BAD REQUEST} and
   * the content type set to {@code application/json}.
   *
   * @param exception the {@link MethodArgumentNotValidException} that contains information about the validation errors.
   * @return a {@link ResponseEntity} containing an {@link ErrorDto} with the list of error messages and a 400 status code.
   * @see MethodArgumentNotValidException
   * @see ErrorDto
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
   * Handles exceptions related to project creation and type identification, specifically
   * {@link ProjectAlreadyExistsException} and {@link ProjectTypeNotFoundException}.
   *
   * <p>This method captures the exception message and wraps it in an {@link ErrorDto} object,
   * along with the HTTP status code {@code 400 BAD REQUEST}.
   *
   * <p>The response is returned as a {@link ResponseEntity} with a status of {@code 400 BAD REQUEST}
   * and the content type set to {@code application/json}.
   *
   * @param exception the {@link RuntimeException} that triggered this handler, expected to be either
   *                  {@link ProjectAlreadyExistsException} or {@link ProjectTypeNotFoundException}.
   * @return a {@link ResponseEntity} containing an {@link ErrorDto} with the error message and a 400 status code.
   * @see ProjectAlreadyExistsException
   * @see ProjectTypeNotFoundException
   * @see ErrorDto
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
