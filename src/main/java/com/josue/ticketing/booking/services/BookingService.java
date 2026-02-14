package com.josue.ticketing.booking.services;

import com.josue.ticketing.booking.dtos.BookingCreateResponse;
import com.josue.ticketing.payment.dtos.BookingCreateRequest;

import java.util.UUID;

public interface BookingService {

    BookingCreateResponse create(BookingCreateRequest bookingCreateRequest);
    void confirm(UUID publicId);
    void cancel(UUID publicId, String reason);
    void expire(UUID publicId);

}
