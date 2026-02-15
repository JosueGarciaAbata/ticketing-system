package com.josue.ticketing.catalog.seats.exceps;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SeatExceptionHandler {

  @ExceptionHandler(SeatNotFoundException.class)
  public ProblemDetail handleSeatNotFound(SeatNotFoundException ex) {
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    pd.setTitle("Seat not found");
    return pd;
  }
}
