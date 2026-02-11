package com.josue.ticketing.catalog.show.exps;

public class InsufficientVenueCapacityException extends RuntimeException {

    public InsufficientVenueCapacityException(String message) {
        super(message);
    }

}
