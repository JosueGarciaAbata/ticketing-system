package com.josue.ticketing.provider;

import java.util.UUID;

/**
 * Proveedor de sesiones de checkout para procesamiento de pagos.
 */
public interface CheckoutSessionProvider {

  /**
   * Crea una URL de sesión de checkout para una reserva.
   * 
   * @param bookingPublicId identificador público de la reserva
   * @return URL de la sesión de checkout
   */
  String createCheckoutSessionUrl(UUID bookingPublicId);
}
