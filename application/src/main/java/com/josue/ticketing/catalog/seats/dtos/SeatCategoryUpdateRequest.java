package com.josue.ticketing.catalog.seats.dtos;

import com.josue.ticketing.catalog.seats.enums.SeatCategory;
import jakarta.validation.constraints.NotNull;

public record SeatCategoryUpdateRequest(@NotNull SeatCategory seatCategory) {
}
