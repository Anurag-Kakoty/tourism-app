package com.tourism.backend.state.service;

import com.tourism.backend.state.dto.StateRequest;
import com.tourism.backend.state.dto.StateResponse;

import java.util.List;

public interface StateService {

    StateResponse createState(StateRequest request);

    List<StateResponse> getAllStates(String name);

    StateResponse getStateById(Long id);

    StateResponse updateState(Long id, StateRequest request);

    void deleteState(Long id);

}