package com.josue.ticketing.catalog.cities.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CityExceptionHandler {

    @ExceptionHandler(CityNotFoundException.class)
    public ProblemDetail handleCityNotFound(CityNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setTitle("City not found");
        return pd;
    }

    @ExceptionHandler(CityAlreadyExistsException.class)
    public ProblemDetail handleCityAlreadyExists(CityAlreadyExistsException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setTitle("City already exists");
        return pd;
    }

    @ExceptionHandler(CityHasDependenciesException.class)
    public ProblemDetail handleCityHasDependencies(CityHasDependenciesException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setTitle("City has dependencies");
        return pd;
    }

}
