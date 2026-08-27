package com.tourism.backend.festivaloccurrence.specification;

import com.tourism.backend.festivaloccurrence.entity.FestivalOccurrence;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class FestivalOccurrenceSpecification {

    private FestivalOccurrenceSpecification() {
    }

    public static Specification<FestivalOccurrence> hasStateId(
            Long stateId) {

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

    public static Specification<FestivalOccurrence> hasYear(
            Integer year) {

        return (root, query, criteriaBuilder) -> {

            if (year == null) {
                return null;
            }

            return criteriaBuilder.equal(
                    root.get("year"),
                    year
            );
        };
    }

    public static Specification<FestivalOccurrence> startsOnOrAfter(
            LocalDate date) {

        return (root, query, criteriaBuilder) -> {

            if (date == null) {
                return null;
            }

            return criteriaBuilder.greaterThanOrEqualTo(
                    root.get("startDate"),
                    date
            );
        };
    }
}