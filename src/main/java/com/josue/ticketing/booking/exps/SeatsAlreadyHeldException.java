package com.josue.ticketing.booking.exps;

public class SeatsAlreadyHeldException extends RuntimeException {

    public SeatsAlreadyHeldException(String message) {
        super(message);
    }

}
