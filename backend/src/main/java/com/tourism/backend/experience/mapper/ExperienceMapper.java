package com.tourism.backend.experience.mapper;

import com.tourism.backend.experience.dto.ExperienceRequest;
import com.tourism.backend.experience.dto.ExperienceResponse;
import com.tourism.backend.experience.entity.Experience;
import org.springframework.stereotype.Component;

@Component
public class ExperienceMapper {

    public Experience toEntity(ExperienceRequest request) {

        Experience experience = new Experience();

        experience.setName(request.getName());
        experience.setDescription(request.getDescription());
        experience.setIcon(request.getIcon());

        return experience;
    }

    public void updateEntity(Experience experience,
                             ExperienceRequest request) {

        experience.setName(request.getName());
        experience.setDescription(request.getDescription());
        experience.setIcon(request.getIcon());
    }

    public ExperienceResponse toResponse(Experience experience) {

        ExperienceResponse response = new ExperienceResponse();

        response.setId(experience.getId());
        response.setName(experience.getName());
        response.setDescription(experience.getDescription());
        response.setIcon(experience.getIcon());

        return response;
    }

}