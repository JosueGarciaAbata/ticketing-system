package com.josue.ticketing.booking.exceptions;

public class SeatsAlreadyHeldException extends RuntimeException {

    public SeatsAlreadyHeldException(String message) {
        super(message);
    }

}
