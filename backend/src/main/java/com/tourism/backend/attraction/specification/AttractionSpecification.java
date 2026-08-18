package com.tourism.backend.attraction.specification;

import com.tourism.backend.attraction.entity.Attraction;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public final class AttractionSpecification {

    private AttractionSpecification() {
    }

    public static Specification<Attraction> hasStateId(Long stateId) {

        return (root, query, criteriaBuilder) -> {

            if (stateId == null) {
                return null;
            }

            return criteriaBuilder.equal(
                    root.get("destination")
                            .get("state")
                            .get("id"),
                    stateId
            );
        };
    }

    public static Specification<Attraction> hasDestinationId(
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

    public static Specification<Attraction> hasExperienceId(
            Long experienceId) {

        return (root, query, criteriaBuilder) -> {

            if (experienceId == null) {
                return null;
            }

            Join<Attraction, ?> experiences =
                    root.join("experiences", JoinType.INNER);

            return criteriaBuilder.equal(
                    experiences.get("id"),
                    experienceId
            );
        };
    }

    public static Specification<Attraction> hasTagId(
            Long tagId) {

        return (root, query, criteriaBuilder) -> {

            if (tagId == null) {
                return null;
            }

            Join<Attraction, ?> tags =
                    root.join("tags", JoinType.INNER);

            return criteriaBuilder.equal(
                    tags.get("id"),
                    tagId
            );
        };
    }

    public static Specification<Attraction> isFeatured(
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
}