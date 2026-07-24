package com.tourism.backend.service;

import com.tourism.backend.dto.state.StateRequest;
import com.tourism.backend.dto.state.StateResponse;

import java.util.List;

public interface StateService {

    StateResponse createState(StateRequest request);

    List<StateResponse> getAllStates(String name);

    StateResponse getStateById(Long id);

    StateResponse updateState(Long id, StateRequest request);

    void deleteState(Long id);

}