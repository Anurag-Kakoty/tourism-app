package com.tourism.backend.destination.mapper;

import com.tourism.backend.destination.dto.DestinationRequest;
import com.tourism.backend.destination.dto.DestinationResponse;
import com.tourism.backend.destination.entity.Destination;
import com.tourism.backend.state.entity.State;
import com.tourism.backend.tag.entity.Tag;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DestinationMapper {

    public Destination toEntity(
            DestinationRequest request,
            State state,
            Set<Tag> tags) {

        Destination destination = new Destination();

        updateEntity(destination, request, state, tags);

        return destination;
    }

    public void updateEntity(
            Destination destination,
            DestinationRequest request,
            State state,
            Set<Tag> tags) {

        destination.setName(request.getName());
        destination.setDescription(request.getDescription());
        destination.setDistrict(request.getDistrict());
        destination.setLatitude(request.getLatitude());
        destination.setLongitude(request.getLongitude());
        destination.setBestSeason(request.getBestSeason());
        destination.setEntryFee(request.getEntryFee());
        destination.setThumbnailUrl(request.getThumbnailUrl());

        destination.setState(state);
        destination.setTags(tags);
    }

    public DestinationResponse toResponse(Destination destination) {

        DestinationResponse response = new DestinationResponse();

        response.setId(destination.getId());
        response.setName(destination.getName());
        response.setDescription(destination.getDescription());
        response.setDistrict(destination.getDistrict());
        response.setLatitude(destination.getLatitude());
        response.setLongitude(destination.getLongitude());
        response.setBestSeason(destination.getBestSeason());
        response.setEntryFee(destination.getEntryFee());
        response.setThumbnailUrl(destination.getThumbnailUrl());

        response.setStateId(destination.getState().getId());
        response.setStateName(destination.getState().getName());

        response.setTagIds(
                destination.getTags()
                        .stream()
                        .map(Tag::getId)
                        .collect(Collectors.toSet())
        );

        response.setTagNames(
                destination.getTags()
                        .stream()
                        .map(Tag::getName)
                        .collect(Collectors.toSet())
        );

        return response;
    }
}