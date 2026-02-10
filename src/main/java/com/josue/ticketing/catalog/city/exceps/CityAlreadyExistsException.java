package com.josue.ticketing.catalog.city.exceps;

public class CityAlreadyExistsException extends RuntimeException {

    public  CityAlreadyExistsException(String message) {
        super(message);
    }

}
