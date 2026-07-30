package com.tourism.backend.guide.mapper;

import com.tourism.backend.destination.entity.Destination;
import com.tourism.backend.guide.dto.GuideRequest;
import com.tourism.backend.guide.dto.GuideResponse;
import com.tourism.backend.guide.entity.Guide;
import org.springframework.stereotype.Component;

@Component
public class GuideMapper {

    public Guide toEntity(GuideRequest request, Destination destination) {

        Guide guide = new Guide();

        guide.setName(request.getName());
        guide.setBio(request.getBio());
        guide.setPhone(request.getPhone());
        guide.setEmail(request.getEmail());
        guide.setLanguages(request.getLanguages());
        guide.setYearsOfExperience(request.getYearsOfExperience());
        guide.setPricePerDay(request.getPricePerDay());

        guide.setRating(
                request.getRating() != null ? request.getRating() : 0.0
        );

        guide.setAvailable(
                request.getAvailable() != null ? request.getAvailable() : true
        );

        guide.setLicenseNumber(request.getLicenseNumber());

        guide.setProvidesTransport(
                request.getProvidesTransport() != null
                        ? request.getProvidesTransport()
                        : false
        );

        guide.setImageUrl(request.getImageUrl());

        guide.setDestination(destination);

        return guide;
    }

    public GuideResponse toResponse(Guide guide) {

        GuideResponse response = new GuideResponse();

        response.setId(guide.getId());
        response.setName(guide.getName());
        response.setBio(guide.getBio());
        response.setPhone(guide.getPhone());
        response.setEmail(guide.getEmail());
        response.setLanguages(guide.getLanguages());
        response.setYearsOfExperience(guide.getYearsOfExperience());
        response.setPricePerDay(guide.getPricePerDay());
        response.setRating(guide.getRating());
        response.setAvailable(guide.getAvailable());
        response.setLicenseNumber(guide.getLicenseNumber());
        response.setProvidesTransport(guide.getProvidesTransport());
        response.setImageUrl(guide.getImageUrl());

        response.setDestinationId(guide.getDestination().getId());
        response.setDestinationName(guide.getDestination().getName());

        response.setStateId(
                guide.getDestination().getState().getId()
        );

        response.setStateName(
                guide.getDestination().getState().getName()
        );

        return response;
    }
}