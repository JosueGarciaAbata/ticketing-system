package com.josue.ticketing.catalog.cities.controllers;

import com.josue.ticketing.catalog.cities.dtos.CityCreateRequest;
import com.josue.ticketing.catalog.cities.dtos.CityResponse;
import com.josue.ticketing.catalog.cities.dtos.CityUpdateRequest;
import com.josue.ticketing.catalog.cities.services.CityService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de ciudades.
 */
@RestController
@RequestMapping("/api/v1/cities")
@RequiredArgsConstructor
@Validated
public class CityController {

    private final CityService cityService;

    /**
     * Obtiene todas las ciudades registradas.
     * 
     * @return lista de ciudades
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/")
    public List<CityResponse> findAll() {
        return this.cityService.findAll();
    }

    /**
     * Crea una nueva ciudad.
     * 
     * @param req datos de la ciudad a crear
     * @return ciudad creada
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/")
    public ResponseEntity<CityResponse> create(@Valid @RequestBody CityCreateRequest req) {
        CityResponse res = this.cityService.create(req);
        return ResponseEntity.ok(res);
    }

    /**
     * Actualiza una ciudad existente.
     * 
     * @param id  identificador de la ciudad
     * @param req datos actualizados
     * @return ciudad actualizada
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CityResponse> update(@Positive @PathVariable Integer id,
            @Valid @RequestBody CityUpdateRequest req) {
        CityResponse res = this.cityService.update(id, req);
        return ResponseEntity.ok(res);
    }

    /**
     * Elimina una ciudad por su ID.
     * 
     * @param id identificador de la ciudad
     * @return respuesta vacía con estado OK
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Positive @PathVariable Integer id) {
        this.cityService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
