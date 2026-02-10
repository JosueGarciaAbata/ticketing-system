package com.josue.ticketing.catalog.venue.dtos;

public record VenueCreateRequest(Integer cityId, String name, Integer capacity) {
}
