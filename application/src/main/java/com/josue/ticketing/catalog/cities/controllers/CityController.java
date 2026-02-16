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

@RestController
@RequestMapping("/api/v1/cities")
@RequiredArgsConstructor
@Validated
public class CityController {

    private final CityService cityService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/")
    public List<CityResponse> findAll() {
        return this.cityService.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/")
    public ResponseEntity<CityResponse> create(@Valid @RequestBody CityCreateRequest req) {
        CityResponse res = this.cityService.create(req);
        return ResponseEntity.ok(res);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CityResponse> update(@Positive @PathVariable Integer id,
            @Valid @RequestBody CityUpdateRequest req) {
        CityResponse res = this.cityService.update(id, req);
        return ResponseEntity.ok(res);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Positive @PathVariable Integer id) {
        this.cityService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
