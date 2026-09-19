package com.tourism.backend.ai.dto.candidate;

import com.tourism.backend.restaurant.entity.Cuisine;
import com.tourism.backend.restaurant.entity.PriceRange;
import lombok.Builder;
import lombok.Getter;

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
}