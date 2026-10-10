package com.tourism.backend.restaurant.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tourism.backend.restaurant.entity.Cuisine;
import com.tourism.backend.restaurant.entity.PriceRange;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class RestaurantResponse {

    private Long id;

    private String name;

    private String description;

    private Cuisine cuisine;

    private Boolean vegetarian;

    private Double rating;

    private PriceRange priceRange;

    @Schema(
            description = "Legacy textual opening hours representation",
            example = "08:00-22:00"
    )
    private String openingHours;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    @Schema(
            description = "Daily opening time in 24-hour format",
            example = "11:30:00"
    )
    private LocalTime openingTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    @Schema(
            description = "Daily closing time in 24-hour format",
            example = "15:00:00"
    )
    private LocalTime closingTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    @Schema(
            description = "Second opening time for split sessions in 24-hour format",
            example = "18:30:00"
    )
    private LocalTime secondOpeningTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    @Schema(
            description = "Second closing time for split sessions in 24-hour format",
            example = "23:00:00"
    )
    private LocalTime secondClosingTime;

    @Schema(
            description = "Days of the week when the restaurant is closed",
            example = "[\"MONDAY\"]"
    )
    private Set<DayOfWeek> closedDays = new HashSet<>();

    private String phone;

    private String website;

    private String imageUrl;

    private Long destinationId;

    private String destinationName;

    private Long stateId;

    private String stateName;
}