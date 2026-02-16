package com.josue.ticketing.catalog.seats.dtos;

import com.josue.ticketing.catalog.seats.enums.SeatCategory;
import com.josue.ticketing.catalog.seats.enums.SeatStatus;

import java.math.BigDecimal;

public record SeatResponse(Integer id, Integer showId, String seatNumber, SeatCategory category, SeatStatus status, BigDecimal price) {
}
