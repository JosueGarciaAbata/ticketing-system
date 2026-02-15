package com.josue.ticketing.catalog.shows.dtos;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;

public record ShowUpdateRequest(
    @NotNull @FutureOrPresent ZonedDateTime startTime,
    @NotNull @FutureOrPresent ZonedDateTime endTime) {

  @AssertTrue(message = "endTime must be after startTime")
  public boolean isTimeRangeValid() {
    return startTime != null && endTime != null && endTime.isAfter(startTime);
  }
}
