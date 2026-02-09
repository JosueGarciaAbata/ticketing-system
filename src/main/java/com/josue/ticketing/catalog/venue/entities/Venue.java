package com.josue.ticketing.catalog.venue.entities;

import com.josue.ticketing.catalog.city.entities.City;
import jakarta.persistence.*;

@Entity
@Table(name = "venues",
        uniqueConstraints = @UniqueConstraint(columnNames = {"city_id", "name"}))
public class Venue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private Integer active;

}
