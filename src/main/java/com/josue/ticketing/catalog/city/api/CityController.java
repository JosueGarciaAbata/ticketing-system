package com.josue.ticketing.catalog.city.api;

import com.josue.ticketing.catalog.city.dtos.CityCreateRequest;
import com.josue.ticketing.catalog.city.dtos.CityResponse;
import com.josue.ticketing.catalog.city.dtos.CityUpdateRequest;
import com.josue.ticketing.catalog.city.service.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cities")
@RequiredArgsConstructor
public class CityController {

    private final CityService cityService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/")
    public List<CityResponse> findAll() {
        return this.cityService.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/")
    public ResponseEntity<CityResponse> create(@RequestBody CityCreateRequest req) {
        CityResponse res = this.cityService.create(req);
        return ResponseEntity.ok(res);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CityResponse> update(@PathVariable Integer id, @RequestBody CityUpdateRequest req) {
        CityResponse res = this.cityService.update(id, req);
        return ResponseEntity.ok(res);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        this.cityService.delete(id);
        return ResponseEntity.ok().build();
    }
}
