package com.josue.ticketing.catalog.shows.dtos;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record ShowWithSeatsCreateRequest(
    @NotNull @Positive Integer eventId,
    @NotNull @Positive Integer venueId,
    @NotNull @Positive Integer capacity,
    @NotNull @Positive @DecimalMin(value = "1.00") BigDecimal seatPrice,
    @NotNull @FutureOrPresent ZonedDateTime startTime,
    @NotNull @FutureOrPresent ZonedDateTime endTime) {

  @AssertTrue(message = "endTime must be after startTime")
  public boolean isTimeRangeValid() {
    return startTime != null && endTime != null && endTime.isAfter(startTime);
  }
}
