package com.josue.ticketing.catalog.event.exceps;

public class EventHasDependenciesException extends RuntimeException {

    public  EventHasDependenciesException(String message) {
        super(message);
    }

}
