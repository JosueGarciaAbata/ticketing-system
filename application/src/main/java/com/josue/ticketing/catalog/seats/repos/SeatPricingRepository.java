package com.josue.ticketing.catalog.seats.repos;

import com.josue.ticketing.catalog.seats.entities.SeatPricing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatPricingRepository extends JpaRepository<SeatPricing, Integer> {
    List<SeatPricing> findAllByShowId(Integer showId);
}
