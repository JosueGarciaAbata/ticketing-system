package com.josue.ticketing.catalog.event.services;

import com.josue.ticketing.catalog.event.dtos.EventCreateRequest;
import com.josue.ticketing.catalog.event.dtos.EventResponse;
import com.josue.ticketing.catalog.event.dtos.EventUpdateRequest;

import java.util.List;

public interface EventService {

    List<EventResponse> findAll();
    EventResponse create(EventCreateRequest req);
    EventResponse update(Integer id, EventUpdateRequest req);
    void deleteById(Integer id);

}
