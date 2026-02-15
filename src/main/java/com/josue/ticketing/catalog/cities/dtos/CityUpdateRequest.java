package com.josue.ticketing.catalog.cities.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CityUpdateRequest(
    @NotBlank @Size(max = 100) String name,
    @NotBlank @Size(max = 100) String country,
    @NotBlank @Size(max = 50) String timezone) {
}
