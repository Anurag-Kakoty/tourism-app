package com.tourism.backend.transport.specification;

import com.tourism.backend.transport.entity.Transport;
import com.tourism.backend.transport.entity.TransportType;
import org.springframework.data.jpa.domain.Specification;

public final class TransportSpecification {

    private TransportSpecification() {
    }

    public static Specification<Transport> hasDestinationId(
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

    public static Specification<Transport> hasType(
            TransportType type) {

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

    public static Specification<Transport> isAvailable(
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