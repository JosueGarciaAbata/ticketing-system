package com.josue.ticketing.catalog.show.entities;

import com.josue.ticketing.catalog.events.entities.Event;
import com.josue.ticketing.catalog.show.enums.ShowStatus;
import com.josue.ticketing.catalog.venue.entities.Venue;
import jakarta.persistence.*;

import java.time.ZonedDateTime;

@Entity
@Table(name = "shows")
public class Show {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @Column(nullable = false)
    private Integer capacity;

    @Column(name = "start_time", nullable = false)
    private ZonedDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private ZonedDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ShowStatus status;

}
