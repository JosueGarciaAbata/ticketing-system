package com.josue.ticketing.payment.entities;

import com.josue.ticketing.booking.entities.Booking;
import com.josue.ticketing.payment.enums.PaymentStatus;
import jakarta.persistence.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments", uniqueConstraints = @UniqueConstraint(columnNames = {"provider", "provider_reference"}))
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "public_id", nullable = false)
    private UUID publicId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(nullable = false, length = 50)
    private String provider;

    @Column(name = "provider_reference", nullable = false)
    private String providerReference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(nullable = false)
    private Double amount;

    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = ZonedDateTime.now();
    }

}
