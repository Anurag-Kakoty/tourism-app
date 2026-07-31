package com.tourism.backend.restaurant.controller;

import com.tourism.backend.constants.ApiPaths;
import com.tourism.backend.restaurant.dto.RestaurantRequest;
import com.tourism.backend.restaurant.dto.RestaurantResponse;
import com.tourism.backend.restaurant.entity.Cuisine;
import com.tourism.backend.restaurant.entity.PriceRange;
import com.tourism.backend.restaurant.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.RESTAURANTS)
@RequiredArgsConstructor
@Tag(name = "Restaurant", description = "Restaurant Management APIs")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new restaurant")
    public RestaurantResponse createRestaurant(
            @Valid @RequestBody RestaurantRequest request) {

        return restaurantService.createRestaurant(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing restaurant")
    public RestaurantResponse updateRestaurant(
            @PathVariable Long id,
            @Valid @RequestBody RestaurantRequest request) {

        return restaurantService.updateRestaurant(id, request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get restaurant by ID")
    public RestaurantResponse getRestaurantById(@PathVariable Long id) {

        return restaurantService.getRestaurantById(id);
    }

    @GetMapping
    @Operation(
            summary = "Get restaurants",
            description = "Returns all restaurants or filters by a single optional parameter. If multiple filters are provided, only the first applicable filter is used."
    )
    public List<RestaurantResponse> getRestaurants(

            @RequestParam(required = false) Long destinationId,
            @RequestParam(required = false) Cuisine cuisine,
            @RequestParam(required = false) Boolean vegetarian,
            @RequestParam(required = false) PriceRange priceRange) {

        if (destinationId != null) {
            return restaurantService.getRestaurantsByDestination(destinationId);
        }

        if (cuisine != null) {
            return restaurantService.getRestaurantsByCuisine(cuisine);
        }

        if (vegetarian != null) {
            return restaurantService.getRestaurantsByVegetarian(vegetarian);
        }

        if (priceRange != null) {
            return restaurantService.getRestaurantsByPriceRange(priceRange);
        }

        return restaurantService.getAllRestaurants();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete restaurant")
    public void deleteRestaurant(@PathVariable Long id) {

        restaurantService.deleteRestaurant(id);
    }
}