package com.tourism.backend.attraction.service;

import com.tourism.backend.attraction.dto.AttractionRequest;
import com.tourism.backend.attraction.dto.AttractionResponse;

import java.util.List;

public interface AttractionService {

    AttractionResponse createAttraction(AttractionRequest request);

    List<AttractionResponse> getAllAttractions(Long destinationId);

    List<AttractionResponse> getFeaturedAttractions();

    AttractionResponse getAttractionById(Long id);

    AttractionResponse updateAttraction(
            Long id,
            AttractionRequest request);

    void deleteAttraction(Long id);
}