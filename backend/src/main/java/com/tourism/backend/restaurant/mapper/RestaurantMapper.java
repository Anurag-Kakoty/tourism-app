package com.tourism.backend.restaurant.mapper;

import com.tourism.backend.destination.entity.Destination;
import com.tourism.backend.restaurant.dto.RestaurantRequest;
import com.tourism.backend.restaurant.dto.RestaurantResponse;
import com.tourism.backend.restaurant.entity.Restaurant;
import org.springframework.stereotype.Component;

@Component
public class RestaurantMapper {

    public Restaurant toEntity(RestaurantRequest request, Destination destination) {

        Restaurant restaurant = new Restaurant();

        restaurant.setName(request.getName());
        restaurant.setDescription(request.getDescription());
        restaurant.setCuisine(request.getCuisine());
        restaurant.setVegetarian(request.getVegetarian());
        restaurant.setRating(request.getRating() != null ? request.getRating() : 0.0);
        restaurant.setPriceRange(request.getPriceRange());
        restaurant.setOpeningHours(request.getOpeningHours());
        restaurant.setPhone(request.getPhone());
        restaurant.setWebsite(request.getWebsite());
        restaurant.setImageUrl(request.getImageUrl());
        restaurant.setDestination(destination);

        return restaurant;
    }

    public RestaurantResponse toResponse(Restaurant restaurant) {

        RestaurantResponse response = new RestaurantResponse();

        response.setId(restaurant.getId());
        response.setName(restaurant.getName());
        response.setDescription(restaurant.getDescription());
        response.setCuisine(restaurant.getCuisine());
        response.setVegetarian(restaurant.getVegetarian());
        response.setRating(restaurant.getRating());
        response.setPriceRange(restaurant.getPriceRange());
        response.setOpeningHours(restaurant.getOpeningHours());
        response.setPhone(restaurant.getPhone());
        response.setWebsite(restaurant.getWebsite());
        response.setImageUrl(restaurant.getImageUrl());

        response.setDestinationId(restaurant.getDestination().getId());
        response.setDestinationName(restaurant.getDestination().getName());

        response.setStateId(restaurant.getDestination().getState().getId());
        response.setStateName(restaurant.getDestination().getState().getName());

        return response;
    }
}