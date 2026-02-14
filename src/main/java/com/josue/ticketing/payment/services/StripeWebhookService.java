package com.josue.ticketing.payment.services;

import com.stripe.model.Event;

public interface StripeWebhookService {

    void handleExpiredBooking(Event event);
    void handlePaymentIntentSucecceded(Event event);
    void handlePaymentFailed(Event event);
}
