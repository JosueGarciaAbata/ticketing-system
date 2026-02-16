package com.josue.ticketing.catalog.cities.repos;

import com.josue.ticketing.catalog.cities.entities.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Integer> {
    boolean existsByNameAndCountry(String name, String country);

    @Query("SELECT c FROM City c LEFT JOIN FETCH c.venues v WHERE c.id = :id")
    Optional<City> findByIdWithVenues(Integer id);
    boolean existsByNameAndCountryAndIdNot(String name, String country, Integer id);
}
