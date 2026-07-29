package com.tourism.backend.festival.mapper;

import com.tourism.backend.festival.dto.FestivalRequest;
import com.tourism.backend.festival.dto.FestivalResponse;
import com.tourism.backend.festival.entity.Festival;
import org.springframework.stereotype.Component;

@Component
public class FestivalMapper {

    public Festival toEntity(FestivalRequest request) {

        return Festival.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .imageUrl(request.getImageUrl())
                .officialWebsite(request.getOfficialWebsite())
                .build();
    }

    public void updateEntity(
            Festival festival,
            FestivalRequest request
    ) {

        festival.setName(request.getName());
        festival.setDescription(request.getDescription());
        festival.setCategory(request.getCategory());
        festival.setImageUrl(request.getImageUrl());
        festival.setOfficialWebsite(request.getOfficialWebsite());

    }

    public FestivalResponse toResponse(Festival festival) {

        return FestivalResponse.builder()
                .id(festival.getId())
                .name(festival.getName())
                .description(festival.getDescription())
                .category(festival.getCategory())
                .imageUrl(festival.getImageUrl())
                .officialWebsite(festival.getOfficialWebsite())
                .build();

    }

}