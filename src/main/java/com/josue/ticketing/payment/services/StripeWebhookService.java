package com.josue.ticketing.payment.services;

import com.stripe.model.Event;

public interface StripeWebhookService {

    void handleCheckoutSessionCompleted(Event event);
    void handlePaymentFailed(Event event);
}
