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
            summary = "Retrieve attractions with optional filters",
            description = """
                Returns attractions with optional dynamic filters.

                Available filters:

                /api/attractions?stateId=1

                /api/attractions?destinationId=1

                /api/attractions?experienceId=1

                /api/attractions?tagId=1

                /api/attractions?featured=true

                Multiple filters can be combined:

                /api/attractions?destinationId=1&experienceId=1

                /api/attractions?stateId=1&tagId=1&featured=true
                """
    )
    @GetMapping
    public List<AttractionResponse> getAllAttractions(

            @Parameter(
                    description = "Filter attractions by state ID",
                    example = "1"
            )
            @RequestParam(required = false)
            Long stateId,

            @Parameter(
                    description = "Filter attractions by destination ID",
                    example = "1"
            )
            @RequestParam(required = false)
            Long destinationId,

            @Parameter(
                    description = "Filter attractions by experience ID",
                    example = "1"
            )
            @RequestParam(required = false)
            Long experienceId,

            @Parameter(
                    description = "Filter attractions by tag ID",
                    example = "1"
            )
            @RequestParam(required = false)
            Long tagId,

            @Parameter(
                    description = "Filter featured attractions",
                    example = "true"
            )
            @RequestParam(required = false)
            Boolean featured) {

        return attractionService.getAllAttractions(
                stateId,
                destinationId,
                experienceId,
                tagId,
                featured
        );
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