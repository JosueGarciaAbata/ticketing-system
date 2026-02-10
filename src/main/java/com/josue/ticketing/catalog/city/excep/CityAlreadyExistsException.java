package com.josue.ticketing.catalog.city.excep;

public class CityAlreadyExistsException extends RuntimeException {

    public  CityAlreadyExistsException(String message) {
        super(message);
    }

}
