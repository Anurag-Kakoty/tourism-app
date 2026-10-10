package com.tourism.backend.attraction.entity;

import com.tourism.backend.util.BaseEntity;
import com.tourism.backend.destination.entity.Destination;
import com.tourism.backend.experience.entity.Experience;
import com.tourism.backend.tag.entity.Tag;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "attractions")
@Getter
@Setter
@NoArgsConstructor
public class Attraction extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false, length = 150)
    private String bestSeason;

    @Column(precision = 10, scale = 2)
    private BigDecimal entryFee;

    @Column(length = 500)
    private String thumbnailUrl;

    @Column(nullable = false)
    private Boolean featured = false;

    @Column(nullable = false)
    private Integer displayOrder = 0;

    /**
     * Daily opening time (e.g. 09:00:00).
     * Null indicates that operating hours are unverified or unrestricted (e.g. open 24/7).
     * Assumes uniform opening hours across all open days of the week.
     * Note: For overnight hours (openingTime > closingTime), v1 closed-day validation evaluates
     * against the activity's calendar date rather than the shift-start date.
     */
    @Column
    private LocalTime openingTime;

    /**
     * Daily closing time (e.g. 17:30:00).
     * Null indicates that operating hours are unverified or unrestricted.
     * Assumes uniform closing hours across all open days of the week.
     */
    @Column
    private LocalTime closingTime;

    /**
     * Days of the week when the attraction is closed.
     * An empty set indicates no known closed days.
     */
    @ElementCollection(targetClass = DayOfWeek.class, fetch = FetchType.EAGER)
    @CollectionTable(
            name = "attraction_closed_days",
            joinColumns = @JoinColumn(name = "attraction_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private Set<DayOfWeek> closedDays = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "destination_id", nullable = false)
    private Destination destination;

    @ManyToMany
    @JoinTable(
            name = "attraction_tags",
            joinColumns = @JoinColumn(name = "attraction_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "attraction_experiences",
            joinColumns = @JoinColumn(name = "attraction_id"),
            inverseJoinColumns = @JoinColumn(name = "experience_id")
    )
    private Set<Experience> experiences = new HashSet<>();
}