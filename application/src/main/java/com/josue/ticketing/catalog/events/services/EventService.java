package com.josue.ticketing.catalog.events.services;

import com.josue.ticketing.catalog.events.dtos.EventCreateRequest;
import com.josue.ticketing.catalog.events.dtos.EventResponse;
import com.josue.ticketing.catalog.events.dtos.EventUpdateRequest;

import java.util.List;

/**
 * Servicio para gestión de eventos.
 */
public interface EventService {

    /**
     * Obtiene todos los eventos.
     * 
     * @return lista de eventos
     */
    List<EventResponse> findAll();

    /**
     * Crea un nuevo evento.
     * 
     * @param req datos del evento
     * @return evento creado
     */
    EventResponse create(EventCreateRequest req);

    /**
     * Actualiza un evento existente.
     * 
     * @param id  identificador del evento
     * @param req datos actualizados
     * @return evento actualizado
     */
    EventResponse update(Integer id, EventUpdateRequest req);

    /**
     * Elimina un evento por su ID.
     * 
     * @param id identificador del evento
     */
    void deleteById(Integer id);

}
