package com.josue.ticketing.checkout.service;

import com.josue.ticketing.checkout.dtos.CheckoutStatusResponse;
import com.josue.ticketing.checkout.dtos.CheckoutCreateRequest;

/**
 * Servicio para gestión del proceso de checkout.
 */
public interface CheckoutService {

    /**
     * Inicia el checkout creando reserva con bloqueo Redis y sesión de pago.
     * 
     * @param req datos del checkout
     * @return URL de la sesión de pago
     */
    String startCheckout(CheckoutCreateRequest req);

    /**
     * Inicia el checkout creando reserva solo en BD y sesión de pago.
     * 
     * @param req datos del checkout
     * @return URL de la sesión de pago
     */
    String startCheckoutDbOnly(CheckoutCreateRequest req);

    /**
     * Obtiene el estado de una sesión de checkout.
     * 
     * @param sessionId identificador de la sesión de Stripe
     * @return estado del checkout
     */
    CheckoutStatusResponse getCheckoutStatus(String sessionId);

    /**
     * Expira manualmente una sesión de checkout en Stripe.
     * 
     * @param sessionId identificador de la sesión
     */
    void expireCheckoutSession(String sessionId);

}
