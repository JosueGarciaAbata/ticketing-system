package com.josue.ticketing.payment.controllers;

import com.josue.ticketing.payment.services.StripeWebhookService;
import org.springframework.web.bind.annotation.RestController;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.util.logging.Logger;

@RestController
public class StripeWebhookController {

    private final Logger logger = Logger.getLogger(StripeWebhookController.class.getName());
    private StripeWebhookService stripeWebhookService;

    public StripeWebhookController(StripeWebhookService stripeWebhookService) {
        this.stripeWebhookService = stripeWebhookService;
    }

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
        logger.info("Payload: " + payload);
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
        stripeWebhookService.handlePaymentFailed(event);
    }

    private void handleCheckoutCompleted(Event event) {
        logger.info("Checkout completed");
        stripeWebhookService.handleCheckoutSessionCompleted(event);
    }

    /*
    "id": "evt_1T0a5MIeADuLKrJVoPKwvIl4",
  "object": "event",
  "api_version": "2026-01-28.clover",
  "created": 1771041840,
  "data": {
    "object": {
      "id": "cs_test_a1yLrQloLkXm5x0qx5KEC6GKDGrbLkztRnSL9qkIJyG7nmmEOKuBPEzr18",
      "object": "checkout.session", <--------------------------- IMPORTANTE!!!
      "adaptive_pricing": {
        "enabled": true
      },
      "after_expiration": null,
      "allow_promotion_codes": null,
      "amount_subtotal": 3000,
      "amount_total": 3000,
      "automatic_tax": {
        "enabled": false,
        "liability": null,
        "provider": null,
        "status": null
      },
      "billing_address_collection": null,
      "branding_settings": {
        "background_color": "#ffffff",
        "border_style": "rounded",
        "button_color": "#0074d4",
        "display_name": "Sandbox Business sandbox",
        "font_family": "default",
        "icon": null,
        "logo": null
      },
      "cancel_url": "https://httpbin.org/post",
      "client_reference_id": null,
      "client_secret": null,
      "collected_information": null,
      "consent": null,
      "consent_collection": null,
      "created": 1771041837,
      "currency": "usd",
      "currency_conversion": null,
      "custom_fields": [],
      "custom_text": {
        "after_submit": null,
        "shipping_address": null,
        "submit": null,
        "terms_of_service_acceptance": null
      },
      "customer": null,
      "customer_account": null,
      "customer_creation": "if_required",
      "customer_details": {
        "address": {
          "city": "South San Francisco",
          "country": "US",
          "line1": "354 Oyster Point Blvd",
          "line2": null,
          "postal_code": "94080",
          "state": "CA"
        },
        "business_name": null,
        "email": "stripe@example.com",
        "individual_name": null,
        "name": "Jenny Rosen",
        "phone": null,
        "tax_exempt": "none",
        "tax_ids": []
      },
      "customer_email": null,
      "discounts": [],
      "expires_at": 1771128237,
      "invoice": null,
      "invoice_creation": {
        "enabled": false,
        "invoice_data": {
          "account_tax_ids": null,
          "custom_fields": null,
          "description": null,
          "footer": null,
          "issuer": null,
          "metadata": {},
          "rendering_options": null
        }
      },
      "livemode": false,
      "locale": null,
      "metadata": {},
      "mode": "payment",
      "origin_context": null,
      "payment_intent": "pi_3T0a5LIeADuLKrJV03FitWbm",
      "payment_link": null,
      "payment_method_collection": "if_required",
      "payment_method_configuration_details": {
        "id": "pmc_1SzixkIeADuLKrJVUgZT2LLN",
        "parent": null
      },
      "payment_method_options": {
        "card": {
          "request_three_d_secure": "automatic"
        }
      },
      "payment_method_types": [
        "card",
        "klarna",
        "link",
        "cashapp",
        "amazon_pay"
      ],
      "payment_status": "paid",
      "permissions": null,
      "phone_number_collection": {
        "enabled": false
      },
      "recovered_from": null,
      "saved_payment_method_options": null,
      "setup_intent": null,
      "shipping_address_collection": null,
      "shipping_cost": null,
      "shipping_options": [],
      "status": "complete",
      "submit_type": null,
      "subscription": null,
      "success_url": "https://httpbin.org/post",
      "total_details": {
        "amount_discount": 0,
        "amount_shipping": 0,
        "amount_tax": 0
      },
      "ui_mode": "hosted",
      "url": null,
      "wallet_options": null
    }
  },
  "livemode": false,
  "pending_webhooks": 2,
  "request": {
    "id": null,
    "idempotency_key": null
  },
  "type": "checkout.session.completed"
}
*/

}
