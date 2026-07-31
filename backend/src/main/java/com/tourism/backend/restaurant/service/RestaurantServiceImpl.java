package com.tourism.backend.restaurant.service;

import com.tourism.backend.destination.entity.Destination;
import com.tourism.backend.destination.repository.DestinationRepository;
import com.tourism.backend.exception.DuplicateResourceException;
import com.tourism.backend.exception.ResourceNotFoundException;
import com.tourism.backend.restaurant.dto.RestaurantRequest;
import com.tourism.backend.restaurant.dto.RestaurantResponse;
import com.tourism.backend.restaurant.entity.Cuisine;
import com.tourism.backend.restaurant.entity.PriceRange;
import com.tourism.backend.restaurant.entity.Restaurant;
import com.tourism.backend.restaurant.mapper.RestaurantMapper;
import com.tourism.backend.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final DestinationRepository destinationRepository;
    private final RestaurantMapper restaurantMapper;

    @Override
    public RestaurantResponse createRestaurant(RestaurantRequest request) {

        if (request.getPhone() != null &&
                restaurantRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException("Phone number already exists.");
        }

        Destination destination = destinationRepository.findById(request.getDestinationId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination not found."));

        Restaurant restaurant = restaurantMapper.toEntity(request, destination);

        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        log.info("Restaurant created with ID {}", savedRestaurant.getId());

        return restaurantMapper.toResponse(savedRestaurant);
    }

    @Override
    public RestaurantResponse updateRestaurant(Long id, RestaurantRequest request) {

        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found."));

        if (request.getPhone() != null &&
                restaurantRepository.existsByPhoneAndIdNot(request.getPhone(), id)) {
            throw new DuplicateResourceException("Phone number already exists.");
        }

        Destination destination = destinationRepository.findById(request.getDestinationId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination not found."));

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

        Restaurant updatedRestaurant = restaurantRepository.save(restaurant);

        log.info("Restaurant updated with ID {}", id);

        return restaurantMapper.toResponse(updatedRestaurant);
    }

    @Override
    @Transactional(readOnly = true)
    public RestaurantResponse getRestaurantById(Long id) {

        Restaurant restaurant = restaurantRepository.findWithDestinationById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found."));

        return restaurantMapper.toResponse(restaurant);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantResponse> getAllRestaurants() {

        return restaurantRepository.findAllByOrderByRatingDescNameAsc()
                .stream()
                .map(restaurantMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantResponse> getRestaurantsByDestination(Long destinationId) {

        return restaurantRepository.findAllByDestination_IdOrderByRatingDescNameAsc(destinationId)
                .stream()
                .map(restaurantMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantResponse> getRestaurantsByCuisine(Cuisine cuisine) {

        return restaurantRepository.findAllByCuisineOrderByRatingDescNameAsc(cuisine)
                .stream()
                .map(restaurantMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantResponse> getRestaurantsByVegetarian(Boolean vegetarian) {

        return restaurantRepository.findAllByVegetarianOrderByRatingDescNameAsc(vegetarian)
                .stream()
                .map(restaurantMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantResponse> getRestaurantsByPriceRange(PriceRange priceRange) {

        return restaurantRepository.findAllByPriceRangeOrderByRatingDescNameAsc(priceRange)
                .stream()
                .map(restaurantMapper::toResponse)
                .toList();
    }

    @Override
    public void deleteRestaurant(Long id) {

        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found."));

        restaurantRepository.delete(restaurant);

        log.info("Restaurant deleted with ID {}", id);
    }
}