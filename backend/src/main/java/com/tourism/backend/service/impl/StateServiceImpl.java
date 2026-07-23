package com.tourism.backend.service.impl;

import com.tourism.backend.dto.state.StateRequest;
import com.tourism.backend.dto.state.StateResponse;
import com.tourism.backend.entity.State;
import com.tourism.backend.mapper.StateMapper;
import com.tourism.backend.repository.StateRepository;
import com.tourism.backend.service.StateService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StateServiceImpl implements StateService {

    private final StateRepository stateRepository;
    private final StateMapper stateMapper;

    public StateServiceImpl(StateRepository stateRepository,
                            StateMapper stateMapper) {
        this.stateRepository = stateRepository;
        this.stateMapper = stateMapper;
    }

    @Override
    public StateResponse createState(StateRequest request) {

        if (stateRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("State already exists.");
        }

        State state = stateMapper.toEntity(request);

        State savedState = stateRepository.save(state);

        return stateMapper.toResponse(savedState);
    }

    @Override
    public List<StateResponse> getAllStates() {

        return stateRepository.findAll()
                .stream()
                .map(stateMapper::toResponse)
                .toList();
    }

    @Override
    public StateResponse getStateById(Long id) {

        State state = stateRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("State not found."));

        return stateMapper.toResponse(state);
    }

    @Override
    public StateResponse updateState(Long id,
                                     StateRequest request) {

        State state = stateRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("State not found."));

        state.setName(request.getName());
        state.setCapital(request.getCapital());
        state.setDescription(request.getDescription());
        state.setThumbnailUrl(request.getThumbnailUrl());

        State updatedState = stateRepository.save(state);

        return stateMapper.toResponse(updatedState);
    }

    @Override
    public void deleteState(Long id) {

        if (!stateRepository.existsById(id)) {
            throw new EntityNotFoundException("State not found.");
        }

        stateRepository.deleteById(id);
    }
}