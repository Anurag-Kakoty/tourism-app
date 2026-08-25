package com.tourism.backend.accommodation.specification;

import com.tourism.backend.accommodation.entity.Accommodation;
import com.tourism.backend.accommodation.entity.AccommodationType;
import org.springframework.data.jpa.domain.Specification;

public final class AccommodationSpecification {

    private AccommodationSpecification() {
    }

    public static Specification<Accommodation> hasDestinationId(
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

    public static Specification<Accommodation> hasType(
            AccommodationType type) {

        return (root, query, criteriaBuilder) -> {

            if (type == null) {
                return null;
            }

            return criteriaBuilder.equal(
                    root.get("type"),
                    type
            );
        };
    }

    public static Specification<Accommodation> isAvailable(
            Boolean available) {

        return (root, query, criteriaBuilder) -> {

            if (available == null) {
                return null;
            }

            return criteriaBuilder.equal(
                    root.get("available"),
                    available
            );
        };
    }
}