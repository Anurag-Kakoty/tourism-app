package com.tourism.backend.ai.dto.candidate;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Getter
@Builder
public class AttractionCandidate {

    private Long id;

    private String name;

    private String description;

    private String bestSeason;

    private BigDecimal entryFee;

    private Boolean featured;

    private Double latitude;

    private Double longitude;

    private LocalTime openingTime;

    private LocalTime closingTime;

    private Set<DayOfWeek> closedDays;

    private List<Long> experienceIds;

    private List<String> experiences;

    private List<String> tags;
}