package com.tourism.backend.destination.controller;

import com.tourism.backend.constants.ApiPaths;
import com.tourism.backend.destination.dto.DestinationRequest;
import com.tourism.backend.destination.dto.DestinationResponse;
import com.tourism.backend.destination.entity.DestinationType;
import com.tourism.backend.destination.service.DestinationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.DESTINATIONS)
@RequiredArgsConstructor
@Tag(
        name = "Destination",
        description = "Destination management APIs"
)
public class DestinationController {

    private final DestinationService service;

    @PostMapping
    @Operation(summary = "Create destination")
    public DestinationResponse create(
            @Valid @RequestBody DestinationRequest request) {

        return service.create(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update destination")
    public DestinationResponse update(
            @PathVariable Long id,
            @Valid @RequestBody DestinationRequest request) {

        return service.update(id, request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get destination by ID")
    public DestinationResponse getById(
            @PathVariable Long id) {

        return service.getById(id);
    }

    @GetMapping
    @Operation(summary = "Get destinations")
    public List<DestinationResponse> getAll(

            @RequestParam(required = false)
            Long stateId,

            @RequestParam(required = false)
            DestinationType type,

            @RequestParam(required = false)
            Boolean featured,

            @RequestParam(required = false)
            Boolean popular) {

        if (featured != null && featured) {
            return service.getFeatured();
        }

        if (popular != null && popular) {
            return service.getPopular();
        }

        if (stateId != null) {
            return service.getByState(stateId);
        }

        if (type != null) {
            return service.getByType(type);
        }

        return service.getAll();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete destination")
    public void delete(
            @PathVariable Long id) {

        service.delete(id);
    }
}