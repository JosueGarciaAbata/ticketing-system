package com.josue.ticketing.catalog.shows.services;

import com.josue.ticketing.catalog.shows.dtos.ShowWithSeatsCreateRequest;
import com.josue.ticketing.catalog.shows.dtos.ShowResponse;
import com.josue.ticketing.catalog.shows.dtos.ShowUpdateRequest;

import java.util.List;

public interface ShowService {

    List<ShowResponse> findAll();
    ShowResponse createShowWithSeats(ShowWithSeatsCreateRequest showWithSeatsCreateRequest);
    ShowResponse update(Integer id, ShowUpdateRequest req);
    void cancelBookingAndReleaseSeats(Integer showId);

}
