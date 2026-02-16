package com.josue.ticketing.catalog.seats.repos;

import com.josue.ticketing.catalog.seats.entities.SeatPricing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatPricingRepository extends JpaRepository<SeatPricing, Integer> {
}
