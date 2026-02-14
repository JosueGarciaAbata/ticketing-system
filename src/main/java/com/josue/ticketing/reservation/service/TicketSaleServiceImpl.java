package com.josue.ticketing.reservation.service;

import com.josue.ticketing.booking.dtos.BookingCreateResponse;
import com.josue.ticketing.booking.services.BookingService;
import com.josue.ticketing.payment.dtos.BookingCreateRequest;
import com.josue.ticketing.payment.services.PaymentService;
import com.josue.ticketing.reservation.dtos.TicketCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketSaleServiceImpl implements TicketSaleService {

    private final BookingService bookingService;
    private final PaymentService paymentService;

    @Transactional()
    @Override
    public String reservate(TicketCreateRequest req) throws Exception {
        BookingCreateRequest bookingCreateRequest = new BookingCreateRequest(req.showId(), req.seatsId());
        BookingCreateResponse response = bookingService.create(bookingCreateRequest);

        UUID bookingPublicId = response.bookingPublicId();
        String sessionUrl = paymentService.createCheckoutSession(bookingPublicId);

        return sessionUrl;
    }
}
