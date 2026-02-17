package com.josue.ticketing.checkout.service;

import com.josue.ticketing.booking.dtos.BookingCreateResponse;
import com.josue.ticketing.booking.entities.Booking;
import com.josue.ticketing.booking.enums.BookingStatus;
import com.josue.ticketing.booking.exceptions.BookingNotFoundException;
import com.josue.ticketing.booking.repos.BookingRepository;
import com.josue.ticketing.booking.services.BookingService;
import com.josue.ticketing.checkout.dtos.CheckoutStatusResponse;
import com.josue.ticketing.checkout.dtos.CheckoutCreateRequest;
import com.josue.ticketing.checkout.exceptions.PaymentSessionException;
import com.josue.ticketing.config.AuthService;
import com.josue.ticketing.payment.dtos.BookingCreateRequest;
import com.josue.ticketing.provider.CheckoutSessionProvider;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;
import org.springframework.security.access.AccessDeniedException;

@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {

    private final BookingService bookingService;
    private final CheckoutSessionProvider checkoutSessionProvider;
    private final BookingRepository bookingRepository;
    private final AuthService authService;

    @Transactional()
    @Override
    public String startCheckout(CheckoutCreateRequest req) {
        BookingCreateRequest bookingCreateRequest = new BookingCreateRequest(req.showId(), req.seatsId());
        BookingCreateResponse response = bookingService.create(bookingCreateRequest);

        UUID bookingPublicId = response.bookingPublicId();
        String sessionUrl = checkoutSessionProvider.createCheckoutSessionUrl(bookingPublicId);

        return sessionUrl;
    }

    @Transactional()
    @Override
    public String startCheckoutDbOnly(CheckoutCreateRequest req) {
        BookingCreateRequest bookingCreateRequest = new BookingCreateRequest(req.showId(), req.seatsId());
        BookingCreateResponse response = bookingService.createDbOnly(bookingCreateRequest);

        UUID bookingPublicId = response.bookingPublicId();
        String sessionUrl = checkoutSessionProvider.createCheckoutSessionUrl(bookingPublicId);

        return sessionUrl;
    }

    @Override
    public CheckoutStatusResponse getCheckoutStatus(String sessionId) {
        Integer userId = authService.getUserId();
        Session session;
        try {
            session = Session.retrieve(sessionId);
        } catch (StripeException e) {
            throw new PaymentSessionException("No se pudo recuperar la sesion de Stripe.");
        }

        Map<String, String> metadata = session.getMetadata();

        if (metadata == null || metadata.get("userId") == null) {
            throw new IllegalStateException("La sesión de Stripe no contiene userId en metadata.");
        }

        if (!metadata.get("userId").equals(userId.toString())) {
            throw new AccessDeniedException("No tienes acceso para realizar esta operación.");
        }

        String bookingPublicId = metadata.get("bookingPublicId");

        if (bookingPublicId == null) {
            throw new IllegalStateException("No se pudo recuperar la booking publicId.");
        }

        Booking booking = bookingRepository
                .findByPublicId(UUID.fromString(bookingPublicId))
                .orElseThrow(() -> new BookingNotFoundException("Reserva no encontrada con id=" + bookingPublicId));

        String message = "";

        if (booking.getStatus() == BookingStatus.CANCELED) {
            message = "Reserva expirada. El pago no se concreto.";
        }

        String paymentStatus = session.getPaymentStatus();
        if ("paid".equalsIgnoreCase(paymentStatus)) {
            message = "Pago realizado con exito.";
        }

        return new CheckoutStatusResponse(session.getStatus(), message);
    }

    @Override
    public void expireCheckoutSession(String sessionId) {
        Session session;
        try {
            session = Session.retrieve(sessionId);
        } catch (StripeException e) {
            throw new PaymentSessionException("No se pudo recuperar la sesion de Stripe.");
        }

        Map<String, String> metadata = session.getMetadata();
        Integer userId = authService.getUserId();

        if (metadata == null || metadata.get("userId") == null) {
            throw new IllegalStateException("La sesión de Stripe no contiene userId en metadata.");
        }

        if (!metadata.get("userId").equals(userId.toString())) {
            throw new AccessDeniedException("No tienes acceso para realizar esta operación.");
        }

        try {
            session.expire();
        } catch (StripeException e) {
            throw new PaymentSessionException("No se pudo expirar la sesion de Stripe.");
        }

        System.out.println("Session expired: " + session.getStatus());
    }
}
