package com.josue.ticketing.catalog.seats.repos;

import com.josue.ticketing.catalog.seats.entities.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Integer> {

    List<Seat> findAllByShowId(Integer showId);
}
