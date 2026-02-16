package com.josue.ticketing.catalog.seats.exceps;

public class SeatNotFoundException extends RuntimeException {

    public SeatNotFoundException(String message) {
        super(message);
    }

}
