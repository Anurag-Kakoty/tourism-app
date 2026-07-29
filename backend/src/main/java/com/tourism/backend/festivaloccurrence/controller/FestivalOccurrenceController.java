package com.tourism.backend.festivaloccurrence.controller;

import com.tourism.backend.festivaloccurrence.dto.FestivalOccurrenceRequest;
import com.tourism.backend.festivaloccurrence.dto.FestivalOccurrenceResponse;
import com.tourism.backend.festivaloccurrence.service.FestivalOccurrenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/festival-occurrences")
@RequiredArgsConstructor
@Tag(name = "Festival Occurrence", description = "Festival occurrence management APIs")
public class FestivalOccurrenceController {

    private final FestivalOccurrenceService service;

    @PostMapping
    @Operation(summary = "Create a festival occurrence")
    public FestivalOccurrenceResponse create(
            @Valid @RequestBody FestivalOccurrenceRequest request) {

        return service.create(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a festival occurrence")
    public FestivalOccurrenceResponse update(
            @PathVariable Long id,
            @Valid @RequestBody FestivalOccurrenceRequest request) {

        return service.update(id, request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get festival occurrence by ID")
    public FestivalOccurrenceResponse getById(
            @PathVariable Long id) {

        return service.getById(id);
    }

    @GetMapping
    @Operation(summary = "Get festival occurrences")
    public List<FestivalOccurrenceResponse> getAll(
            @RequestParam(required = false) Long stateId,
            @RequestParam(required = false) Integer year) {

        if (stateId != null && year != null) {
            return service.getByStateAndYear(stateId, year);
        }

        if (stateId != null) {
            return service.getByState(stateId);
        }

        if (year != null) {
            return service.getByYear(year);
        }

        return service.getAll();
    }
    @GetMapping("/upcoming")
    @Operation(summary = "Get upcoming festival occurrences")
    public List<FestivalOccurrenceResponse> getUpcoming() {

        return service.getUpcoming();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a festival occurrence")
    public void delete(
            @PathVariable Long id) {

        service.delete(id);
    }
}