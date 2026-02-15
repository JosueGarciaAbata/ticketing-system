package com.josue.ticketing.reservation.controllers;

import com.josue.ticketing.booking.repos.BookingRepository;
import com.josue.ticketing.reservation.dtos.PaymentStatusResponse;
import com.josue.ticketing.reservation.dtos.TicketCreateRequest;
import com.josue.ticketing.reservation.service.TicketSaleService;
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
    private final BookingRepository bookingRepository;
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
    public ResponseEntity<PaymentStatusResponse> getPaymentStatus(@RequestParam("session_id") String sessionId) {
        PaymentStatusResponse response = ticketSaleService.getPaymentStatusBySessionId(sessionId);
        return  ResponseEntity.ok().body(response);
    }

    @GetMapping("/expire")
    public ResponseEntity<String> expireStripeSession(@RequestParam("session_id") String sessionId)  {
        ticketSaleService.expireStripeSession(sessionId);

        return ResponseEntity.ok().body("expire");
    }


}
