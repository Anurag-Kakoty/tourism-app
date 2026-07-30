package com.tourism.backend.attraction.controller;

import com.tourism.backend.attraction.dto.AttractionRequest;
import com.tourism.backend.attraction.dto.AttractionResponse;
import com.tourism.backend.attraction.service.AttractionService;
import com.tourism.backend.constants.ApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Attractions",
        description = "Operations for managing tourist attractions"
)
@RestController
@RequestMapping(ApiPaths.ATTRACTIONS)
public class AttractionController {

    private final AttractionService attractionService;

    public AttractionController(AttractionService attractionService) {
        this.attractionService = attractionService;
    }

    @Operation(
            summary = "Create a new attraction",
            description = "Creates a new tourist attraction."
    )
    @ApiResponse(responseCode = "201", description = "Attraction created successfully")
    @ApiResponse(responseCode = "400", description = "Validation failed")
    @ApiResponse(responseCode = "404", description = "Destination not found")
    @ApiResponse(responseCode = "409", description = "Attraction already exists")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AttractionResponse createAttraction(
            @Valid @RequestBody AttractionRequest request) {

        return attractionService.createAttraction(request);
    }

    @Operation(
            summary = "Retrieve all attractions or filter by destination",
            description = """
                    Returns all attractions.

                    Optionally filter by destination:

                    /api/attractions?destinationId=1
                    """
    )
    @GetMapping
    public List<AttractionResponse> getAllAttractions(

            @Parameter(
                    description = "Filter attractions by destination ID",
                    example = "1"
            )
            @RequestParam(required = false)
            Long destinationId) {

        return attractionService.getAllAttractions(destinationId);
    }

    @Operation(
            summary = "Retrieve featured attractions"
    )
    @ApiResponse(responseCode = "200", description = "Featured attractions retrieved successfully")
    @GetMapping("/featured")
    public List<AttractionResponse> getFeaturedAttractions() {

        return attractionService.getFeaturedAttractions();
    }

    @Operation(
            summary = "Retrieve an attraction by its ID"
    )
    @ApiResponse(responseCode = "200", description = "Attraction found")
    @ApiResponse(responseCode = "404", description = "Attraction not found")
    @GetMapping("/{id}")
    public AttractionResponse getAttractionById(

            @Parameter(
                    description = "Attraction ID",
                    example = "1"
            )
            @PathVariable
            Long id) {

        return attractionService.getAttractionById(id);
    }

    @Operation(
            summary = "Update an existing attraction"
    )
    @ApiResponse(responseCode = "200", description = "Attraction updated successfully")
    @ApiResponse(responseCode = "404", description = "Attraction or Destination not found")
    @ApiResponse(responseCode = "409", description = "Duplicate attraction")
    @PutMapping("/{id}")
    public AttractionResponse updateAttraction(

            @Parameter(
                    description = "Attraction ID",
                    example = "1"
            )
            @PathVariable
            Long id,

            @Valid
            @RequestBody
            AttractionRequest request) {

        return attractionService.updateAttraction(id, request);
    }

    @Operation(
            summary = "Delete an attraction"
    )
    @ApiResponse(responseCode = "204", description = "Attraction deleted successfully")
    @ApiResponse(responseCode = "404", description = "Attraction not found")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAttraction(

            @Parameter(
                    description = "Attraction ID",
                    example = "1"
            )
            @PathVariable
            Long id) {

        attractionService.deleteAttraction(id);
    }
}