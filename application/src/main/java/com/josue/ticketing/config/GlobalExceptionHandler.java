package com.josue.ticketing.config;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Maneja excepciones de argumento ilegal.
   * 
   * @param ex excepción capturada
   * @return detalle del problema con estado HTTP 400
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    pd.setTitle("Bad request");
    return pd;
  }

  /**
   * Maneja excepciones de estado ilegal.
   * 
   * @param ex excepción capturada
   * @return detalle del problema con estado HTTP 409
   */
  @ExceptionHandler(IllegalStateException.class)
  public ProblemDetail handleIllegalState(IllegalStateException ex) {
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    pd.setTitle("Invalid state");
    return pd;
  }

  /**
   * Maneja excepciones de violación de integridad de datos.
   * 
   * @param ex excepción capturada
   * @return detalle del problema con estado HTTP 409
   */
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ProblemDetail handleDataIntegrityViolation(DataIntegrityViolationException ex) {
    String detail = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, detail);
    pd.setTitle("Data integrity violation");
    return pd;
  }

  /**
   * Maneja excepciones de acceso denegado.
   * 
   * @param ex excepción capturada
   * @return detalle del problema con estado HTTP 403
   */
  @ExceptionHandler(AccessDeniedException.class)
  public ProblemDetail handleAccessDenied(AccessDeniedException ex) {
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
    pd.setTitle("Access denied");
    return pd;
  }
}
