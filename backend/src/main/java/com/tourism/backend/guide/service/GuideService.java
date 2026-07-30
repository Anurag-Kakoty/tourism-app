package com.tourism.backend.guide.service;

import com.tourism.backend.guide.dto.GuideRequest;
import com.tourism.backend.guide.dto.GuideResponse;
import com.tourism.backend.guide.entity.Language;

import java.util.List;

public interface GuideService {

    GuideResponse createGuide(GuideRequest request);

    GuideResponse updateGuide(Long id, GuideRequest request);

    GuideResponse getGuideById(Long id);

    List<GuideResponse> getAllGuides();

    List<GuideResponse> getGuidesByDestination(Long destinationId);

    List<GuideResponse> getAvailableGuides();

    List<GuideResponse> getGuidesProvidingTransport();

    List<GuideResponse> getGuidesByLanguage(Language language);

    void deleteGuide(Long id);
}