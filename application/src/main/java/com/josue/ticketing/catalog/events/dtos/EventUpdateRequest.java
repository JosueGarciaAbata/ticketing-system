package com.josue.ticketing.catalog.events.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record EventUpdateRequest(
    @NotBlank @Size(max = 255) String title,
    @Size(max = 5000) String description,
    @NotBlank @Size(max = 50) String category,
    @NotNull @Positive Integer durationMinutes) {
}
