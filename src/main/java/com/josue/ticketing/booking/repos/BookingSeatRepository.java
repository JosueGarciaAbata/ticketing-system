package com.josue.ticketing.booking.repos;

import com.josue.ticketing.booking.entities.BookingSeat;
import com.josue.ticketing.booking.pk.BookingSeatId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookingSeatRepository extends JpaRepository<BookingSeat, BookingSeatId> {

    @Modifying
    @Query("""
        DELETE FROM BookingSeat bs
        WHERE bs.booking.id = :bookingId
        """
    )
    void deleteByBookingId(Integer bookingId);

    List<BookingSeat> findByBookingId(Integer bookingId);
}
