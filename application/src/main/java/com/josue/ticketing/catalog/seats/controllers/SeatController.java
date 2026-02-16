package com.josue.ticketing.catalog.seats.controllers;

import com.josue.ticketing.catalog.seats.dtos.SeatCategoryUpdateRequest;
import com.josue.ticketing.catalog.seats.dtos.SeatResponse;
import com.josue.ticketing.catalog.seats.services.SeatService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/seats")
@RequiredArgsConstructor
@Validated
public class SeatController {

    private final SeatService seatService;

    @GetMapping("/shows/{id}")
    public List<SeatResponse> getSeats(@Positive @PathVariable Integer id) {
        return seatService.findAllByShowId(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SeatResponse> changeCategory(@Positive @PathVariable Integer id,
            @Valid @RequestBody SeatCategoryUpdateRequest req) {
        return ResponseEntity.ok(seatService.changeCategory(id, req.seatCategory()));
    }

}
