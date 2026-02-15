package com.josue.ticketing.catalog.venues.exceps;

public class VenueNotFoundException extends RuntimeException {

    public  VenueNotFoundException(String message) {
        super(message);
    }

}
