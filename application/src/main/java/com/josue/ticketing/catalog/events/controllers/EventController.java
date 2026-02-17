package com.josue.ticketing.catalog.events.controllers;

import com.josue.ticketing.catalog.events.dtos.EventCreateRequest;
import com.josue.ticketing.catalog.events.dtos.EventResponse;
import com.josue.ticketing.catalog.events.dtos.EventUpdateRequest;
import com.josue.ticketing.catalog.events.services.EventService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de eventos.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/events")
@Validated
public class EventController {

    Logger logger = LoggerFactory.getLogger(EventController.class);

    private final EventService eventService;

    /**
     * Obtiene todos los eventos registrados.
     * 
     * @return lista de eventos
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @GetMapping("/")
    public List<EventResponse> findAll() {
        return eventService.findAll();
    }

    /**
     * Crea un nuevo evento.
     * 
     * @param eventRequest datos del evento a crear
     * @return evento creado
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @PostMapping("/")
    public ResponseEntity<EventResponse> create(@Valid @RequestBody EventCreateRequest eventRequest) {
        return ResponseEntity.ok(eventService.create(eventRequest));
    }

    /**
     * Actualiza un evento existente.
     * 
     * @param id           identificador del evento
     * @param eventRequest datos actualizados
     * @return evento actualizado
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> update(@Positive @PathVariable Integer id,
            @Valid @RequestBody EventUpdateRequest eventRequest) {
        logger.info("La categoria es {}", eventRequest.category());
        return ResponseEntity.ok(eventService.update(id, eventRequest));
    }

    /**
     * Elimina un evento por su ID.
     * 
     * @param id identificador del evento
     * @return respuesta vacía con estado OK
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<EventResponse> delete(@Positive @PathVariable Integer id) {
        eventService.deleteById(id);
        return ResponseEntity.ok().build();
    }

}
