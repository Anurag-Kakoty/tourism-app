package com.tourism.backend.itinerary.repository;

import com.tourism.backend.itinerary.entity.Itinerary;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ItineraryRepository
        extends JpaRepository<Itinerary, Long>,
        JpaSpecificationExecutor<Itinerary> {

    @EntityGraph(attributePaths = {
            "destination",
            "destination.state",
            "items"
    })
    Optional<Itinerary> findWithItemsById(Long id);

    @EntityGraph(attributePaths = {
            "destination",
            "destination.state",
            "items"
    })
    Optional<Itinerary> findWithItemsByIdAndUser_Id(
            Long id,
            Long userId
    );

    @EntityGraph(attributePaths = {
            "destination",
            "destination.state",
            "items"
    })
    List<Itinerary> findAllBy();

    @EntityGraph(attributePaths = {
            "destination",
            "destination.state",
            "items"
    })
    List<Itinerary> findAllByDestination_Id(Long destinationId);
}