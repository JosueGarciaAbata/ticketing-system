package com.josue.ticketing.catalog.seat.repos;

import com.josue.ticketing.catalog.seat.entities.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Integer> {

}
