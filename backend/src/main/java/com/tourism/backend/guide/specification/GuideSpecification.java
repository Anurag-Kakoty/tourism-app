package com.tourism.backend.guide.specification;

import com.tourism.backend.guide.entity.Guide;
import com.tourism.backend.guide.entity.Language;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public final class GuideSpecification {

    private GuideSpecification() {
    }

    /**
     * Fetches all relationships required by GuideResponse.
     *
     * This prevents LazyInitializationException when the DTO mapper
     * accesses destination, state, and languages.
     */
    public static Specification<Guide> fetchRequiredRelations() {

        return (root, query, criteriaBuilder) -> {

            if (query.getResultType() != Long.class
                    && query.getResultType() != long.class) {

                root.fetch("destination", JoinType.LEFT)
                        .fetch("state", JoinType.LEFT);

                root.fetch("languages", JoinType.LEFT);

                query.distinct(true);
            }

            return null;
        };
    }

    public static Specification<Guide> hasDestinationId(
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

    public static Specification<Guide> isAvailable(
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

    public static Specification<Guide> providesTransport(
            Boolean providesTransport) {

        return (root, query, criteriaBuilder) -> {

            if (providesTransport == null) {
                return null;
            }

            return criteriaBuilder.equal(
                    root.get("providesTransport"),
                    providesTransport
            );
        };
    }

    public static Specification<Guide> hasLanguage(
            Language language) {

        return (root, query, criteriaBuilder) -> {

            if (language == null) {
                return null;
            }

            Join<Guide, Language> languages =
                    root.join("languages", JoinType.INNER);

            return criteriaBuilder.equal(
                    languages,
                    language
            );
        };
    }
}