package com.josue.ticketing.catalog.venues.services;

import com.josue.ticketing.catalog.venues.dtos.VenueCreateRequest;
import com.josue.ticketing.catalog.venues.dtos.VenueResponse;
import com.josue.ticketing.catalog.venues.dtos.VenueUpdateRequest;

import java.util.List;

public interface VenueService {

    List<VenueResponse> findAll();
    VenueResponse create(VenueCreateRequest req);
    VenueResponse update(Integer id, VenueUpdateRequest req);
    void deleteById(Integer id);

}
