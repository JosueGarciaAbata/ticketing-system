package com.josue.ticketing.catalog.venues.exceps;

public class VenueScheduleConflictException extends RuntimeException {

    public VenueScheduleConflictException(String message) {
        super(message);
    }

}
