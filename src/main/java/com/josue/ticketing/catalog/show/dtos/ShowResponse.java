package com.josue.ticketing.catalog.show.dtos;

import java.time.ZonedDateTime;

public record ShowResponse(Integer id, Integer eventId, Integer venueId, Integer capacity, ZonedDateTime startTime, ZonedDateTime endTime) {
}
