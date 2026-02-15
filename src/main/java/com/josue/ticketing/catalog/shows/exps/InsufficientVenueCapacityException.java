package com.josue.ticketing.catalog.shows.exps;

public class InsufficientVenueCapacityException extends RuntimeException {

    public InsufficientVenueCapacityException(String message) {
        super(message);
    }

}
