package com.josue.ticketing.provider.stripe.webhook;

import com.josue.ticketing.booking.entities.Booking;
import com.josue.ticketing.booking.enums.BookingStatus;
import com.josue.ticketing.booking.exceptions.BookingNotFoundException;
import com.josue.ticketing.booking.repos.BookingRepository;
import com.josue.ticketing.booking.services.BookingService;
import com.josue.ticketing.payment.entities.Payment;
import com.josue.ticketing.payment.enums.PaymentStatus;
import com.josue.ticketing.payment.repos.PaymentRepository;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class StripeWebhookServiceImpl implements StripeWebhookService {

    private final Logger  logger = Logger.getLogger(StripeWebhookServiceImpl.class.getName());
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;
    private final PaymentRepository paymentRepository;

    @Transactional(readOnly = false)
    @Override
    public void handlePaymentIntentSucecceded(Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getData().getObject();
        String bookingPublicId = paymentIntent.getMetadata().get("bookingPublicId");

        Booking booking = bookingRepository.findByPublicId(UUID.fromString(bookingPublicId)).orElse(null);

        if (booking == null) {
            logger.info("Booking no encontrado: " + bookingPublicId);
            return;
        }

        if (booking.getStatus() == BookingStatus.CANCELED) {
            logger.info("Booking ya cancelado, nada que hacer.");
            return;
        }

        if (paymentRepository.existsByProviderAndProviderReference("STRIPE", paymentIntent.getId())) {
            return;
        }

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setProvider("STRIPE");
        payment.setProviderReference(paymentIntent.getId());
        payment.setStatus(PaymentStatus.SUCCESS);

        bookingService.confirm(UUID.fromString(bookingPublicId));
        paymentRepository.save(payment);
    }

    @Transactional(readOnly = false)
    @Override
    public void handlePaymentFailed(Event event) {
        // TODO: Hacer algo si el pago falla (por ejemplo lanzar una excepcion al
        // usuario)
    }
}
