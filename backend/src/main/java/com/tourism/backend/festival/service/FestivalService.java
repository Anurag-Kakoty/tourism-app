package com.tourism.backend.festival.service;

import com.tourism.backend.festival.dto.FestivalRequest;
import com.tourism.backend.festival.dto.FestivalResponse;

import java.util.List;

public interface FestivalService {

    FestivalResponse create(FestivalRequest request);

    FestivalResponse update(Long id, FestivalRequest request);

    FestivalResponse getById(Long id);

    List<FestivalResponse> getAll();

    void delete(Long id);

}