package com.josue.ticketing.catalog.city.exceps;

public class CityNotFoundException extends RuntimeException{

    public CityNotFoundException(String message) {
        super(message);
    }

}
