package com.tourism.backend.ai.dto.candidate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tourism.backend.restaurant.entity.Cuisine;
import com.tourism.backend.restaurant.entity.PriceRange;
import lombok.Builder;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

@Getter
@Builder
public class RestaurantCandidate {

    private Long id;
    private String name;
    private String description;
    private Cuisine cuisine;
    private Boolean vegetarian;
    private Double rating;
    private PriceRange priceRange;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime openingTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime closingTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime secondOpeningTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime secondClosingTime;
    private Set<DayOfWeek> closedDays;
}