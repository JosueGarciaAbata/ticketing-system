package com.josue.ticketing.provider.stripe.webhook;

import com.stripe.model.Event;

public interface StripeWebhookService {

    void handlePaymentIntentSucecceded(Event event);
    void handlePaymentFailed(Event event);
}
