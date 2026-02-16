package com.josue.ticketing.catalog.cities.entities;

import com.josue.ticketing.catalog.venues.entities.Venue;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;

import java.util.List;

@Getter
@Setter
@SoftDelete(columnName = "active", strategy = SoftDeleteType.ACTIVE)
// @SQLDelete(sql = "UPDATE city SET active=false WHERE id=?")
// @Where(clause = "active=true")
@Entity
@Table(name = "cities",
        uniqueConstraints =  @UniqueConstraint(columnNames = {"name", "country"}))
public class City {

    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToMany(mappedBy = "city", fetch = FetchType.LAZY)
    private List<Venue> venues;

    @Column(nullable = false, length = 100)
    private  String name;

    @Column(nullable = false, length = 100)
    private String country;

    @Column(nullable = false, length = 100)
    private String timezone;

    @Column(nullable = false, insertable = false, updatable = false)
    private Boolean active;

    @PrePersist
    public void prePersist() {
        this.active = true;
    }

}
