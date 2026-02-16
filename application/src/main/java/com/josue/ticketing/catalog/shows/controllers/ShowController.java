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

@RestController
@RequestMapping("/api/v1/shows")
@RequiredArgsConstructor
@Validated
public class ShowController {

    private final ShowService showService;

    @GetMapping("/")
    public List<ShowResponse> findAll() {
        return showService.findAll();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @PostMapping("/")
    public ResponseEntity<ShowResponse> createShowWithSeats(@Valid @RequestBody ShowWithSeatsCreateRequest req) {
        return ResponseEntity.ok(showService.createShowWithSeats(req));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @PutMapping("/{id}")
    public ResponseEntity<ShowResponse> update(@Positive @PathVariable Integer id,
            @Valid @RequestBody ShowUpdateRequest req) {
        return ResponseEntity.ok(showService.update(id, req));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShow(@Positive @PathVariable Integer id) {
        showService.cancelBookingAndReleaseSeats(id);
        return ResponseEntity.ok().build();
    }
}
