package com.josue.ticketing.provider;

import java.util.UUID;

public interface CheckoutSessionProvider {

  String createCheckoutSessionUrl(UUID bookingPublicId);
}
