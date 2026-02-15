package com.josue.ticketing.catalog.cities.services;

import com.josue.ticketing.catalog.cities.dtos.CityCreateRequest;
import com.josue.ticketing.catalog.cities.dtos.CityResponse;
import com.josue.ticketing.catalog.cities.dtos.CityUpdateRequest;

import java.util.List;

public interface CityService {

    List<CityResponse> findAll();
    CityResponse create(CityCreateRequest req);
    CityResponse update(Integer id, CityUpdateRequest req);
    void deleteById(Integer id);

}
