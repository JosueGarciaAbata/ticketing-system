package com.josue.ticketing.payment.controllers;

import com.josue.ticketing.booking.entities.Booking;
import com.josue.ticketing.booking.enums.BookingStatus;
import com.josue.ticketing.booking.repos.BookingRepository;
import com.josue.ticketing.payment.enums.PaymentStatus;
import com.josue.ticketing.reservation.dtos.TicketCreateRequest;
import com.josue.ticketing.reservation.service.TicketSaleService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionExpireParams;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ticket-sale")
@RequiredArgsConstructor
public class TicketSaleController {

    private final Logger logger = LoggerFactory.getLogger(TicketSaleController.class);

    private final TicketSaleService ticketSaleService;
    private final BookingRepository bookingRepository;

    @PreAuthorize("hasAnyRole('CLIENT')")
    @PostMapping("/")
    public ResponseEntity<String> saleTicket(@RequestBody TicketCreateRequest req) {
        try {
            return ResponseEntity.ok().body(ticketSaleService.reservate(req));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //cs_test_a1XjCENSmEUHsdzodzZ99whdWhszfZ2qHyv5bZGzUjDkyD0hytgqa1HKQq
    @GetMapping("/payment-status")
    public ResponseEntity<String> getPaymentStatus(@RequestParam("session_id") String sessionId) throws StripeException {
        Session session = Session.retrieve(sessionId);

        String paymentStatus = session.getPaymentStatus();
        String bookingPublicId = session.getMetadata().get("bookingPublicId");

        Booking booking = bookingRepository.findByPublicId(
                UUID.fromString(bookingPublicId)
        ).orElse(null);

        if (booking == null) {
            return ResponseEntity.badRequest().body("Reserva no encontrada");
        }

        if (booking.getStatus() == BookingStatus    .CANCELED) {
            return ResponseEntity.ok("Reserva expirada. El pago no es válido.");
        }

        if ("paid".equalsIgnoreCase(paymentStatus)) {
            return ResponseEntity.ok("Pago confirmado correctamente");
        }

        return ResponseEntity.ok("Pago no completado");
    }

    @GetMapping("/expire")
    public void expireStripeSession(@RequestParam("session_id") String sessionId) throws Exception {
        // llama al endpoint de expiración
        Session session = Session.retrieve(sessionId);
        session.expire();

        System.out.println("Session expired: " + session.getStatus());
    }


}
