package com.tourism.backend.ai.dto.candidate;

import com.tourism.backend.accommodation.entity.AccommodationType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalTime;

@Getter
@Builder
public class AccommodationCandidate {

    private Long id;
    private String name;
    private String description;
    private AccommodationType type;
    private BigDecimal pricePerNight;
    private Double rating;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
}