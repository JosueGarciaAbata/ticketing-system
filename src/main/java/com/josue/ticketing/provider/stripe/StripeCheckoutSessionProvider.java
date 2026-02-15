package com.josue.ticketing.provider.stripe;

import com.josue.ticketing.booking.entities.Booking;
import com.josue.ticketing.booking.entities.BookingSeat;
import com.josue.ticketing.booking.exceptions.BookingNotFoundException;
import com.josue.ticketing.booking.repos.BookingRepository;
import com.josue.ticketing.booking.repos.BookingSeatRepository;
import com.josue.ticketing.config.AuthService;
import com.josue.ticketing.provider.CheckoutSessionProvider;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service

public class StripeCheckoutSessionProvider implements CheckoutSessionProvider {

  @Value("${FRONTEND_URL}")
  private String frontendUrl;

  private final AuthService authService;
  private final BookingRepository bookingRepository;
  private final BookingSeatRepository bookingSeatRepository;

  public StripeCheckoutSessionProvider(BookingRepository bookingRepository,
                                       BookingSeatRepository bookingSeatRepository,
                                       AuthService authService) {
    this.bookingRepository = bookingRepository;
    this.bookingSeatRepository = bookingSeatRepository;
    this.authService = authService;
    Stripe.apiKey = System.getenv("STRIPE_SECRET_KEY");
  }

  @Override
  public String createCheckoutSessionUrl(UUID bookingPublicId) {

    Booking booking = bookingRepository.findByPublicId(bookingPublicId)
        .orElseThrow(() -> new BookingNotFoundException(
            "Reserva no encontrad con id=" + bookingPublicId));

    List<BookingSeat> bookingSeats = bookingSeatRepository.findByBookingId(booking.getId());

    int quantity = bookingSeats.size();
    long unitAmount = 500;
    String productName = "Ticket para " + booking.getPublicId();

    Integer userId = authService.getUserId();

    SessionCreateParams params = SessionCreateParams.builder()
        .setPaymentIntentData(SessionCreateParams.PaymentIntentData.builder()
            .putMetadata("bookingPublicId", booking.getPublicId().toString())
            .build())
        .putMetadata("bookingPublicId", booking.getPublicId().toString())
        .putMetadata("userId", userId.toString())
        .setMode(SessionCreateParams.Mode.PAYMENT)
        .setExpiresAt((System.currentTimeMillis() / 1000L) + (30 * 60))
        .setSuccessUrl(frontendUrl + "/status?session_id={CHECKOUT_SESSION_ID}")
        .setCancelUrl(frontendUrl + "/canceled")
        .addLineItem(
            SessionCreateParams.LineItem.builder()
                .setQuantity((long) quantity)
                .setPriceData(
                    SessionCreateParams.LineItem.PriceData
                        .builder()
                        .setCurrency("usd")
                        .setUnitAmount(unitAmount)
                        .setProductData(
                            SessionCreateParams.LineItem.PriceData.ProductData
                                .builder()
                                .setName(productName)
                                .build())
                        .build())
                .build())
        .build();

    try {
      Session session = Session.create(params);
      return session.getUrl();
    } catch (Exception e) {
      throw new RuntimeException("No se pudo crear la sesión de checkout.", e);
    }
  }
}
