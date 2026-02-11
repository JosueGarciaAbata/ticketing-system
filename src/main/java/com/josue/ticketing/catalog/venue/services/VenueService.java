package com.josue.ticketing.catalog.venue.services;

import com.josue.ticketing.catalog.venue.dtos.VenueCreateRequest;
import com.josue.ticketing.catalog.venue.dtos.VenueResponse;
import com.josue.ticketing.catalog.venue.dtos.VenueUpdateRequest;

import java.util.List;

public interface VenueService {

    List<VenueResponse> findAll();
    VenueResponse create(VenueCreateRequest req);
    VenueResponse update(Integer id, VenueUpdateRequest req);
    void deleteById(Integer id);

}
