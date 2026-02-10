package com.josue.ticketing.catalog.city.services;

import com.josue.ticketing.catalog.city.dtos.CityCreateRequest;
import com.josue.ticketing.catalog.city.dtos.CityResponse;
import com.josue.ticketing.catalog.city.dtos.CityUpdateRequest;

import java.util.List;

public interface CityService {

    List<CityResponse> findAll();
    CityResponse create(CityCreateRequest req);
    CityResponse update(Integer id, CityUpdateRequest req);
    void delete(Integer id);

}
