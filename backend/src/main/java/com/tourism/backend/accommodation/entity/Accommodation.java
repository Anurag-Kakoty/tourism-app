package com.tourism.backend.accommodation.entity;

import com.tourism.backend.util.BaseEntity;
import com.tourism.backend.destination.entity.Destination;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "accommodations")
@Getter
@Setter
@NoArgsConstructor
public class Accommodation extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccommodationType type;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerNight;

    private Double rating;

    @Column(length = 20)
    private String contactNumber;

    @Column(length = 100)
    private String email;

    @Column(length = 300)
    private String website;

    @Column(nullable = false, length = 300)
    private String address;

    private Double latitude;

    private Double longitude;

    @Column(length = 500)
    private String imageUrl;

    @Column(nullable = false)
    private Boolean available = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "destination_id", nullable = false)
    private Destination destination;
}