package com.josue.ticketing.catalog.show.controllers;

import com.josue.ticketing.catalog.show.dtos.ShowResponse;
import com.josue.ticketing.catalog.show.dtos.ShowUpdateRequest;
import com.josue.ticketing.catalog.show.dtos.ShowWithSeatsCreateRequest;
import com.josue.ticketing.catalog.show.services.ShowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shows")
@RequiredArgsConstructor
public class ShowController {

    private final ShowService showService;

    @GetMapping("/")
    public List<ShowResponse> findAll() {
        return showService.findAll();
    }

    @PostMapping("/")
    public ResponseEntity<ShowResponse> createShowWithSeats(@RequestBody ShowWithSeatsCreateRequest req) {
        return ResponseEntity.ok(showService.createShowWithSeats(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShowResponse> update(@PathVariable Integer id, @RequestBody ShowUpdateRequest req) {
        return ResponseEntity.ok(showService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShow(@PathVariable Integer id) {
        showService.cancelBookingAndReleaseSeats(id);
        return ResponseEntity.ok().build();
    }
}
