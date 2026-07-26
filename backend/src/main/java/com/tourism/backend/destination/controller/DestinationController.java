package com.tourism.backend.destination.controller;

import com.tourism.backend.constants.ApiPaths;
import com.tourism.backend.destination.dto.DestinationRequest;
import com.tourism.backend.destination.dto.DestinationResponse;
import com.tourism.backend.destination.service.DestinationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Destinations",
        description = "Operations for managing tourist destinations"
)
@RestController
@RequestMapping(ApiPaths.DESTINATIONS)
public class DestinationController {

    private final DestinationService destinationService;

    public DestinationController(DestinationService destinationService) {
        this.destinationService = destinationService;
    }

    @Operation(
            summary = "Create a new destination",
            description = "Creates a new tourist destination."
    )
    @ApiResponse(responseCode = "201", description = "Destination created successfully")
    @ApiResponse(responseCode = "400", description = "Validation failed")
    @ApiResponse(responseCode = "404", description = "State not found")
    @ApiResponse(responseCode = "409", description = "Destination already exists")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DestinationResponse createDestination(
            @Valid @RequestBody DestinationRequest request) {

        return destinationService.createDestination(request);
    }

    @Operation(
            summary = "Retrieve all destinations or filter by state",
            description = """
                    Returns all destinations.

                    Optionally filter by state name:

                    /api/destinations?state=Assam
                    """
    )
    @GetMapping
    public List<DestinationResponse> getAllDestinations(

            @Parameter(
                    description = "Filter destinations by state name",
                    example = "Assam"
            )
            @RequestParam(required = false)
            String state) {

        return destinationService.getAllDestinations(state);
    }

    @Operation(
            summary = "Retrieve a destination by its ID"
    )
    @ApiResponse(responseCode = "200", description = "Destination found")
    @ApiResponse(responseCode = "404", description = "Destination not found")
    @GetMapping("/{id}")
    public DestinationResponse getDestinationById(

            @Parameter(
                    description = "Destination ID",
                    example = "1"
            )
            @PathVariable
            Long id) {

        return destinationService.getDestinationById(id);
    }

    @Operation(
            summary = "Update an existing destination"
    )
    @ApiResponse(responseCode = "200", description = "Destination updated successfully")
    @ApiResponse(responseCode = "404", description = "Destination or State not found")
    @ApiResponse(responseCode = "409", description = "Duplicate destination")
    @PutMapping("/{id}")
    public DestinationResponse updateDestination(

            @Parameter(
                    description = "Destination ID",
                    example = "1"
            )
            @PathVariable
            Long id,

            @Valid
            @RequestBody
            DestinationRequest request) {

        return destinationService.updateDestination(id, request);
    }

    @Operation(
            summary = "Delete a destination"
    )
    @ApiResponse(responseCode = "204", description = "Destination deleted successfully")
    @ApiResponse(responseCode = "404", description = "Destination not found")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDestination(

            @Parameter(
                    description = "Destination ID",
                    example = "1"
            )
            @PathVariable
            Long id) {

        destinationService.deleteDestination(id);
    }
}