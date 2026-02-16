package com.josue.ticketing.catalog.events.exceptions;

public class EventHasDependenciesException extends RuntimeException {

    public  EventHasDependenciesException(String message) {
        super(message);
    }

}
