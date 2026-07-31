package com.tourism.backend.transport.entity;

import com.tourism.backend.destination.entity.Destination;
import com.tourism.backend.util.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "transport_options")
@Getter
@Setter
@NoArgsConstructor
public class Transport extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransportType type;

    @Column(nullable = false, length = 150)
    private String providerName;

    @Column(nullable = false, length = 200)
    private String pickupLocation;

    @Column(nullable = false, length = 200)
    private String dropLocation;

    @Column(nullable = false, length = 100)
    private String estimatedDuration;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal estimatedFare;

    @Column(length = 20)
    private String contactNumber;

    @Column(length = 300)
    private String website;

    @Column(length = 500)
    private String bookingUrl;

    @Column(nullable = false)
    private Boolean available = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "destination_id", nullable = false)
    private Destination destination;
}