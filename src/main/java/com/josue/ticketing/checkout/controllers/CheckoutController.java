package com.josue.ticketing.checkout.controllers;

import com.josue.ticketing.checkout.dtos.StartCheckoutResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.josue.ticketing.checkout.dtos.CheckoutStatusResponse;
import com.josue.ticketing.checkout.dtos.CheckoutCreateRequest;
import com.josue.ticketing.checkout.service.CheckoutService;

@RestController
@RequestMapping("/api/v1/checkout")
@RequiredArgsConstructor
@Validated
public class CheckoutController {
    private final CheckoutService checkoutService;

    @PreAuthorize("hasAnyRole('CLIENT')")
    @PostMapping("/")
    public ResponseEntity<StartCheckoutResponse> startCheckout(@Valid @RequestBody CheckoutCreateRequest req) {
        String uri = checkoutService.startCheckout(req);
        StartCheckoutResponse response = new StartCheckoutResponse("Sesion creada con exito", uri);
        return ResponseEntity.ok().body(response);
    }

    @PreAuthorize("hasAnyRole('CLIENT')")
    @GetMapping({ "/status", "/payment-status" })
    public ResponseEntity<CheckoutStatusResponse> getCheckout(
            @NotBlank @RequestParam("session_id") String sessionId) {
        CheckoutStatusResponse response = checkoutService.getCheckoutStatus(sessionId);
        return ResponseEntity.ok().body(response);
    }

    @PreAuthorize("hasAnyRole('CLIENT')")
    @RequestMapping(value = "/expire", method = { RequestMethod.GET, RequestMethod.POST })
    public ResponseEntity<String> expireCheckout(@NotBlank @RequestParam("session_id") String sessionId) {
        checkoutService.expireCheckoutSession(sessionId);
        return ResponseEntity.ok().body("expire");
    }

}
