package com.tourism.backend.tag.controller;

import com.tourism.backend.constants.ApiPaths;
import com.tourism.backend.tag.dto.TagRequest;
import com.tourism.backend.tag.dto.TagResponse;
import com.tourism.backend.tag.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.TAGS)
@Tag(
        name = "Tags",
        description = "Operations for managing destination tags"
)
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @Operation(summary = "Create a new tag")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Tag created successfully",
                    content = @Content(schema = @Schema(implementation = TagResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Tag already exists")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TagResponse createTag(
            @Valid @RequestBody TagRequest request) {

        return tagService.createTag(request);
    }

    @Operation(summary = "Get all tags")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of tags retrieved successfully"
            )
    })
    @GetMapping
    public List<TagResponse> getAllTags() {

        return tagService.getAllTags();
    }

    @Operation(summary = "Get tag by ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Tag found",
                    content = @Content(schema = @Schema(implementation = TagResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Tag not found")
    })
    @GetMapping("/{id}")
    public TagResponse getTagById(
            @PathVariable Long id) {

        return tagService.getTagById(id);
    }

    @Operation(summary = "Update an existing tag")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Tag updated successfully",
                    content = @Content(schema = @Schema(implementation = TagResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Tag not found"),
            @ApiResponse(responseCode = "409", description = "Tag already exists")
    })
    @PutMapping("/{id}")
    public TagResponse updateTag(
            @PathVariable Long id,
            @Valid @RequestBody TagRequest request) {

        return tagService.updateTag(id, request);
    }

    @Operation(summary = "Delete a tag")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tag deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Tag not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTag(
            @PathVariable Long id) {

        tagService.deleteTag(id);
    }
}