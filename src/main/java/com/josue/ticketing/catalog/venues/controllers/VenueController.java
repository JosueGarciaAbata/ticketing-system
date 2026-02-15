package com.josue.ticketing.catalog.venues.controllers;

import com.josue.ticketing.catalog.venues.dtos.VenueCreateRequest;
import com.josue.ticketing.catalog.venues.dtos.VenueResponse;
import com.josue.ticketing.catalog.venues.dtos.VenueUpdateRequest;
import com.josue.ticketing.catalog.venues.services.VenueService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/venues")
@RequiredArgsConstructor
@Validated
public class VenueController {

    private final VenueService venueService;

    @GetMapping("/")
    public List<VenueResponse> findAll() {
        return venueService.findAll();
    }

    @PostMapping("/")
    public ResponseEntity<VenueResponse> create(@Valid @RequestBody VenueCreateRequest req) {
        return ResponseEntity.ok(venueService.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VenueResponse> update(@Positive @PathVariable Integer id,
            @Valid @RequestBody VenueUpdateRequest req) {
        return ResponseEntity.ok(venueService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<VenueResponse> delete(@Positive @PathVariable Integer id) {
        venueService.deleteById(id);
        return ResponseEntity.ok().build();
    }

}
