package com.josue.ticketing.payment.services;

import com.josue.ticketing.booking.entities.Booking;
import com.josue.ticketing.booking.enums.BookingStatus;
import com.josue.ticketing.booking.exps.BookingNotFoundException;
import com.josue.ticketing.booking.repos.BookingRepository;
import com.josue.ticketing.booking.services.BookingService;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StripeWebhookServiceImpl implements StripeWebhookService {

    private final BookingService bookingService;
    private final BookingRepository bookingRepository;

    @Transactional(readOnly = true)
    @Override
    public void handleExpiredBooking(Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getData().getObject();
        String bookingPublicId = paymentIntent.getMetadata().get("bookingPublicId");
        Booking booking = bookingRepository.findByPublicId(UUID.fromString(bookingPublicId))
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));

        if (booking.getStatus().equals(BookingStatus.CANCELED)) {
            throw new IllegalStateException("La reserva expiro.");
        }
    }

    @Transactional(readOnly = false)
    @Override
    public void handlePaymentIntentSucecceded(Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getData().getObject();
        String bookingPublicId = paymentIntent.getMetadata().get("bookingPublicId");
        bookingService.confirm(UUID.fromString(bookingPublicId));
    }

    @Transactional(readOnly = false)
    @Override
    public void handlePaymentFailed(Event event) {
        // TODO: Hacer algo si el pago falla (por ejemplo lanzar una excepcion al usuario)
    }
}
