package com.josue.ticketing.catalog.events.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class EventExceptionHandler {

  @ExceptionHandler(EventNotFoundException.class)
  public ProblemDetail handleEventNotFound(EventNotFoundException ex) {
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    pd.setTitle("Event not found");
    return pd;
  }

  @ExceptionHandler(EventHasDependenciesException.class)
  public ProblemDetail handleEventHasDependencies(EventHasDependenciesException ex) {
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    pd.setTitle("Event has dependencies");
    return pd;
  }
}
