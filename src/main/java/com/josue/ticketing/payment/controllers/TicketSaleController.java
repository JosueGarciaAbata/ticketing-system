package com.josue.ticketing.payment.controllers;

import com.josue.ticketing.payment.enums.PaymentStatus;
import com.josue.ticketing.reservation.dtos.TicketCreateRequest;
import com.josue.ticketing.reservation.service.TicketSaleService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ticket-sale")
@RequiredArgsConstructor
public class TicketSaleController {

    private final Logger logger = LoggerFactory.getLogger(TicketSaleController.class);

    private final TicketSaleService ticketSaleService;

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
    public String getPaymentStatus(@RequestParam("session_id") String sessionId) throws StripeException {
        Session session = Session.retrieve(sessionId);
        String paymentStatus = session.getPaymentStatus();
        logger.info("Session: " + session);
        logger.info("PaymentStatus: " + paymentStatus);

        if ("paid".equalsIgnoreCase(paymentStatus)) {
            return "Pago realizado con exito";
        }

        return "ok";
    }
}
