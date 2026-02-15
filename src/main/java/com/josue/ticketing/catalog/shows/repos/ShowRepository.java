package com.josue.ticketing.catalog.shows.repos;

import com.josue.ticketing.catalog.shows.entities.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.ZonedDateTime;

public interface ShowRepository extends JpaRepository<Show, Integer> {

    boolean existsByVenueId(Integer eventId);

    @Query(" SELECT coalesce(sum(s.capacity), 0) FROM Show s WHERE s.venue.id = :venueId")
    int sumCapacityByVenueId(Integer venueId);

    @Query("""
        SELECT COUNT(s) > 0
        FROM Show s 
        WHERE s.venue.id = :venueId AND
              (s.startTime < :endTime AND :startTime < s.endTime)
        """)
    boolean existsOverlapBetween(Integer venueId, ZonedDateTime startTime, ZonedDateTime endTime);

    boolean existsByEventId(Integer eventId);
}
