package com.josue.ticketing.catalog.venues.exceps;

public class VenueCapacityBelowShowCapacityException extends RuntimeException {

    public  VenueCapacityBelowShowCapacityException(String message) {
        super(message);
    }

}
