package com.josue.ticketing.payment.services;

import com.josue.ticketing.booking.services.BookingService;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StripeWebhookServiceImpl implements StripeWebhookService {

    private final BookingService bookingService;

    @Override
    public void handleCheckoutSessionCompleted(Event event) {
        Session session = (Session) event.getData().getObject();
        String bookingPublicId = session.getMetadata().get("bookingPublicId");
        bookingService.confirm(UUID.fromString(bookingPublicId));
    }

    @Override
    public void handlePaymentFailed(Event event) {
        Session session = (Session) event.getData().getObject();
        String bookingPublicId = session.getMetadata().get("bookingPublicId");
        bookingService.cancel(UUID.fromString(bookingPublicId), "payment_failed");
    }
}
