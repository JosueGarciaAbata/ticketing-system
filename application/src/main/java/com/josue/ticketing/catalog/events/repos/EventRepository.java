package com.josue.ticketing.catalog.events.repos;

import com.josue.ticketing.catalog.events.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Integer> {

    boolean existsById(Integer id);

}
