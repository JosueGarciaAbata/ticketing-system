package com.josue.ticketing.provider.stripe.webhook;

import com.stripe.model.Event;

/**
 * Servicio para procesar eventos webhook de Stripe.
 */
public interface StripeWebhookService {

    /**
     * Procesa el evento de intento de pago exitoso.
     * 
     * @param event evento de Stripe con datos del pago
     */
    void handlePaymentIntentSucecceded(Event event);
}
