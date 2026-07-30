package com.tourism.backend.attraction.mapper;

import com.tourism.backend.attraction.dto.AttractionRequest;
import com.tourism.backend.attraction.dto.AttractionResponse;
import com.tourism.backend.attraction.entity.Attraction;
import com.tourism.backend.destination.entity.Destination;
import com.tourism.backend.experience.entity.Experience;
import com.tourism.backend.tag.entity.Tag;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AttractionMapper {

    public Attraction toEntity(
            AttractionRequest request,
            Destination destination,
            Set<Tag> tags,
            Set<Experience> experiences) {

        Attraction attraction = new Attraction();

        updateEntity(
                attraction,
                request,
                destination,
                tags,
                experiences
        );

        return attraction;
    }

    public void updateEntity(
            Attraction attraction,
            AttractionRequest request,
            Destination destination,
            Set<Tag> tags,
            Set<Experience> experiences) {

        attraction.setName(request.getName());
        attraction.setDescription(request.getDescription());
        attraction.setLatitude(request.getLatitude());
        attraction.setLongitude(request.getLongitude());
        attraction.setBestSeason(request.getBestSeason());
        attraction.setEntryFee(request.getEntryFee());
        attraction.setThumbnailUrl(request.getThumbnailUrl());

        attraction.setFeatured(request.getFeatured());
        attraction.setDisplayOrder(request.getDisplayOrder());

        attraction.setDestination(destination);
        attraction.setTags(tags);
        attraction.setExperiences(experiences);
    }

    public AttractionResponse toResponse(Attraction attraction) {

        AttractionResponse response = new AttractionResponse();

        response.setId(attraction.getId());
        response.setName(attraction.getName());
        response.setDescription(attraction.getDescription());
        response.setLatitude(attraction.getLatitude());
        response.setLongitude(attraction.getLongitude());
        response.setBestSeason(attraction.getBestSeason());
        response.setEntryFee(attraction.getEntryFee());
        response.setThumbnailUrl(attraction.getThumbnailUrl());

        response.setFeatured(attraction.getFeatured());
        response.setDisplayOrder(attraction.getDisplayOrder());

        response.setDestinationId(attraction.getDestination().getId());
        response.setDestinationName(attraction.getDestination().getName());

        response.setStateId(attraction.getDestination().getState().getId());
        response.setStateName(attraction.getDestination().getState().getName());

        response.setTagIds(
                attraction.getTags()
                        .stream()
                        .map(Tag::getId)
                        .collect(Collectors.toSet())
        );

        response.setTagNames(
                attraction.getTags()
                        .stream()
                        .map(Tag::getName)
                        .collect(Collectors.toSet())
        );

        response.setExperienceIds(
                attraction.getExperiences()
                        .stream()
                        .map(Experience::getId)
                        .collect(Collectors.toSet())
        );

        response.setExperienceNames(
                attraction.getExperiences()
                        .stream()
                        .map(Experience::getName)
                        .collect(Collectors.toSet())
        );

        return response;
    }
}