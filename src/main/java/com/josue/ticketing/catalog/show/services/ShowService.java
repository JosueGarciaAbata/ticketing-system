package com.josue.ticketing.catalog.show.services;

import com.josue.ticketing.catalog.show.dtos.ShowWithSeatsCreateRequest;
import com.josue.ticketing.catalog.show.dtos.ShowResponse;
import com.josue.ticketing.catalog.show.dtos.ShowUpdateRequest;

import java.util.List;

public interface ShowService {

    List<ShowResponse> findAll();
    ShowResponse createShowWithSeats(ShowWithSeatsCreateRequest showWithSeatsCreateRequest);
    ShowResponse update(Integer id, ShowUpdateRequest req);
    void cancelBookingAndReleaseSeats(Integer showId);

}
