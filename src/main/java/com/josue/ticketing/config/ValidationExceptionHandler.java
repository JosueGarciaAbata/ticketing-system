package com.josue.ticketing.config;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ValidationExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
    ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    pd.setTitle("Validation failed");

    List<Map<String, Object>> errors = ex.getBindingResult().getFieldErrors().stream()
        .map(this::toError)
        .toList();
    pd.setProperty("errors", errors);
    return pd;
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    pd.setTitle("Constraint violation");
    pd.setProperty(
        "errors",
        ex.getConstraintViolations().stream()
            .map(v -> Map.of(
                "path", String.valueOf(v.getPropertyPath()),
                "message", v.getMessage()))
            .toList());
    return pd;
  }

  private Map<String, Object> toError(FieldError fieldError) {
    Map<String, Object> error = new LinkedHashMap<>();
    error.put("field", fieldError.getField());
    error.put("message", fieldError.getDefaultMessage());
    Object rejected = fieldError.getRejectedValue();
    if (rejected != null) {
      error.put("rejectedValue", rejected);
    }
    return error;
  }
}
