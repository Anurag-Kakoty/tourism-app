package com.tourism.backend.restaurant.dto;

import com.tourism.backend.restaurant.entity.Cuisine;
import com.tourism.backend.restaurant.entity.PriceRange;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

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

    private String openingHours;

    private String phone;

    private String website;

    private String imageUrl;

    @NotNull(message = "Destination is required.")
    private Long destinationId;
}