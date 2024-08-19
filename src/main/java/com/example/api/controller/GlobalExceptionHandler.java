package com.example.api.controller;

import com.example.api.dto.ErrorDto;
import com.example.exceptions.NotCreatorOfProjectException;
import com.example.exceptions.ProjectAlreadyExistsException;
import com.example.exceptions.ProjectNotFoundException;
import com.example.exceptions.ProjectTypeNotFoundException;
import com.example.exceptions.UserNotFoundException;
import com.example.exceptions.UserRoleNotFoundException;
import com.example.exceptions.UsernameAlreadyExistsException;
import java.util.Comparator;
import lombok.val;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
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
   * Handles bad request exceptions and returns a 400 error response with corresponding message.
   *
   * @param exception the exception that occurred
   * @return a 400 {@link ResponseEntity} with error details
   */
  @ExceptionHandler({
      ProjectAlreadyExistsException.class,
      ProjectTypeNotFoundException.class,
      NotCreatorOfProjectException.class,
      UsernameAlreadyExistsException.class
  })
  public ResponseEntity<ErrorDto> handleBadRequest(RuntimeException exception) {
    val error = new ErrorDto(exception.getMessage(), HttpStatus.BAD_REQUEST.value());

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .contentType(MediaType.APPLICATION_JSON)
        .body(error);
  }

  /**
   * Handles not found exceptions and returns a 404 error with corresponding error response.
   *
   * @param exception the RuntimeException thrown when a user is not found
   * @return a ResponseEntity containing an ErrorDto with the error details
   */
  @ExceptionHandler({
      ProjectNotFoundException.class,
      UserNotFoundException.class,
      UserRoleNotFoundException.class
  })
  public ResponseEntity<ErrorDto> handleNotFound(RuntimeException exception) {
    val error = new ErrorDto(exception.getMessage(), HttpStatus.NOT_FOUND.value());

    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .contentType(MediaType.APPLICATION_JSON)
        .body(error);
  }

  /**
   * Handles transaction-related exceptions.
   *
   * @param exception the caught exception
   * @return a ResponseEntity with error details and conflict status
   */
  @ExceptionHandler({DataIntegrityViolationException.class, ObjectOptimisticLockingFailureException.class})
  public ResponseEntity<ErrorDto> handleTransactionExceptions(Exception exception) {
    val error = new ErrorDto(exception.getMessage(), HttpStatus.CONFLICT.value());

    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .contentType(MediaType.APPLICATION_JSON)
        .body(error);
  }

  /**
   * Handles access denied exceptions (403 Forbidden).
   *
   * @param exception the caught AccessDeniedException
   * @return a ResponseEntity with error details and forbidden status
   */
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorDto> handleAccessDeniedException(AccessDeniedException exception) {
    val error = new ErrorDto(exception.getMessage(), HttpStatus.FORBIDDEN.value());

    return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .contentType(MediaType.APPLICATION_JSON)
        .body(error);
  }

  /**
   * Handles unexpected exceptions.
   *
   * @param exception the caught exception
   * @return a ResponseEntity with error details and internal server error status
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorDto> handleGeneralException(Exception exception) {
    val error = new ErrorDto("An unexpected error occurred: " + exception.getMessage(),
        HttpStatus.INTERNAL_SERVER_ERROR.value());

    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .contentType(MediaType.APPLICATION_JSON)
        .body(error);
  }
}
