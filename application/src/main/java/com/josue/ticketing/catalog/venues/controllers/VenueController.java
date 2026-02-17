package com.josue.ticketing.catalog.venues.controllers;

import com.josue.ticketing.catalog.venues.dtos.VenueCreateRequest;
import com.josue.ticketing.catalog.venues.dtos.VenueResponse;
import com.josue.ticketing.catalog.venues.dtos.VenueUpdateRequest;
import com.josue.ticketing.catalog.venues.services.VenueService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de lugares (venues).
 */
@RestController
@RequestMapping("/api/v1/venues")
@RequiredArgsConstructor
@Validated
public class VenueController {

    private final VenueService venueService;

    /**
     * Obtiene todos los lugares registrados.
     * 
     * @return lista de lugares
     */
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/")
    public List<VenueResponse> findAll() {
        return venueService.findAll();
    }

    /**
     * Crea un nuevo lugar.
     * 
     * @param req datos del lugar a crear
     * @return lugar creado
     */
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/")
    public ResponseEntity<VenueResponse> create(@Valid @RequestBody VenueCreateRequest req) {
        return ResponseEntity.ok(venueService.create(req));
    }

    /**
     * Actualiza un lugar existente.
     * 
     * @param id  identificador del lugar
     * @param req datos actualizados
     * @return lugar actualizado
     */
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<VenueResponse> update(@Positive @PathVariable Integer id,
            @Valid @RequestBody VenueUpdateRequest req) {
        return ResponseEntity.ok(venueService.update(id, req));
    }

    /**
     * Elimina un lugar por su ID.
     * 
     * @param id identificador del lugar
     * @return respuesta vacía con estado OK
     */
    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<VenueResponse> delete(@Positive @PathVariable Integer id) {
        venueService.deleteById(id);
        return ResponseEntity.ok().build();
    }

}
