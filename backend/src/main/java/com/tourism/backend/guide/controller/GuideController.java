package com.tourism.backend.guide.controller;

import com.tourism.backend.constants.ApiPaths;
import com.tourism.backend.guide.dto.GuideRequest;
import com.tourism.backend.guide.dto.GuideResponse;
import com.tourism.backend.guide.entity.Language;
import com.tourism.backend.guide.service.GuideService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.GUIDES)
@RequiredArgsConstructor
@Tag(
        name = "Guide",
        description = "Guide Management APIs"
)
public class GuideController {

    private final GuideService guideService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new guide")
    public GuideResponse createGuide(
            @Valid @RequestBody GuideRequest request) {

        return guideService.createGuide(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing guide")
    public GuideResponse updateGuide(
            @PathVariable Long id,
            @Valid @RequestBody GuideRequest request) {

        return guideService.updateGuide(id, request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get guide by ID")
    public GuideResponse getGuideById(
            @PathVariable Long id) {

        return guideService.getGuideById(id);
    }

    @GetMapping
    @Operation(
            summary = "Get guides with optional filters",
            description = """
                    Returns guides using optional combinable filters.

                    Available filters:
                    - destinationId
                    - available
                    - providesTransport
                    - language

                    Examples:

                    /api/guides?destinationId=1

                    /api/guides?available=true

                    /api/guides?providesTransport=true

                    /api/guides?language=KHASI

                    Multiple filters can be combined:

                    /api/guides?destinationId=1&available=true&providesTransport=true&language=KHASI
                    """
    )
    public List<GuideResponse> getGuides(

            @RequestParam(required = false)
            Long destinationId,

            @RequestParam(required = false)
            Boolean available,

            @RequestParam(required = false)
            Boolean providesTransport,

            @RequestParam(required = false)
            Language language) {

        return guideService.getGuides(
                destinationId,
                available,
                providesTransport,
                language
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete guide")
    public void deleteGuide(
            @PathVariable Long id) {

        guideService.deleteGuide(id);
    }
}