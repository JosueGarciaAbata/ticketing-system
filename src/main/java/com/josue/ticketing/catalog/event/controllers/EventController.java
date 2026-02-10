package com.josue.ticketing.catalog.event.controllers;

import com.josue.ticketing.catalog.event.dtos.EventCreateRequest;
import com.josue.ticketing.catalog.event.dtos.EventResponse;
import com.josue.ticketing.catalog.event.dtos.EventUpdateRequest;
import com.josue.ticketing.catalog.event.services.EventService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/events")
public class EventController {

    Logger logger = LoggerFactory.getLogger(EventController.class);

    private final EventService eventService;

    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @GetMapping("/")
    public List<EventResponse> findAll() {
        return eventService.findAll();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @PostMapping("/")
    public ResponseEntity<EventResponse> create(@RequestBody EventCreateRequest eventRequest) {
        return ResponseEntity.ok(eventService.create(eventRequest));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> update(@PathVariable Integer id, @RequestBody EventUpdateRequest eventRequest) {
        logger.info("La categoria es {}", eventRequest.category());
        return ResponseEntity.ok(eventService.update(id, eventRequest));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<EventResponse> delete(@PathVariable Integer id) {
        eventService.delete(id);
        return ResponseEntity.ok().build();
    }

}
