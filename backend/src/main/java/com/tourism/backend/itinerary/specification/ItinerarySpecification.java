package com.tourism.backend.itinerary.specification;

import com.tourism.backend.itinerary.entity.Itinerary;
import org.springframework.data.jpa.domain.Specification;

public final class ItinerarySpecification {

    private ItinerarySpecification() {
    }

    public static Specification<Itinerary> hasUserId(Long userId) {

        return (root, query, criteriaBuilder) -> {

            if (userId == null) {
                return null;
            }

            return criteriaBuilder.equal(
                    root.get("user").get("id"),
                    userId
            );
        };
    }

    public static Specification<Itinerary> hasDestinationId(
            Long destinationId) {

        return (root, query, criteriaBuilder) -> {

            if (destinationId == null) {
                return null;
            }

            return criteriaBuilder.equal(
                    root.get("destination").get("id"),
                    destinationId
            );
        };
    }
}