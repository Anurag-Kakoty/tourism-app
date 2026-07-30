package com.tourism.backend.accommodation.controller;

import com.tourism.backend.accommodation.dto.AccommodationRequest;
import com.tourism.backend.accommodation.dto.AccommodationResponse;
import com.tourism.backend.accommodation.entity.AccommodationType;
import com.tourism.backend.accommodation.service.AccommodationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accommodations")
@RequiredArgsConstructor
@Tag(
        name = "Accommodation",
        description = "Accommodation management APIs"
)
public class AccommodationController {

    private final AccommodationService service;

    @PostMapping
    @Operation(summary = "Create accommodation")
    public AccommodationResponse create(
            @Valid @RequestBody AccommodationRequest request) {

        return service.create(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update accommodation")
    public AccommodationResponse update(
            @PathVariable Long id,
            @Valid @RequestBody AccommodationRequest request) {

        return service.update(id, request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get accommodation by ID")
    public AccommodationResponse getById(
            @PathVariable Long id) {

        return service.getById(id);
    }

    @GetMapping
    @Operation(summary = "Get accommodations")
    public List<AccommodationResponse> getAll(

            @RequestParam(required = false)
            Long destinationId,

            @RequestParam(required = false)
            AccommodationType type,

            @RequestParam(required = false)
            Boolean available) {

        if (destinationId != null && type != null) {
            return service.getByDestinationAndType(
                    destinationId,
                    type);
        }

        if (destinationId != null) {
            return service.getByDestination(destinationId);
        }

        if (type != null) {
            return service.getByType(type);
        }

        if (available != null) {
            return service.getByAvailable(available);
        }

        return service.getAll();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete accommodation")
    public void delete(
            @PathVariable Long id) {

        service.delete(id);
    }
}