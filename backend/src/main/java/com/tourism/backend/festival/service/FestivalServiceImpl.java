package com.tourism.backend.festival.service;

import com.tourism.backend.exception.DuplicateResourceException;
import com.tourism.backend.exception.ResourceNotFoundException;
import com.tourism.backend.festival.dto.FestivalRequest;
import com.tourism.backend.festival.dto.FestivalResponse;
import com.tourism.backend.festival.entity.Festival;
import com.tourism.backend.festival.mapper.FestivalMapper;
import com.tourism.backend.festival.repository.FestivalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FestivalServiceImpl implements FestivalService {

    private final FestivalRepository festivalRepository;
    private final FestivalMapper festivalMapper;

    @Override
    public FestivalResponse create(FestivalRequest request) {

        log.info("Creating festival: {}", request.getName());

        if (festivalRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException(
                    "Festival already exists with name: " + request.getName()
            );
        }

        Festival festival = festivalMapper.toEntity(request);

        Festival savedFestival = festivalRepository.save(festival);

        log.info("Festival created successfully with id {}", savedFestival.getId());

        return festivalMapper.toResponse(savedFestival);
    }

    @Override
    public FestivalResponse update(Long id, FestivalRequest request) {

        log.info("Updating festival with id {}", id);

        Festival festival = festivalRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Festival not found with id: " + id
                        ));

        festivalRepository.findByNameIgnoreCase(request.getName())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new DuplicateResourceException(
                                "Festival already exists with name: " + request.getName()
                        );
                    }
                });

        festivalMapper.updateEntity(festival, request);

        Festival updatedFestival = festivalRepository.save(festival);

        log.info("Festival updated successfully with id {}", id);

        return festivalMapper.toResponse(updatedFestival);
    }

    @Override
    public FestivalResponse getById(Long id) {

        log.info("Fetching festival with id {}", id);

        Festival festival = festivalRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Festival not found with id: " + id
                        ));

        return festivalMapper.toResponse(festival);
    }

    @Override
    public List<FestivalResponse> getAll() {

        log.info("Fetching all festivals");

        return festivalRepository.findAll()
                .stream()
                .map(festivalMapper::toResponse)
                .toList();
    }

    @Override
    public void delete(Long id) {

        log.info("Deleting festival with id {}", id);

        Festival festival = festivalRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Festival not found with id: " + id
                        ));

        festivalRepository.delete(festival);

        log.info("Festival deleted successfully with id {}", id);
    }
}