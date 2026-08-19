package com.tourism.backend.destination.controller;

import com.tourism.backend.constants.ApiPaths;
import com.tourism.backend.destination.dto.DestinationRequest;
import com.tourism.backend.destination.dto.DestinationResponse;
import com.tourism.backend.destination.entity.DestinationType;
import com.tourism.backend.destination.service.DestinationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    @Operation(
            summary = "Get destinations",
            description = """
                    Returns destinations with optional filters.

                    Filters can be combined.

                    Examples:

                    /api/destinations?stateId=1

                    /api/destinations?type=HILL_STATION

                    /api/destinations?featured=true

                    /api/destinations?popular=true

                    /api/destinations?stateId=1&type=HILL_STATION

                    /api/destinations?stateId=1&featured=true

                    /api/destinations?stateId=1&type=HILL_STATION&featured=true
                    """
    )
    public List<DestinationResponse> getAll(

            @Parameter(
                    description = "Filter by state ID",
                    example = "1"
            )
            @RequestParam(required = false)
            Long stateId,

            @Parameter(
                    description = "Filter by destination type",
                    example = "HILL_STATION"
            )
            @RequestParam(required = false)
            DestinationType type,

            @Parameter(
                    description = "Filter by featured status",
                    example = "true"
            )
            @RequestParam(required = false)
            Boolean featured,

            @Parameter(
                    description = "Filter by popular status",
                    example = "true"
            )
            @RequestParam(required = false)
            Boolean popular) {

        return service.getAll(
                stateId,
                type,
                featured,
                popular
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete destination")
    public void delete(
            @PathVariable Long id) {

        service.delete(id);
    }
}