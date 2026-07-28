package com.tourism.backend.experience.service;

import com.tourism.backend.experience.dto.ExperienceRequest;
import com.tourism.backend.experience.dto.ExperienceResponse;

import java.util.List;

public interface ExperienceService {

    ExperienceResponse createExperience(ExperienceRequest request);

    List<ExperienceResponse> getAllExperiences();

    ExperienceResponse getExperienceById(Long id);

    ExperienceResponse updateExperience(Long id,
                                        ExperienceRequest request);

    void deleteExperience(Long id);
}