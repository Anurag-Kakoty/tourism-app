package com.tourism.backend.destination.entity;

import com.tourism.backend.util.BaseEntity;
import com.tourism.backend.state.entity.State;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "destinations",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name", "state_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Destination extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 200)
    private String tagline;

    @Column(length = 1000)
    private String description;

    @Column(length = 100)
    private String district;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "state_id")
    private State state;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DestinationType type;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(length = 500)
    private String thumbnailUrl;

    @Column(length = 500)
    private String coverImageUrl;

    @Column(length = 100)
    private String timezone;

    @Column(length = 100)
    private String nearestAirport;

    @Column(length = 100)
    private String nearestRailwayStation;

    @Column(nullable = false)
    private Boolean featured = false;

    @Column(nullable = false)
    private Boolean popular = false;

    @Column(nullable = false)
    private Integer displayOrder = 0;
}