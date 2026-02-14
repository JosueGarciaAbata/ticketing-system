package com.josue.ticketing.payment.controllers;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.logging.Logger;

@RestController
public class StripeWebhookController {

    private final Logger logger = Logger.getLogger(StripeWebhookController.class.getName());

    @Value("${STRIPE_WEBHOOK_SECRET}")
    private String endpointSecret;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(HttpServletRequest request) throws Exception {
        String payload;
        try {
            payload = new String(request.getInputStream().readAllBytes());
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Cannot read payload");
        }
        String sigHeader = request.getHeader("Stripe-Signature");

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.badRequest().body("Invalid signature");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Webhook error");
        }

        // Procesar evento
        switch (event.getType()) {

            case "checkout.session.completed":
                handleCheckoutCompleted(event);
                break;

            case "payment_intent.payment_failed":
                handlePaymentFailed(event);
                break;

            default:
                // ignorar otros
        }

        return ResponseEntity.ok("Received");
    }

    private void handlePaymentFailed(Event event) {
        logger.info("Payment failed");
    }

    private void handleCheckoutCompleted(Event event) {
        logger.info("Checkout completed");
    }


}
