package com.josue.ticketing.catalog.venues.exceps;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class VenueExceptionHandler {

  @ExceptionHandler(VenueNotFoundException.class)
  public ProblemDetail handleVenueNotFound(VenueNotFoundException ex) {
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    pd.setTitle("Venue not found");
    return pd;
  }

  @ExceptionHandler(VenueHasDependenciesException.class)
  public ProblemDetail handleVenueHasDependencies(VenueHasDependenciesException ex) {
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    pd.setTitle("Venue has dependencies");
    return pd;
  }

  @ExceptionHandler(VenueScheduleConflictException.class)
  public ProblemDetail handleVenueScheduleConflict(VenueScheduleConflictException ex) {
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    pd.setTitle("Venue schedule conflict");
    return pd;
  }

  @ExceptionHandler(VenueCapacityBelowShowCapacityException.class)
  public ProblemDetail handleVenueCapacityBelowShowCapacity(VenueCapacityBelowShowCapacityException ex) {
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    pd.setTitle("Venue capacity below show capacity");
    return pd;
  }
}
