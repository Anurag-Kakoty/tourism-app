package com.tourism.backend.restaurant.entity;

import com.tourism.backend.util.BaseEntity;
import com.tourism.backend.destination.entity.Destination;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "restaurants")
@Getter
@Setter
@NoArgsConstructor
public class Restaurant extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Cuisine cuisine;

    @Column(nullable = false)
    private Boolean vegetarian;

    @Column(nullable = false)
    private Double rating = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PriceRange priceRange;

    private String openingHours;

    @Column
    private LocalTime openingTime;

    @Column
    private LocalTime closingTime;

    @Column
    private LocalTime secondOpeningTime;

    @Column
    private LocalTime secondClosingTime;

    @ElementCollection(targetClass = DayOfWeek.class, fetch = FetchType.EAGER)
    @CollectionTable(
            name = "restaurant_closed_days",
            joinColumns = @JoinColumn(name = "restaurant_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private Set<DayOfWeek> closedDays = new HashSet<>();

    private String phone;

    private String website;

    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_id", nullable = false)
    private Destination destination;
}