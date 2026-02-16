package com.josue.ticketing.catalog.venues.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record VenueUpdateRequest(
    @NotNull @Positive Integer cityId,
    @NotBlank @Size(max = 150) String name,
    @NotNull @Positive Integer capacity) {
}
