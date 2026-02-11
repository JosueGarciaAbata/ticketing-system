package com.josue.ticketing.booking.entities;

import com.josue.ticketing.booking.pk.BookingSeatId;
import com.josue.ticketing.catalog.seat.entities.Seat;
import jakarta.persistence.*;

@Entity
@Table(name = "bookings_seats")
public class BookingSeat {

    // Define la pk
    @EmbeddedId
    private BookingSeatId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("bookingId") // Necesario para decir que "la fk usa la pk"
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("seatId")
    private Seat seat;

}
