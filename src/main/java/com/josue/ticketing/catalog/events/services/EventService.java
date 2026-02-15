package com.josue.ticketing.catalog.events.services;

import com.josue.ticketing.catalog.events.dtos.EventCreateRequest;
import com.josue.ticketing.catalog.events.dtos.EventResponse;
import com.josue.ticketing.catalog.events.dtos.EventUpdateRequest;

import java.util.List;

public interface EventService {

    List<EventResponse> findAll();
    EventResponse create(EventCreateRequest req);
    EventResponse update(Integer id, EventUpdateRequest req);
    void deleteById(Integer id);

}
