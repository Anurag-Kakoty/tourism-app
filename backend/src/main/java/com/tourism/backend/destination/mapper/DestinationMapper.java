package com.tourism.backend.destination.mapper;

import com.tourism.backend.destination.dto.DestinationRequest;
import com.tourism.backend.destination.dto.DestinationResponse;
import com.tourism.backend.destination.entity.Destination;
import com.tourism.backend.state.entity.State;
import org.springframework.stereotype.Component;

@Component
public class DestinationMapper {

    public Destination toEntity(
            DestinationRequest request,
            State state) {

        Destination destination = new Destination();
        updateEntity(destination, request, state);
        return destination;
    }

    public void updateEntity(
            Destination destination,
            DestinationRequest request,
            State state) {

        destination.setName(request.getName());
        destination.setTagline(request.getTagline());
        destination.setDescription(request.getDescription());
        destination.setDistrict(request.getDistrict());
        destination.setState(state);
        destination.setType(request.getType());
        destination.setLatitude(request.getLatitude());
        destination.setLongitude(request.getLongitude());
        destination.setThumbnailUrl(request.getThumbnailUrl());
        destination.setCoverImageUrl(request.getCoverImageUrl());
        destination.setFeatured(request.getFeatured());
        destination.setPopular(request.getPopular());
        destination.setDisplayOrder(request.getDisplayOrder());

        destination.setCoverImageUrl(request.getCoverImageUrl());
        destination.setTimezone(request.getTimezone());
        destination.setNearestAirport(request.getNearestAirport());
        destination.setNearestRailwayStation(request.getNearestRailwayStation());

    }

    public DestinationResponse toResponse(
            Destination destination) {

        DestinationResponse response = new DestinationResponse();

        response.setId(destination.getId());
        response.setName(destination.getName());
        response.setTagline(destination.getTagline());
        response.setDescription(destination.getDescription());
        response.setDistrict(destination.getDistrict());

        response.setStateId(destination.getState().getId());
        response.setStateName(destination.getState().getName());

        response.setType(destination.getType());
        response.setLatitude(destination.getLatitude());
        response.setLongitude(destination.getLongitude());

        response.setThumbnailUrl(destination.getThumbnailUrl());
        response.setCoverImageUrl(destination.getCoverImageUrl());

        response.setFeatured(destination.getFeatured());
        response.setPopular(destination.getPopular());
        response.setDisplayOrder(destination.getDisplayOrder());

        response.setCoverImageUrl(destination.getCoverImageUrl());
        response.setTimezone(destination.getTimezone());
        response.setNearestAirport(destination.getNearestAirport());
        response.setNearestRailwayStation(destination.getNearestRailwayStation());

        return response;
    }
}