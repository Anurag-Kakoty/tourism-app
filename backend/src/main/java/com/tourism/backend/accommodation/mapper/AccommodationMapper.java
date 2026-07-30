package com.tourism.backend.accommodation.mapper;

import com.tourism.backend.accommodation.dto.AccommodationRequest;
import com.tourism.backend.accommodation.dto.AccommodationResponse;
import com.tourism.backend.accommodation.entity.Accommodation;
import com.tourism.backend.destination.entity.Destination;
import org.springframework.stereotype.Component;

@Component
public class AccommodationMapper {

    public Accommodation toEntity(
            AccommodationRequest request,
            Destination destination) {

        Accommodation accommodation = new Accommodation();

        accommodation.setName(request.getName());
        accommodation.setDescription(request.getDescription());
        accommodation.setType(request.getType());
        accommodation.setPricePerNight(request.getPricePerNight());
        accommodation.setRating(request.getRating());
        accommodation.setContactNumber(request.getContactNumber());
        accommodation.setEmail(request.getEmail());
        accommodation.setWebsite(request.getWebsite());
        accommodation.setAddress(request.getAddress());
        accommodation.setLatitude(request.getLatitude());
        accommodation.setLongitude(request.getLongitude());
        accommodation.setImageUrl(request.getImageUrl());
        accommodation.setAvailable(
                request.getAvailable() != null
                        ? request.getAvailable()
                        : true
        );
        accommodation.setDestination(destination);

        return accommodation;
    }

    public void updateEntity(
            Accommodation accommodation,
            AccommodationRequest request,
            Destination destination) {

        accommodation.setName(request.getName());
        accommodation.setDescription(request.getDescription());
        accommodation.setType(request.getType());
        accommodation.setPricePerNight(request.getPricePerNight());
        accommodation.setRating(request.getRating());
        accommodation.setContactNumber(request.getContactNumber());
        accommodation.setEmail(request.getEmail());
        accommodation.setWebsite(request.getWebsite());
        accommodation.setAddress(request.getAddress());
        accommodation.setLatitude(request.getLatitude());
        accommodation.setLongitude(request.getLongitude());
        accommodation.setImageUrl(request.getImageUrl());
        accommodation.setAvailable(
                request.getAvailable() != null
                        ? request.getAvailable()
                        : true
        );
        accommodation.setDestination(destination);
    }

    public AccommodationResponse toResponse(
            Accommodation accommodation) {

        AccommodationResponse response = new AccommodationResponse();

        response.setId(accommodation.getId());
        response.setName(accommodation.getName());
        response.setDescription(accommodation.getDescription());
        response.setType(accommodation.getType());
        response.setPricePerNight(accommodation.getPricePerNight());
        response.setRating(accommodation.getRating());
        response.setContactNumber(accommodation.getContactNumber());
        response.setEmail(accommodation.getEmail());
        response.setWebsite(accommodation.getWebsite());
        response.setAddress(accommodation.getAddress());
        response.setLatitude(accommodation.getLatitude());
        response.setLongitude(accommodation.getLongitude());
        response.setImageUrl(accommodation.getImageUrl());
        response.setAvailable(accommodation.getAvailable());

        response.setDestinationId(accommodation.getDestination().getId());
        response.setDestinationName(accommodation.getDestination().getName());

        response.setStateId(accommodation.getDestination().getState().getId());
        response.setStateName(accommodation.getDestination().getState().getName());

        return response;
    }
}