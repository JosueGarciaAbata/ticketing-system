package com.josue.ticketing.catalog.venue.exceps;

public class VenueScheduleConflictException extends RuntimeException {

    public VenueScheduleConflictException(String message) {
        super(message);
    }

}
