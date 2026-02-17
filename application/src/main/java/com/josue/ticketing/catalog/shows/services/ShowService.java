package com.josue.ticketing.catalog.shows.services;

import com.josue.ticketing.catalog.shows.dtos.ShowWithSeatsCreateRequest;
import com.josue.ticketing.catalog.shows.dtos.ShowResponse;
import com.josue.ticketing.catalog.shows.dtos.ShowUpdateRequest;

import java.util.List;

/**
 * Servicio para gestión de funciones (shows).
 */
public interface ShowService {

    /**
     * Obtiene todas las funciones.
     * 
     * @return lista de funciones
     */
    List<ShowResponse> findAll();

    /**
     * Crea una función con sus asientos y precios.
     * 
     * @param showWithSeatsCreateRequest datos de la función
     * @return función creada
     */
    ShowResponse createShowWithSeats(ShowWithSeatsCreateRequest showWithSeatsCreateRequest);

    /**
     * Actualiza los horarios de una función.
     * 
     * @param id  identificador de la función
     * @param req nuevos horarios
     * @return función actualizada
     */
    ShowResponse update(Integer id, ShowUpdateRequest req);

    /**
     * Cancela una función liberando reservas activas.
     * 
     * @param showId identificador de la función
     */
    void cancelBookingAndReleaseSeats(Integer showId);

}
