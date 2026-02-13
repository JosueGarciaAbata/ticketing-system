package com.josue.ticketing.payment.controllers;

import com.josue.ticketing.payment.dtos.PaymentCreateRequest;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/api/v1/payments")
public class PaymentController {

    public PaymentController() {
        Stripe.apiKey = System.getenv("STRIPE_SECRET_KEY");
    }

    @PreAuthorize("hasAnyRole('CLIENT')")
    @PostMapping("/create-session")
    public String createSession(@RequestBody PaymentCreateRequest req) throws Exception {

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("https://miapp.com/exito")
                .setCancelUrl("https://miapp.com/cancelado")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(5L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setUnitAmount(10L)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Seat")
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();

        Session session = Session.create(params);

        return session.getUrl();
    }

}
