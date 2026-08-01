package com.tourism.backend.itinerary.repository;

import com.tourism.backend.itinerary.entity.ItineraryItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItineraryItemRepository
        extends JpaRepository<ItineraryItem, Long> {

    List<ItineraryItem> findAllByItinerary_IdOrderByDayNumberAscActivityOrderAsc(
            Long itineraryId
    );
}