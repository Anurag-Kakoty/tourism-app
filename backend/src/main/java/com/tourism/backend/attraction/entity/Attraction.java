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