package com.josue.ticketing.checkout.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CheckoutExceptionHandler {

  @ExceptionHandler(PaymentSessionException.class)
  public ProblemDetail handlePaymentSessionException(PaymentSessionException ex) {
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    pd.setTitle("Payment session error");
    return pd;
  }
}
