package com.josue.ticketing.catalog.shows.dtos;

import com.josue.ticketing.catalog.shows.enums.ShowStatus;

import java.time.ZonedDateTime;

public record ShowResponse(Integer id, Integer eventId, Integer venueId, Integer capacity, ZonedDateTime startTime, ZonedDateTime endTime,
                           ShowStatus status) {
}
