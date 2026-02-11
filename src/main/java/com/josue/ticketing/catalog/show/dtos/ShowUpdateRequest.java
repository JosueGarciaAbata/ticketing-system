package com.josue.ticketing.catalog.show.dtos;

import java.time.ZonedDateTime;

public record ShowUpdateRequest(ZonedDateTime startTime, ZonedDateTime endTime) {
}
