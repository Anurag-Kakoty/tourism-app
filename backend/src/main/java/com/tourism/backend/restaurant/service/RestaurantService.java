package com.tourism.backend.restaurant.service;

import com.tourism.backend.restaurant.dto.RestaurantRequest;
import com.tourism.backend.restaurant.dto.RestaurantResponse;
import com.tourism.backend.restaurant.entity.Cuisine;
import com.tourism.backend.restaurant.entity.PriceRange;

import java.util.List;

public interface RestaurantService {

    RestaurantResponse createRestaurant(RestaurantRequest request);

    RestaurantResponse updateRestaurant(Long id, RestaurantRequest request);

    RestaurantResponse getRestaurantById(Long id);

    List<RestaurantResponse> getAllRestaurants();

    List<RestaurantResponse> getRestaurantsByDestination(Long destinationId);

    List<RestaurantResponse> getRestaurantsByCuisine(Cuisine cuisine);

    List<RestaurantResponse> getRestaurantsByVegetarian(Boolean vegetarian);

    List<RestaurantResponse> getRestaurantsByPriceRange(PriceRange priceRange);

    void deleteRestaurant(Long id);
}