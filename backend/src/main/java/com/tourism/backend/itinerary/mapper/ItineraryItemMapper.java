package com.tourism.backend.itinerary.mapper;

import com.tourism.backend.itinerary.dto.ItineraryItemRequest;
import com.tourism.backend.itinerary.dto.ItineraryItemResponse;
import com.tourism.backend.itinerary.entity.Itinerary;
import com.tourism.backend.itinerary.entity.ItineraryItem;
import org.springframework.stereotype.Component;

@Component
public class ItineraryItemMapper {

    public ItineraryItem toEntity(
            ItineraryItemRequest request,
            Itinerary itinerary) {

        ItineraryItem item = new ItineraryItem();

        item.setDayNumber(request.getDayNumber());
        item.setActivityOrder(request.getActivityOrder());
        item.setTime(request.getTime());
        item.setActivityType(request.getActivityType());
        item.setReferenceId(request.getReferenceId());
        item.setNotes(request.getNotes());

        item.setItinerary(itinerary);

        return item;
    }

    public void updateEntity(
            ItineraryItem item,
            ItineraryItemRequest request) {

        item.setDayNumber(request.getDayNumber());
        item.setActivityOrder(request.getActivityOrder());
        item.setTime(request.getTime());
        item.setActivityType(request.getActivityType());
        item.setReferenceId(request.getReferenceId());
        item.setNotes(request.getNotes());
    }

    public ItineraryItemResponse toResponse(
            ItineraryItem item) {

        ItineraryItemResponse response =
                new ItineraryItemResponse();

        response.setId(item.getId());

        response.setDayNumber(item.getDayNumber());

        response.setActivityOrder(
                item.getActivityOrder());

        response.setTime(item.getTime());

        response.setActivityType(
                item.getActivityType());

        response.setReferenceId(
                item.getReferenceId());

        response.setNotes(item.getNotes());

        return response;
    }
}