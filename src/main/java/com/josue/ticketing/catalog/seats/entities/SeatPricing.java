package com.josue.ticketing.catalog.seats.entities;

import com.josue.ticketing.catalog.seats.enums.SeatCategory;
import com.josue.ticketing.catalog.shows.entities.Show;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "seat_pricing", uniqueConstraints = @UniqueConstraint(columnNames = {"show_id", "category"}))
public class SeatPricing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "show_id")
    private Show show;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SeatCategory category;

    @Column(nullable = false)
    private BigDecimal price;

}
