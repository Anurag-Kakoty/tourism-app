package com.tourism.backend.destination.mapper;

import com.tourism.backend.destination.dto.DestinationRequest;
import com.tourism.backend.destination.dto.DestinationResponse;
import com.tourism.backend.destination.entity.Destination;
import com.tourism.backend.state.entity.State;
import org.springframework.stereotype.Component;

@Component
public class DestinationMapper {

    public Destination toEntity(DestinationRequest request, State state) {

        Destination destination = new Destination();

        updateEntity(destination, request, state);

        return destination;
    }

    public void updateEntity(Destination destination,
                             DestinationRequest request,
                             State state) {

        destination.setName(request.getName());
        destination.setDescription(request.getDescription());
        destination.setDistrict(request.getDistrict());
        destination.setLatitude(request.getLatitude());
        destination.setLongitude(request.getLongitude());
        destination.setBestSeason(request.getBestSeason());
        destination.setEntryFee(request.getEntryFee());
        destination.setThumbnailUrl(request.getThumbnailUrl());

        destination.setState(state);
    }

    public DestinationResponse toResponse(Destination destination) {

        return DestinationResponse.builder()
                .id(destination.getId())
                .name(destination.getName())
                .description(destination.getDescription())
                .district(destination.getDistrict())
                .latitude(destination.getLatitude())
                .longitude(destination.getLongitude())
                .bestSeason(destination.getBestSeason())
                .entryFee(destination.getEntryFee())
                .thumbnailUrl(destination.getThumbnailUrl())
                .stateId(destination.getState().getId())
                .stateName(destination.getState().getName())
                .build();
    }
}