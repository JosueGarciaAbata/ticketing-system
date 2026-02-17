package com.josue.ticketing.booking.services;

import com.josue.ticketing.booking.dtos.BookingCreateResponse;
import com.josue.ticketing.payment.dtos.BookingCreateRequest;

import java.util.UUID;

/**
 * Servicio para gestión de reservas de asientos.
 */
public interface BookingService {

    /**
     * Crea una nueva reserva con bloqueo en Redis.
     * 
     * @param bookingCreateRequest datos de la reserva
     * @return respuesta con el ID público de la reserva
     * @throws SeatsAlreadyHeldException si los asientos ya están retenidos
     */
    BookingCreateResponse create(BookingCreateRequest bookingCreateRequest);

    /**
     * Crea una nueva reserva solo en base de datos (sin Redis).
     * 
     * @param bookingCreateRequest datos de la reserva
     * @return respuesta con el ID público de la reserva
     */
    BookingCreateResponse createDbOnly(BookingCreateRequest bookingCreateRequest);

    /**
     * Confirma una reserva activa marcando los asientos como vendidos.
     * 
     * @param publicId identificador público de la reserva
     * @throws BookingNotFoundException si la reserva no existe
     */
    void confirm(UUID publicId);

    /**
     * Cancela una reserva activa liberando los asientos.
     * 
     * @param publicId identificador público de la reserva
     * @param reason   motivo de la cancelación
     */
    void cancel(UUID publicId, String reason);

    /**
     * Expira una reserva activa por timeout, liberando asientos.
     * 
     * @param publicId identificador público de la reserva
     */
    void expire(UUID publicId);

}
