package com.tourism.backend.destination.specification;

import com.tourism.backend.destination.entity.Destination;
import com.tourism.backend.destination.entity.DestinationType;
import org.springframework.data.jpa.domain.Specification;

public final class DestinationSpecification {

    private DestinationSpecification() {
    }

    public static Specification<Destination> hasStateId(Long stateId) {

        return (root, query, criteriaBuilder) -> {

            if (stateId == null) {
                return null;
            }

            return criteriaBuilder.equal(
                    root.get("state").get("id"),
                    stateId
            );
        };
    }

    public static Specification<Destination> hasType(
            DestinationType type) {

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

    public static Specification<Destination> isFeatured(
            Boolean featured) {

        return (root, query, criteriaBuilder) -> {

            if (featured == null) {
                return null;
            }

            return criteriaBuilder.equal(
                    root.get("featured"),
                    featured
            );
        };
    }

    public static Specification<Destination> isPopular(
            Boolean popular) {

        return (root, query, criteriaBuilder) -> {

            if (popular == null) {
                return null;
            }

            return criteriaBuilder.equal(
                    root.get("popular"),
                    popular
            );
        };
    }
}