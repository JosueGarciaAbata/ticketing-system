package com.josue.ticketing.catalog.cities.exceptions;

public class CityNotFoundException extends RuntimeException{

    public CityNotFoundException(String message) {
        super(message);
    }

}
