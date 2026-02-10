package com.josue.ticketing.catalog.venue.repos;

import com.josue.ticketing.catalog.venue.entities.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface VenueRepository extends JpaRepository<Venue,Integer> {

    @Query("SELECT v FROM Venue v LEFT JOIN FETCH v.shows s WHERE v.id = :id")
    Optional<Venue> findByIdWithShows(Integer id);

    boolean existsByCityId(Integer cityId);

}
