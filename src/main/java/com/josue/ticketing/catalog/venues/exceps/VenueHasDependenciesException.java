package com.josue.ticketing.catalog.venues.exceps;

public class VenueHasDependenciesException extends RuntimeException {

    public  VenueHasDependenciesException(String message) {
        super(message);
    }
}
