package com.josue.ticketing.catalog.venue.controllers;

import com.josue.ticketing.catalog.venue.dtos.VenueCreateRequest;
import com.josue.ticketing.catalog.venue.dtos.VenueResponse;
import com.josue.ticketing.catalog.venue.dtos.VenueUpdateRequest;
import com.josue.ticketing.catalog.venue.services.VenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/venues")
@RequiredArgsConstructor
public class VenueController {

    private final VenueService venueService;

    @GetMapping("/")
    public List<VenueResponse> findAll() {
        return venueService.findAll();
    }

    @PostMapping("/")
    public ResponseEntity<VenueResponse> create(@RequestBody VenueCreateRequest req) {
        return ResponseEntity.ok(venueService.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VenueResponse> update(@PathVariable Integer id, @RequestBody VenueUpdateRequest req) {
        return ResponseEntity.ok(venueService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<VenueResponse> delete(@PathVariable Integer id) {
        venueService.delete(id);
        return ResponseEntity.ok().build();
    }

}
