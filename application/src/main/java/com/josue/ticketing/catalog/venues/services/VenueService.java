package com.josue.ticketing.catalog.venues.services;

import com.josue.ticketing.catalog.venues.dtos.VenueCreateRequest;
import com.josue.ticketing.catalog.venues.dtos.VenueResponse;
import com.josue.ticketing.catalog.venues.dtos.VenueUpdateRequest;

import java.util.List;

/**
 * Servicio para gestión de lugares (venues).
 */
public interface VenueService {

    /**
     * Obtiene todos los lugares.
     * 
     * @return lista de lugares
     */
    List<VenueResponse> findAll();

    /**
     * Crea un nuevo lugar.
     * 
     * @param req datos del lugar
     * @return lugar creado
     */
    VenueResponse create(VenueCreateRequest req);

    /**
     * Actualiza un lugar existente.
     * 
     * @param id  identificador del lugar
     * @param req datos actualizados
     * @return lugar actualizado
     */
    VenueResponse update(Integer id, VenueUpdateRequest req);

    /**
     * Elimina un lugar por su ID.
     * 
     * @param id identificador del lugar
     */
    void deleteById(Integer id);

}
