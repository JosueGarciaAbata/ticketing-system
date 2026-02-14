package com.josue.ticketing.payment.services;

import java.util.UUID;

public interface PaymentService {

    String createCheckoutSession(UUID bookingPublicId) throws Exception;

}
