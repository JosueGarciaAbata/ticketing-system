package com.josue.ticketing.catalog.show.repos;

import com.josue.ticketing.catalog.show.entities.Show;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShowRepository extends JpaRepository<Show, Integer> {

    boolean existsByEventId(Integer eventId);

}
