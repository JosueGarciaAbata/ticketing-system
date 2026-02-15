package com.josue.ticketing.catalog.venues.entities;

import com.josue.ticketing.catalog.cities.entities.City;
import com.josue.ticketing.catalog.shows.entities.Show;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;

import java.util.List;

@Getter
@Setter
@SoftDelete(columnName = "active", strategy = SoftDeleteType.ACTIVE)
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

    @OneToMany(mappedBy = "venue", fetch = FetchType.LAZY)
    private List<Show> shows;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false, insertable = false, updatable = false)
    private Boolean active;

    @PrePersist
    public void prePersist() {
        this.active = true;
    }

}
