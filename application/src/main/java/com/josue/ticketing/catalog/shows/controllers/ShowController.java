package com.josue.ticketing.catalog.shows.controllers;

import com.josue.ticketing.catalog.shows.dtos.ShowResponse;
import com.josue.ticketing.catalog.shows.dtos.ShowUpdateRequest;
import com.josue.ticketing.catalog.shows.dtos.ShowWithSeatsCreateRequest;
import com.josue.ticketing.catalog.shows.services.ShowService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de funciones (shows).
 */
@RestController
@RequestMapping("/api/v1/shows")
@RequiredArgsConstructor
@Validated
public class ShowController {

    private final ShowService showService;

    /**
     * Obtiene todas las funciones registradas.
     * 
     * @return lista de funciones
     */
    @GetMapping("/")
    public List<ShowResponse> findAll() {
        return showService.findAll();
    }

    /**
     * Crea una nueva función con sus asientos.
     * 
     * @param req datos de la función incluyendo capacidad y precio
     * @return función creada
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @PostMapping("/")
    public ResponseEntity<ShowResponse> createShowWithSeats(@Valid @RequestBody ShowWithSeatsCreateRequest req) {
        return ResponseEntity.ok(showService.createShowWithSeats(req));
    }

    /**
     * Actualiza los horarios de una función existente.
     * 
     * @param id  identificador de la función
     * @param req nuevos horarios
     * @return función actualizada
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @PutMapping("/{id}")
    public ResponseEntity<ShowResponse> update(@Positive @PathVariable Integer id,
            @Valid @RequestBody ShowUpdateRequest req) {
        return ResponseEntity.ok(showService.update(id, req));
    }

    /**
     * Cancela una función liberando reservas activas.
     * 
     * @param id identificador de la función
     * @return respuesta vacía con estado OK
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShow(@Positive @PathVariable Integer id) {
        showService.cancelBookingAndReleaseSeats(id);
        return ResponseEntity.ok().build();
    }
}
