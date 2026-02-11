package com.josue.ticketing.catalog.seat.exceps;

public class SeatNotFoundException extends RuntimeException {

    public SeatNotFoundException(String message) {
        super(message);
    }

}
