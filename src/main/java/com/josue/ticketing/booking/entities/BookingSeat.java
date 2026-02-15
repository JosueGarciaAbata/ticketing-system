package com.josue.ticketing.booking.entities;

import com.josue.ticketing.booking.embbeded.BookingSeatId;
import com.josue.ticketing.catalog.seats.entities.Seat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "bookings_seats")
public class BookingSeat {

    // Define la pk
    @EmbeddedId
    private BookingSeatId id;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("bookingId") // Necesario para decir que "la fk usa la pk"
    private Booking booking;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("seatId")
    private Seat seat;

}
