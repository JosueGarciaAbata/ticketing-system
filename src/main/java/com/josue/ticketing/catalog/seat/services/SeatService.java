package com.josue.ticketing.catalog.seat.services;

import com.josue.ticketing.catalog.seat.dtos.SeatResponse;
import com.josue.ticketing.catalog.seat.enums.SeatCategory;

import java.util.List;

public interface SeatService {

    List<SeatResponse> findAllByShowId(Integer showId);
    SeatResponse changeCategory(Integer id, SeatCategory category);

}
