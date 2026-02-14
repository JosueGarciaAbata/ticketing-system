package com.josue.ticketing.booking.services;

import com.josue.ticketing.booking.dtos.BookingCreateResponse;
import com.josue.ticketing.payment.dtos.BookingCreateRequest;

public interface BookingService {

    BookingCreateResponse create(BookingCreateRequest bookingCreateRequest);
    void confirm();
    void cancel();

}
