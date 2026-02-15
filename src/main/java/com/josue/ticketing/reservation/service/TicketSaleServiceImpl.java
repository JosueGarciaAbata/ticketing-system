package com.josue.ticketing.reservation.service;

import com.josue.ticketing.booking.dtos.BookingCreateResponse;
import com.josue.ticketing.booking.entities.Booking;
import com.josue.ticketing.booking.enums.BookingStatus;
import com.josue.ticketing.booking.exps.BookingNotFoundException;
import com.josue.ticketing.booking.repos.BookingRepository;
import com.josue.ticketing.booking.services.BookingService;
import com.josue.ticketing.payment.dtos.BookingCreateRequest;
import com.josue.ticketing.reservation.dtos.PaymentStatusResponse;
import com.josue.ticketing.reservation.exps.PaymentSessionException;
import com.josue.ticketing.payment.services.PaymentService;
import com.josue.ticketing.reservation.dtos.TicketCreateRequest;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketSaleServiceImpl implements TicketSaleService {

    private final BookingService bookingService;
    private final PaymentService paymentService;
    private final BookingRepository bookingRepository;

    @Transactional()
    @Override
    public String reservate(TicketCreateRequest req) throws Exception {
        BookingCreateRequest bookingCreateRequest = new BookingCreateRequest(req.showId(), req.seatsId());
        BookingCreateResponse response = bookingService.create(bookingCreateRequest);

        UUID bookingPublicId = response.bookingPublicId();
        String sessionUrl = paymentService.createCheckoutSession(bookingPublicId);

        return sessionUrl;
    }

    @Override
    public PaymentStatusResponse getPaymentStatusBySessionId(String sessionId) {

        Session session;
        try {
            session = Session.retrieve(sessionId);
        } catch (StripeException e) {
            throw new PaymentSessionException("No se pudo recuperar la sesion de Stripe.");
        }

        String paymentStatus = session.getPaymentStatus();
        String bookingPublicId = session.getMetadata().get("bookingPublicId");

        if (bookingPublicId == null) {
            throw new IllegalStateException("No se pudo recuperar la booking publicid.");
        }

        Booking booking = bookingRepository
                .findByPublicId(UUID.fromString(bookingPublicId))
                .orElseThrow(() -> new BookingNotFoundException("Reserva no encontrada con id=" + bookingPublicId));

        String message = "";

        if (booking.getStatus() == BookingStatus.CANCELED) {
            message = "Reserva expirada. El pago no se concreto.";
        }

        if ("paid".equalsIgnoreCase(paymentStatus)) {
            message = "Pago realizado con exito.";
        }

        return new PaymentStatusResponse(message);
    }

    @Override
    public void expireStripeSession(String sessionId) {
        Session session = null;
        try {
            session = Session.retrieve(sessionId);
            session.expire();
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Session expired: " + session.getStatus());
    }
}
