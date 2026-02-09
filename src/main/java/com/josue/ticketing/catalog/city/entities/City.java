package com.josue.ticketing.catalog.city.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "cities",
        uniqueConstraints =  @UniqueConstraint(columnNames = {"name", "country"}))
public class City {

    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private  String name;

    @Column(nullable = false, length = 100)
    private String country;

    @Column(nullable = false, length = 100)
    private String timezone;

    @Column(nullable = false)
    private Boolean active;

}
