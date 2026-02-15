package com.josue.ticketing.booking.exceptions;

public class NoAvailableSeatsException extends RuntimeException {

    public NoAvailableSeatsException(String message) {
        super(message);
    }

}
