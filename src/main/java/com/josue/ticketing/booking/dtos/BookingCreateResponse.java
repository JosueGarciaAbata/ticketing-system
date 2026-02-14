package com.josue.ticketing.booking.dtos;

import com.josue.ticketing.booking.enums.BookingStatus;

import java.util.UUID;

public record BookingCreateResponse(UUID bookingPublicId, Integer showId, BookingStatus bookingStatus) {
}
