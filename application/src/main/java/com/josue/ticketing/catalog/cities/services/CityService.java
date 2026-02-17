package com.josue.ticketing.catalog.cities.services;

import com.josue.ticketing.catalog.cities.dtos.CityCreateRequest;
import com.josue.ticketing.catalog.cities.dtos.CityResponse;
import com.josue.ticketing.catalog.cities.dtos.CityUpdateRequest;

import java.util.List;

/**
 * Servicio para gestión de ciudades.
 */
public interface CityService {

    /**
     * Obtiene todas las ciudades.
     * 
     * @return lista de ciudades
     */
    List<CityResponse> findAll();

    /**
     * Crea una nueva ciudad.
     * 
     * @param req datos de la ciudad
     * @return ciudad creada
     */
    CityResponse create(CityCreateRequest req);

    /**
     * Actualiza una ciudad existente.
     * 
     * @param id  identificador de la ciudad
     * @param req datos actualizados
     * @return ciudad actualizada
     */
    CityResponse update(Integer id, CityUpdateRequest req);

    /**
     * Elimina una ciudad por su ID.
     * 
     * @param id identificador de la ciudad
     */
    void deleteById(Integer id);

}
