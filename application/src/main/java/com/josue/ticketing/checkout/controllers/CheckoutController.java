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

/**
 * Controlador REST para operaciones de checkout y pagos.
 */
@RestController
@RequestMapping("/api/v1/checkout")
@RequiredArgsConstructor
@Validated
public class CheckoutController {
    private final CheckoutService checkoutService;

    /**
     * Inicia el proceso de checkout con bloqueo de asientos en Redis.
     * 
     * @param req datos del checkout con show y asientos
     * @return respuesta con URL de la sesión de pago
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER', 'CLIENT')")
    @PostMapping("/")
    public ResponseEntity<StartCheckoutResponse> startCheckout(@Valid @RequestBody CheckoutCreateRequest req) {
        String uri = checkoutService.startCheckout(req);
        StartCheckoutResponse response = new StartCheckoutResponse("Sesion creada con exito", uri);
        return ResponseEntity.ok().body(response);
    }

    /**
     * Inicia el proceso de checkout solo con validación en base de datos.
     * 
     * @param req datos del checkout con show y asientos
     * @return respuesta con URL de la sesión de pago
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER', 'CLIENT')")
    @PostMapping("/db-only")
    public ResponseEntity<StartCheckoutResponse> startCheckoutDbOnly(@Valid @RequestBody CheckoutCreateRequest req) {
        String uri = checkoutService.startCheckoutDbOnly(req);
        StartCheckoutResponse response = new StartCheckoutResponse("Sesion creada con exito", uri);
        return ResponseEntity.ok().body(response);
    }

    /**
     * Obtiene el estado de una sesión de checkout.
     * 
     * @param sessionId identificador de la sesión de Stripe
     * @return estado actual del checkout y mensaje
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER', 'CLIENT')")
    @GetMapping({ "/status", "/payment-status" })
    public ResponseEntity<CheckoutStatusResponse> getCheckout(
            @NotBlank @RequestParam("session_id") String sessionId) {
        CheckoutStatusResponse response = checkoutService.getCheckoutStatus(sessionId);
        return ResponseEntity.ok().body(response);
    }

    /**
     * Expira manualmente una sesión de checkout.
     * 
     * @param sessionId identificador de la sesión de Stripe
     * @return confirmación de expiración
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER', 'CLIENT')")
    @RequestMapping(value = "/expire", method = { RequestMethod.GET, RequestMethod.POST })
    public ResponseEntity<String> expireCheckout(@NotBlank @RequestParam("session_id") String sessionId) {
        checkoutService.expireCheckoutSession(sessionId);
        return ResponseEntity.ok().body("expire");
    }

}
