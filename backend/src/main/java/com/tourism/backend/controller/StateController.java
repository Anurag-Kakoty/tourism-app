package com.tourism.backend.controller;

import com.tourism.backend.constants.ApiPaths;
import com.tourism.backend.dto.state.StateRequest;
import com.tourism.backend.dto.state.StateResponse;
import com.tourism.backend.service.StateService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;


@Tag(
        name = "States",
        description = "Operations for managing Indian states"
)
@RestController
@RequestMapping(ApiPaths.STATES)
public class StateController {

    private final StateService stateService;

    public StateController(StateService stateService) {
        this.stateService = stateService;
    }

    @Operation(
            summary = "Create a new state",
            description = "Creates a new Indian state."
    )
    @ApiResponse(responseCode = "201", description = "State created successfully")
    @ApiResponse(responseCode = "400", description = "Validation failed")
    @ApiResponse(responseCode = "409", description = "State already exists")

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StateResponse createState(@Valid @RequestBody StateRequest request) {
        return stateService.createState(request);
    }

    @Operation(
            summary = "Retrieve all states"
    )
    @GetMapping
    public List<StateResponse> getAllStates() {
        return stateService.getAllStates();
    }

    @Operation(
            summary = "Retrieve a state by its ID"
    )
    @ApiResponse(responseCode = "200", description = "State found")
    @ApiResponse(responseCode = "404", description = "State not found")
    @GetMapping("/{id}")
    public StateResponse getStateById(@PathVariable Long id) {
        return stateService.getStateById(id);
    }

    @Operation(
            summary = "Update an existing state"
    )
    @ApiResponse(responseCode = "200", description = "State updated successfully")
    @ApiResponse(responseCode = "404", description = "State not found")
    @ApiResponse(responseCode = "409", description = "Duplicate state")
    @PutMapping("/{id}")
    public StateResponse updateState(@PathVariable Long id,
                                     @Valid @RequestBody StateRequest request) {
        return stateService.updateState(id, request);
    }

    @Operation(
            summary = "Delete a state"
    )
    @ApiResponse(responseCode = "204", description = "State deleted successfully")
    @ApiResponse(responseCode = "404", description = "State not found")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteState(@PathVariable Long id) {
        stateService.deleteState(id);
    }
}