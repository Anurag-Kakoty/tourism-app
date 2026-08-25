package com.tourism.backend.accommodation.controller;

import com.tourism.backend.accommodation.dto.AccommodationRequest;
import com.tourism.backend.accommodation.dto.AccommodationResponse;
import com.tourism.backend.accommodation.entity.AccommodationType;
import com.tourism.backend.accommodation.service.AccommodationService;
import com.tourism.backend.constants.ApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.ACCOMMODATIONS)
@RequiredArgsConstructor
@Tag(
        name = "Accommodations",
        description = "Operations for managing accommodations"
)
public class AccommodationController {

    private final AccommodationService service;

    @Operation(summary = "Create a new accommodation")
    @ApiResponse(
            responseCode = "201",
            description = "Accommodation created successfully"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Validation failed"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Destination not found"
    )
    @ApiResponse(
            responseCode = "409",
            description = "Accommodation already exists"
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccommodationResponse create(
            @Valid @RequestBody AccommodationRequest request) {

        return service.create(request);
    }

    @Operation(summary = "Update an existing accommodation")
    @ApiResponse(
            responseCode = "200",
            description = "Accommodation updated successfully"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Accommodation or Destination not found"
    )
    @ApiResponse(
            responseCode = "409",
            description = "Duplicate accommodation"
    )
    @PutMapping("/{id}")
    public AccommodationResponse update(

            @Parameter(
                    description = "Accommodation ID",
                    example = "1"
            )
            @PathVariable
            Long id,

            @Valid
            @RequestBody
            AccommodationRequest request) {

        return service.update(
                id,
                request
        );
    }

    @Operation(summary = "Retrieve an accommodation by its ID")
    @ApiResponse(
            responseCode = "200",
            description = "Accommodation found"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Accommodation not found"
    )
    @GetMapping("/{id}")
    public AccommodationResponse getById(

            @Parameter(
                    description = "Accommodation ID",
                    example = "1"
            )
            @PathVariable
            Long id) {

        return service.getById(id);
    }

    @Operation(
            summary = "Retrieve accommodations",
            description = """
                    Returns accommodations using optional combined filters.

                    Available filters:
                    - destinationId
                    - type
                    - available

                    Filters can be combined.

                    Examples:

                    /api/accommodations?destinationId=1

                    /api/accommodations?type=HOTEL

                    /api/accommodations?available=true

                    /api/accommodations?destinationId=1&type=HOTEL

                    /api/accommodations?destinationId=1&type=HOTEL&available=true
                    """
    )
    @GetMapping
    public List<AccommodationResponse> getAll(

            @Parameter(
                    description = "Destination ID",
                    example = "1"
            )
            @RequestParam(required = false)
            Long destinationId,

            @Parameter(
                    description = "Accommodation type",
                    example = "HOTEL"
            )
            @RequestParam(required = false)
            AccommodationType type,

            @Parameter(
                    description = "Availability",
                    example = "true"
            )
            @RequestParam(required = false)
            Boolean available) {

        return service.getAll(
                destinationId,
                type,
                available
        );
    }

    @Operation(summary = "Delete an accommodation")
    @ApiResponse(
            responseCode = "204",
            description = "Accommodation deleted successfully"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Accommodation not found"
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(

            @Parameter(
                    description = "Accommodation ID",
                    example = "1"
            )
            @PathVariable
            Long id) {

        service.delete(id);
    }
}