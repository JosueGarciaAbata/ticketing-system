package com.josue.ticketing.catalog.seats.services;

import com.josue.ticketing.catalog.seats.dtos.SeatResponse;
import com.josue.ticketing.catalog.seats.enums.SeatCategory;

import java.util.List;

/**
 * Servicio para gestión de asientos.
 */
public interface SeatService {

    /**
     * Obtiene todos los asientos de un show.
     * 
     * @param showId identificador del show
     * @return lista de asientos
     */
    List<SeatResponse> findAllByShowId(Integer showId);

    /**
     * Cambia la categoría de un asiento.
     * 
     * @param id       identificador del asiento
     * @param category nueva categoría
     * @return asiento actualizado
     */
    SeatResponse changeCategory(Integer id, SeatCategory category);

}
