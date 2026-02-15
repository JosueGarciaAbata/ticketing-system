package com.josue.ticketing.catalog.shows.dtos;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.ZonedDateTime;

public record ShowWithSeatsCreateRequest(
    @NotNull @Positive Integer eventId,
    @NotNull @Positive Integer venueId,
    @NotNull @Positive Integer capacity,
    @NotNull @FutureOrPresent ZonedDateTime startTime,
    @NotNull @FutureOrPresent ZonedDateTime endTime) {

  @AssertTrue(message = "endTime must be after startTime")
  public boolean isTimeRangeValid() {
    return startTime != null && endTime != null && endTime.isAfter(startTime);
  }
}
