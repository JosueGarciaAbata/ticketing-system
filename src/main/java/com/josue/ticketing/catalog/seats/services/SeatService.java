package com.josue.ticketing.catalog.seats.services;

import com.josue.ticketing.catalog.seats.dtos.SeatResponse;
import com.josue.ticketing.catalog.seats.enums.SeatCategory;

import java.util.List;

public interface SeatService {

    List<SeatResponse> findAllByShowId(Integer showId);
    SeatResponse changeCategory(Integer id, SeatCategory category);

}
