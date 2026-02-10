package com.josue.ticketing.catalog.venue.exceps;

public class VenueCapacityBelowShowCapacityException extends RuntimeException {

    public  VenueCapacityBelowShowCapacityException(String message) {
        super(message);
    }

}
