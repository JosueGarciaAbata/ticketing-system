package com.josue.ticketing.checkout.service;

import com.josue.ticketing.checkout.dtos.CheckoutStatusResponse;
import com.josue.ticketing.checkout.dtos.CheckoutCreateRequest;

public interface CheckoutService {

    String startCheckout(CheckoutCreateRequest req);
    CheckoutStatusResponse getCheckoutStatus(String sessionId);
    void expireCheckoutSession(String sessionId);

}
