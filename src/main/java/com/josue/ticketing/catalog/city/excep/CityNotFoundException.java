package com.josue.ticketing.catalog.city.excep;

public class CityNotFoundException extends RuntimeException{

    public CityNotFoundException(String message) {
        super(message);
    }

}
