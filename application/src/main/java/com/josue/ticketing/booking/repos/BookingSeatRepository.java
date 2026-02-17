package com.josue.ticketing.booking.repos;

import com.josue.ticketing.booking.entities.BookingSeat;
import com.josue.ticketing.booking.embbeded.BookingSeatId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingSeatRepository extends JpaRepository<BookingSeat, BookingSeatId> {

    @Query("""
                SELECT bs.seat.id
                FROM BookingSeat bs
                WHERE bs.booking.show.id = :showId
                  AND bs.booking.status = com.josue.ticketing.booking.enums.BookingStatus.ACTIVE
                  AND bs.booking.expiresAt > :now
                  AND bs.seat.id IN :seatIds
            """)
    List<Integer> findCurrentlyHeldSeatIds(Integer showId, Collection<Integer> seatIds, ZonedDateTime now);

    @Modifying
    @Query("""
            DELETE FROM BookingSeat bs
            WHERE bs.booking.id = :bookingId
            """)
    void deleteByBookingId(Integer bookingId);

    List<BookingSeat> findByBookingId(Integer bookingId);
}
