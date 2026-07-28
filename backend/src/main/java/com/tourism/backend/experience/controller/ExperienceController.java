package com.tourism.backend.experience.controller;

import com.tourism.backend.constants.ApiPaths;
import com.tourism.backend.experience.dto.ExperienceRequest;
import com.tourism.backend.experience.dto.ExperienceResponse;
import com.tourism.backend.experience.service.ExperienceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.EXPERIENCES)
@Tag(name = "Experience", description = "Experience management APIs")
public class ExperienceController {

    private final ExperienceService experienceService;

    public ExperienceController(ExperienceService experienceService) {
        this.experienceService = experienceService;
    }

    @Operation(summary = "Create a new experience")
    @PostMapping
    public ResponseEntity<ExperienceResponse> createExperience(
            @Valid @RequestBody ExperienceRequest request) {

        ExperienceResponse response =
                experienceService.createExperience(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(summary = "Get all experiences")
    @GetMapping
    public ResponseEntity<List<ExperienceResponse>> getAllExperiences() {

        return ResponseEntity.ok(
                experienceService.getAllExperiences()
        );
    }

    @Operation(summary = "Get experience by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ExperienceResponse> getExperienceById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                experienceService.getExperienceById(id)
        );
    }

    @Operation(summary = "Update an experience")
    @PutMapping("/{id}")
    public ResponseEntity<ExperienceResponse> updateExperience(
            @PathVariable Long id,
            @Valid @RequestBody ExperienceRequest request) {

        return ResponseEntity.ok(
                experienceService.updateExperience(id, request)
        );
    }

    @Operation(summary = "Delete an experience")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExperience(
            @PathVariable Long id) {

        experienceService.deleteExperience(id);

        return ResponseEntity.noContent().build();
    }
}