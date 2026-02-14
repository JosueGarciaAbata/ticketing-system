package com.josue.ticketing.booking.repos;

import com.josue.ticketing.booking.entities.Booking;
import com.josue.ticketing.catalog.seat.entities.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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

    // Contexto: necesario el id de los asientos que existan y esten disponibles. Todo lo demas se ignora.
    @Query(value = """
        SELECT s
        FROM Seat s
        WHERE s.id IN :seatsId AND s.status = 'AVAILABLE'
    """)
    Set<Seat> filterAvailableSeatIds(Set<Integer> seatsId);


    Optional<Booking> findByPublicId(UUID publicId);
}
