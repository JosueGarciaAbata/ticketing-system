package com.josue.ticketing.booking.exps;

public class NoAvailableSeatsException extends RuntimeException {

    public NoAvailableSeatsException(String message) {
        super(message);
    }

}
