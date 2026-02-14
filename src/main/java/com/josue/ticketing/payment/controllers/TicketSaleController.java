package com.josue.ticketing.payment.controllers;

import com.josue.ticketing.reservation.dtos.TicketCreateRequest;
import com.josue.ticketing.reservation.service.TicketSaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ticket-sale")
@RequiredArgsConstructor
public class TicketSaleController {

    private final TicketSaleService ticketSaleService;

    @PostMapping("/")
    public ResponseEntity<String> saleTicket(@RequestBody TicketCreateRequest req) {
        try {
            return ResponseEntity.ok().body(ticketSaleService.reservate(req));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
