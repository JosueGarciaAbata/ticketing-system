package com.josue.ticketing.catalog.shows.exps;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ShowExceptionHandler {

  @ExceptionHandler(ShowNotFoundException.class)
  public ProblemDetail handleShowNotFound(ShowNotFoundException ex) {
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    pd.setTitle("Show not found");
    return pd;
  }

  @ExceptionHandler(InsufficientVenueCapacityException.class)
  public ProblemDetail handleInsufficientVenueCapacity(InsufficientVenueCapacityException ex) {
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    pd.setTitle("Insufficient venue capacity");
    return pd;
  }

  @ExceptionHandler(ShowHasBookingException.class)
  public ProblemDetail handleShowHasBooking(ShowHasBookingException ex) {
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    pd.setTitle("Show has bookings");
    return pd;
  }
}
