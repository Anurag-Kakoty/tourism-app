package com.tourism.backend.restaurant.dto;

import com.tourism.backend.restaurant.entity.Cuisine;
import com.tourism.backend.restaurant.entity.PriceRange;
import lombok.Getter;
import lombok.Setter;

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

    private String openingHours;

    private String phone;

    private String website;

    private String imageUrl;

    private Long destinationId;

    private String destinationName;

    private Long stateId;

    private String stateName;
}