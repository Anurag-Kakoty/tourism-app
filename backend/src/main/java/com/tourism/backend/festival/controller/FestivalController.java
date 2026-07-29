package com.tourism.backend.festival.controller;

import com.tourism.backend.festival.dto.FestivalRequest;
import com.tourism.backend.festival.dto.FestivalResponse;
import com.tourism.backend.festival.service.FestivalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/festivals")
@Tag(name = "Festival", description = "Festival management APIs")
@RequiredArgsConstructor
public class FestivalController {

    private final FestivalService festivalService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create Festival")
    public FestivalResponse create(
            @Valid @RequestBody FestivalRequest request) {

        return festivalService.create(request);
    }

    @GetMapping
    @Operation(summary = "Get All Festivals")
    public List<FestivalResponse> getAll() {

        return festivalService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Festival By Id")
    public FestivalResponse getById(@PathVariable Long id) {

        return festivalService.getById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Festival")
    public FestivalResponse update(
            @PathVariable Long id,
            @Valid @RequestBody FestivalRequest request) {

        return festivalService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete Festival")
    public void delete(@PathVariable Long id) {

        festivalService.delete(id);
    }
}