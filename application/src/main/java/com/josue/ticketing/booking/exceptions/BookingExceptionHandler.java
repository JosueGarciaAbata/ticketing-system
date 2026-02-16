package com.josue.ticketing.booking.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BookingExceptionHandler {

  @ExceptionHandler(BookingNotFoundException.class)
  public ProblemDetail handleBookingNotFound(BookingNotFoundException ex) {
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    pd.setTitle("Booking not found");
    return pd;
  }

  @ExceptionHandler(SeatsAlreadyHeldException.class)
  public ProblemDetail handleSeatsAlreadyHeld(SeatsAlreadyHeldException ex) {
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    pd.setTitle("Seats already held");
    return pd;
  }

  @ExceptionHandler(NoAvailableSeatsException.class)
  public ProblemDetail handleNoAvailableSeats(NoAvailableSeatsException ex) {
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    pd.setTitle("No available seats");
    return pd;
  }
}
