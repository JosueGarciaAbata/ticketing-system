package com.josue.ticketing.booking.dtos;

import com.josue.ticketing.booking.enums.BookingStatus;

public record BookingCreateResponse(Integer bookingId, Integer showId, BookingStatus bookingStatus) {
}
