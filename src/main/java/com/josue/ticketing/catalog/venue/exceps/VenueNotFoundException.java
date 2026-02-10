package com.josue.ticketing.catalog.venue.exceps;

public class VenueNotFoundException extends RuntimeException {

    public  VenueNotFoundException(String message) {
        super(message);
    }

}
