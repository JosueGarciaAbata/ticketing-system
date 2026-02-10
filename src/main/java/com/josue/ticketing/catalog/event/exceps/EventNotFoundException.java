package com.josue.ticketing.catalog.event.exceps;

public class EventNotFoundException extends RuntimeException {

    public EventNotFoundException(String message) {
        super(message);
    }

}
