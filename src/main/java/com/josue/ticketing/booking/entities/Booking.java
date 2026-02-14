package com.josue.ticketing.booking.entities;

import com.josue.ticketing.booking.enums.BookingStatus;
import com.josue.ticketing.catalog.seat.entities.Seat;
import com.josue.ticketing.catalog.show.entities.Show;
import com.josue.ticketing.user.entities.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "public_id", nullable = false)
    private UUID publicId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BookingStatus status;

    @Column(name = "cancel_reason", nullable = false, length = 50)
    private String cancelReason;

    @Column(nullable = false)
    private ZonedDateTime expiresAt;

    @Column(nullable = false)
    private ZonedDateTime createdAt;

    @PrePersist
    public void onPrePersist() {
        this.createdAt = ZonedDateTime.now();
        this.status = BookingStatus.ACTIVE;
        this.expiresAt = ZonedDateTime.now().plusMinutes(5);
    }
}
