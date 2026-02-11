package com.josue.ticketing.catalog.seat.dtos;

import com.josue.ticketing.catalog.seat.enums.SeatCategory;
import com.josue.ticketing.catalog.seat.enums.SeatStatus;

public record SeatResponse(Integer id, Integer showId, String seatNumber, SeatCategory category, SeatStatus status) {
}
