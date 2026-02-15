package com.josue.ticketing.catalog.cities.exceptions;

public class CityAlreadyExistsException extends RuntimeException {

    public  CityAlreadyExistsException(String message) {
        super(message);
    }

}
