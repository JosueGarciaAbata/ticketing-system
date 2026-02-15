package com.josue.ticketing.payment.services;

import com.josue.ticketing.booking.entities.Booking;
import com.josue.ticketing.booking.entities.BookingSeat;
import com.josue.ticketing.booking.exps.BookingNotFoundException;
import com.josue.ticketing.booking.repos.BookingRepository;
import com.josue.ticketing.booking.repos.BookingSeatRepository;
import com.josue.ticketing.payment.repos.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Value("${FRONTEND_URL}")
    private String frontendUrl;

    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;

    public  PaymentServiceImpl(BookingRepository bookingRepository,
                               BookingSeatRepository bookingSeatRepository) {
        this.bookingRepository = bookingRepository;
        this.bookingSeatRepository = bookingSeatRepository;
        Stripe.apiKey = System.getenv("STRIPE_SECRET_KEY");
    }

    @Override
    public String createCheckoutSession(UUID bookingPublicId) throws Exception {

        Booking booking = bookingRepository.findByPublicId(bookingPublicId)
                .orElseThrow(() -> new BookingNotFoundException("Reserva no encontrad con id=" +  bookingPublicId));

        List<BookingSeat> bookingSeats = bookingSeatRepository.findByBookingId(booking.getId());

        int quantity = bookingSeats.size();
        long unitAmount = 500;
        String productName = "Ticket para " + booking.getPublicId();

        SessionCreateParams params = SessionCreateParams.builder()
                .setPaymentIntentData(SessionCreateParams.PaymentIntentData.builder().putMetadata("bookingPublicId", booking.getPublicId().toString()).build())
                .setPaymentIntentData(SessionCreateParams.PaymentIntentData.builder().putMetadata("sessionId", "{CHECKOUT_SESSION_ID}").build())
                .putMetadata("bookingPublicId", booking.getPublicId().toString())
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setExpiresAt((System.currentTimeMillis() / 1000L) + (30 * 60))
                .setSuccessUrl(frontendUrl + "/ok?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(frontendUrl + "/cancel")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity((long) quantity)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setUnitAmount(unitAmount)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName(productName)
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
