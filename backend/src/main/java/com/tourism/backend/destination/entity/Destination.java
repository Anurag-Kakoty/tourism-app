package com.tourism.backend.destination.entity;

import com.tourism.backend.state.entity.State;
import com.tourism.backend.util.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "destinations")
@Getter
@Setter
@NoArgsConstructor
public class Destination extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false,length=100)
    private String district;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false, length = 150)
    private String bestSeason;

    @Column(precision = 10, scale = 2)
    private BigDecimal entryFee;

    @Column(length=500)
    private String thumbnailUrl;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "state_id", nullable = false)
    private State state;
}