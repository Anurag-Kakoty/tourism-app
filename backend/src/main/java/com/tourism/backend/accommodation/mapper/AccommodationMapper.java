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

        return Accommodation.builder()
                .name(request.getName())
                .description(request.getDescription())
                .type(request.getType())
                .pricePerNight(request.getPricePerNight())
                .rating(request.getRating())
                .contactNumber(request.getContactNumber())
                .email(request.getEmail())
                .website(request.getWebsite())
                .address(request.getAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .imageUrl(request.getImageUrl())
                .available(request.getAvailable() != null
                        ? request.getAvailable()
                        : true)
                .destination(destination)
                .build();
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

        return AccommodationResponse.builder()
                .id(accommodation.getId())
                .name(accommodation.getName())
                .description(accommodation.getDescription())
                .type(accommodation.getType())
                .pricePerNight(accommodation.getPricePerNight())
                .rating(accommodation.getRating())
                .contactNumber(accommodation.getContactNumber())
                .email(accommodation.getEmail())
                .website(accommodation.getWebsite())
                .address(accommodation.getAddress())
                .latitude(accommodation.getLatitude())
                .longitude(accommodation.getLongitude())
                .imageUrl(accommodation.getImageUrl())
                .available(accommodation.getAvailable())
                .destinationId(accommodation.getDestination().getId())
                .destinationName(accommodation.getDestination().getName())
                .build();
    }
}