package com.tourism.backend.transport.repository;

import com.tourism.backend.transport.entity.Transport;
import com.tourism.backend.transport.entity.TransportType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransportRepository
        extends JpaRepository<Transport, Long> {

    @EntityGraph(attributePaths = {
            "destination",
            "destination.state"
    })
    Optional<Transport> findWithDestinationById(Long id);

    @EntityGraph(attributePaths = {
            "destination",
            "destination.state"
    })
    List<Transport> findAllByOrderByTypeAscProviderNameAsc();

    @EntityGraph(attributePaths = {
            "destination",
            "destination.state"
    })
    List<Transport> findAllByDestination_IdOrderByTypeAscProviderNameAsc(
            Long destinationId
    );

    @EntityGraph(attributePaths = {
            "destination",
            "destination.state"
    })
    List<Transport> findAllByTypeOrderByProviderNameAsc(
            TransportType type
    );

    @EntityGraph(attributePaths = {
            "destination",
            "destination.state"
    })
    List<Transport> findAllByAvailableOrderByTypeAscProviderNameAsc(
            Boolean available
    );

    boolean existsByProviderNameIgnoreCaseAndDestination_Id(
            String providerName,
            Long destinationId
    );

    boolean existsByProviderNameIgnoreCaseAndDestination_IdAndIdNot(
            String providerName,
            Long destinationId,
            Long id
    );
}