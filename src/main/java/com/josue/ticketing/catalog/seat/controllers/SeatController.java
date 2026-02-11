package com.josue.ticketing.catalog.seat.controllers;

import com.josue.ticketing.catalog.seat.dtos.SeatCategoryUpdateRequest;
import com.josue.ticketing.catalog.seat.dtos.SeatResponse;
import com.josue.ticketing.catalog.seat.entities.Seat;
import com.josue.ticketing.catalog.seat.enums.SeatCategory;
import com.josue.ticketing.catalog.seat.services.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @GetMapping("/shows/{id}")
    public List<SeatResponse> getSeats(@PathVariable Integer id) {
        return seatService.findAllByShowId(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SeatResponse> changeCategory(@PathVariable Integer id, @RequestBody SeatCategoryUpdateRequest req) {
            return ResponseEntity.ok(seatService.changeCategory(id, req.seatCategory()));
    }

}
