package com.tourism.backend.controller;

import com.tourism.backend.constants.ApiPaths;
import com.tourism.backend.dto.state.StateRequest;
import com.tourism.backend.dto.state.StateResponse;
import com.tourism.backend.service.StateService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.STATES)
public class StateController {

    private final StateService stateService;

    public StateController(StateService stateService) {
        this.stateService = stateService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StateResponse createState(@Valid @RequestBody StateRequest request) {
        return stateService.createState(request);
    }

    @GetMapping
    public List<StateResponse> getAllStates() {
        return stateService.getAllStates();
    }

    @GetMapping("/{id}")
    public StateResponse getStateById(@PathVariable Long id) {
        return stateService.getStateById(id);
    }

    @PutMapping("/{id}")
    public StateResponse updateState(@PathVariable Long id,
                                     @Valid @RequestBody StateRequest request) {
        return stateService.updateState(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteState(@PathVariable Long id) {
        stateService.deleteState(id);
    }
}