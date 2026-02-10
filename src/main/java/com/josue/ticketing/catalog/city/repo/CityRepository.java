package com.josue.ticketing.catalog.city.repo;

import com.josue.ticketing.catalog.city.entities.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Integer> {
    boolean existsByNameAndCountry(String name, String country);

    @Query("SELECT c FROM City c JOIN FETCH c.venue v WHERE c.id = :id")
    Optional<City> findByIdWithVenues(Integer id);
}
