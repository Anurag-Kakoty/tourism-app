package com.tourism.backend.restaurant.repository;

import com.tourism.backend.restaurant.entity.Cuisine;
import com.tourism.backend.restaurant.entity.PriceRange;
import com.tourism.backend.restaurant.entity.Restaurant;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    @EntityGraph(attributePaths = {
            "destination",
            "destination.state"
    })
    Optional<Restaurant> findWithDestinationById(Long id);

    @EntityGraph(attributePaths = {
            "destination",
            "destination.state"
    })
    List<Restaurant> findAllByOrderByRatingDescNameAsc();

    @EntityGraph(attributePaths = {
            "destination",
            "destination.state"
    })
    List<Restaurant> findAllByDestination_IdOrderByRatingDescNameAsc(Long destinationId);

    @EntityGraph(attributePaths = {
            "destination",
            "destination.state"
    })
    List<Restaurant> findAllByCuisineOrderByRatingDescNameAsc(Cuisine cuisine);

    @EntityGraph(attributePaths = {
            "destination",
            "destination.state"
    })
    List<Restaurant> findAllByVegetarianOrderByRatingDescNameAsc(Boolean vegetarian);

    @EntityGraph(attributePaths = {
            "destination",
            "destination.state"
    })
    List<Restaurant> findAllByPriceRangeOrderByRatingDescNameAsc(PriceRange priceRange);

    boolean existsByPhone(String phone);

    boolean existsByPhoneAndIdNot(String phone, Long id);
}