package com.tourism.backend.itinerary.controller;

import com.tourism.backend.constants.ApiPaths;
import com.tourism.backend.itinerary.dto.ItineraryItemRequest;
import com.tourism.backend.itinerary.dto.ItineraryRequest;
import com.tourism.backend.itinerary.dto.ItineraryResponse;
import com.tourism.backend.itinerary.service.ItineraryService;
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
@RequestMapping(ApiPaths.ITINERARIES)
@RequiredArgsConstructor
@Tag(
        name = "Itineraries",
        description = "Operations for managing travel itineraries"
)
public class ItineraryController {

    private final ItineraryService itineraryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create itinerary",
            description = "Creates a new travel itinerary."
    )
    @ApiResponse(responseCode = "201", description = "Itinerary created successfully")
    public ItineraryResponse createItinerary(
            @Valid @RequestBody ItineraryRequest request) {

        return itineraryService.createItinerary(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update itinerary")
    @ApiResponse(responseCode = "200", description = "Itinerary updated successfully")
    @ApiResponse(responseCode = "404", description = "Itinerary not found")
    public ItineraryResponse updateItinerary(

            @Parameter(description = "Itinerary ID", example = "1")
            @PathVariable Long id,

            @Valid
            @RequestBody
            ItineraryRequest request) {

        return itineraryService.updateItinerary(id, request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get itinerary by ID")
    @ApiResponse(responseCode = "200", description = "Itinerary found")
    @ApiResponse(responseCode = "404", description = "Itinerary not found")
    public ItineraryResponse getItineraryById(

            @Parameter(description = "Itinerary ID", example = "1")
            @PathVariable Long id) {

        return itineraryService.getItineraryById(id);
    }

    @GetMapping
    @Operation(
            summary = "Retrieve itineraries",
            description = """
                    Returns all itineraries.

                    Optionally filter by destination:

                    /api/itineraries?destinationId=1
                    """
    )
    public List<ItineraryResponse> getItineraries(

            @Parameter(
                    description = "Filter by destination ID",
                    example = "1"
            )
            @RequestParam(required = false)
            Long destinationId) {

        if (destinationId != null) {
            return itineraryService.getItinerariesByDestination(destinationId);
        }

        return itineraryService.getAllItineraries();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete itinerary")
    @ApiResponse(responseCode = "204", description = "Itinerary deleted successfully")
    @ApiResponse(responseCode = "404", description = "Itinerary not found")
    public void deleteItinerary(

            @Parameter(description = "Itinerary ID", example = "1")
            @PathVariable Long id) {

        itineraryService.deleteItinerary(id);
    }

    @PostMapping("/{id}/items")
    @Operation(summary = "Add itinerary item")
    @ApiResponse(responseCode = "200", description = "Item added successfully")
    @ApiResponse(responseCode = "404", description = "Itinerary or referenced entity not found")
    public ItineraryResponse addItem(

            @Parameter(description = "Itinerary ID", example = "1")
            @PathVariable Long id,

            @Valid
            @RequestBody
            ItineraryItemRequest request) {

        return itineraryService.addItem(id, request);
    }

    @PutMapping("/{id}/items/{itemId}")
    @Operation(summary = "Update itinerary item")
    @ApiResponse(responseCode = "200", description = "Item updated successfully")
    @ApiResponse(responseCode = "404", description = "Itinerary, item or referenced entity not found")
    public ItineraryResponse updateItem(

            @Parameter(description = "Itinerary ID", example = "1")
            @PathVariable Long id,

            @Parameter(description = "Item ID", example = "1")
            @PathVariable Long itemId,

            @Valid
            @RequestBody
            ItineraryItemRequest request) {

        return itineraryService.updateItem(
                id,
                itemId,
                request);
    }

    @DeleteMapping("/{id}/items/{itemId}")
    @Operation(summary = "Delete itinerary item")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(responseCode = "200", description = "Item deleted successfully")
    @ApiResponse(responseCode = "404", description = "Itinerary or item not found")
    public ItineraryResponse deleteItem(

            @Parameter(description = "Itinerary ID", example = "1")
            @PathVariable Long id,

            @Parameter(description = "Item ID", example = "1")
            @PathVariable Long itemId) {

        return itineraryService.deleteItem(
                id,
                itemId);
    }
}