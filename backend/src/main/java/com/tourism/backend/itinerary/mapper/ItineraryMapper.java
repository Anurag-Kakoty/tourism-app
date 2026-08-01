package com.tourism.backend.itinerary.mapper;

import com.tourism.backend.destination.entity.Destination;
import com.tourism.backend.itinerary.dto.ItineraryRequest;
import com.tourism.backend.itinerary.dto.ItineraryResponse;
import com.tourism.backend.itinerary.entity.Itinerary;
import org.springframework.stereotype.Component;

@Component
public class ItineraryMapper {

    public Itinerary toEntity(
            ItineraryRequest request,
            Destination destination) {

        Itinerary itinerary = new Itinerary();

        itinerary.setTitle(request.getTitle());
        itinerary.setDescription(request.getDescription());
        itinerary.setStartDate(request.getStartDate());
        itinerary.setEndDate(request.getEndDate());
        itinerary.setNumberOfTravelers(request.getNumberOfTravelers());
        itinerary.setEstimatedBudget(request.getEstimatedBudget());

        itinerary.setDestination(destination);

        return itinerary;
    }

    public void updateEntity(
            Itinerary itinerary,
            ItineraryRequest request,
            Destination destination) {

        itinerary.setTitle(request.getTitle());
        itinerary.setDescription(request.getDescription());
        itinerary.setStartDate(request.getStartDate());
        itinerary.setEndDate(request.getEndDate());
        itinerary.setNumberOfTravelers(request.getNumberOfTravelers());
        itinerary.setEstimatedBudget(request.getEstimatedBudget());

        itinerary.setDestination(destination);
    }

    public ItineraryResponse toResponse(Itinerary itinerary) {

        ItineraryResponse response = new ItineraryResponse();

        response.setId(itinerary.getId());

        response.setTitle(itinerary.getTitle());
        response.setDescription(itinerary.getDescription());

        response.setStartDate(itinerary.getStartDate());
        response.setEndDate(itinerary.getEndDate());

        response.setNumberOfTravelers(
                itinerary.getNumberOfTravelers());

        response.setEstimatedBudget(
                itinerary.getEstimatedBudget());

        response.setDestinationId(
                itinerary.getDestination().getId());

        response.setDestinationName(
                itinerary.getDestination().getName());

        response.setStateId(
                itinerary.getDestination().getState().getId());

        response.setStateName(
                itinerary.getDestination().getState().getName());

        return response;
    }
}