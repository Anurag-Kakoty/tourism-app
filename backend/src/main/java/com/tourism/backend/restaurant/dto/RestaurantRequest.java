package com.tourism.backend.restaurant.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tourism.backend.restaurant.entity.Cuisine;
import com.tourism.backend.restaurant.entity.PriceRange;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class RestaurantRequest {

    @NotBlank(message = "Restaurant name is required.")
    private String name;

    private String description;

    @NotNull(message = "Cuisine is required.")
    private Cuisine cuisine;

    @NotNull(message = "Vegetarian status is required.")
    private Boolean vegetarian;

    private Double rating;

    @NotNull(message = "Price range is required.")
    private PriceRange priceRange;

    @Schema(
            description = "Legacy textual opening hours representation. Optional.",
            example = "08:00-22:00"
    )
    private String openingHours;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm[:ss]")
    @Schema(
            description = "Daily opening time in 24-hour format (e.g. 11:30:00). Optional.",
            example = "11:30:00"
    )
    private LocalTime openingTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm[:ss]")
    @Schema(
            description = "Daily closing time in 24-hour format (e.g. 15:00:00). Optional.",
            example = "15:00:00"
    )
    private LocalTime closingTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm[:ss]")
    @Schema(
            description = "Second opening time for split sessions in 24-hour format (e.g. 18:30:00). Optional.",
            example = "18:30:00"
    )
    private LocalTime secondOpeningTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm[:ss]")
    @Schema(
            description = "Second closing time for split sessions in 24-hour format (e.g. 23:00:00). Optional.",
            example = "23:00:00"
    )
    private LocalTime secondClosingTime;

    @Schema(
            description = "Days of the week when the restaurant is closed. Optional.",
            example = "[\"MONDAY\"]"
    )
    private Set<DayOfWeek> closedDays = new HashSet<>();

    private String phone;

    private String website;

    private String imageUrl;

    @NotNull(message = "Destination is required.")
    private Long destinationId;
}