package com.josue.ticketing.booking.repos;

import com.josue.ticketing.booking.entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Query("""
        SELECT count(b) > 0
        FROM Booking b
        WHERE b.status = com.josue.ticketing.booking.enums.BookingStatus.CONFIRMED AND
              b.show.id = :showId
        """)
    boolean existsConfirmedBookingByShowId(Integer showId);

    @Query("""
        SELECT b
        FROM Booking b
        WHERE b.status = com.josue.ticketing.booking.enums.BookingStatus.ACTIVE AND
            b.show.id = :showId
    """)
    List<Booking> findAllActiveBookingsByShowId(Integer showId);

}
