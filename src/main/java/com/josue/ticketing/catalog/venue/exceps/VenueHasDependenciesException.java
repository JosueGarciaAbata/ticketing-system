package com.josue.ticketing.catalog.venue.exceps;

public class VenueHasDependenciesException extends RuntimeException {

    public  VenueHasDependenciesException(String message) {
        super(message);
    }
}
