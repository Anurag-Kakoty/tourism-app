package com.tourism.backend.service.impl;

import com.tourism.backend.dto.state.StateRequest;
import com.tourism.backend.dto.state.StateResponse;
import com.tourism.backend.entity.State;
import com.tourism.backend.exception.DuplicateResourceException;
import com.tourism.backend.exception.ResourceNotFoundException;
import com.tourism.backend.mapper.StateMapper;
import com.tourism.backend.repository.StateRepository;
import com.tourism.backend.service.StateService;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class StateServiceImpl implements StateService {

    private static final Logger logger =
            LoggerFactory.getLogger(StateServiceImpl.class);

    private final StateRepository stateRepository;
    private final StateMapper stateMapper;

    public StateServiceImpl(StateRepository stateRepository,
                            StateMapper stateMapper) {
        this.stateRepository = stateRepository;
        this.stateMapper = stateMapper;
    }

    @Override
    public StateResponse createState(StateRequest request) {

        logger.info("Creating state '{}'", request.getName());

        if (stateRepository.existsByName(request.getName())) {

            logger.warn("Duplicate state creation attempted: '{}'",
                    request.getName());

            throw new DuplicateResourceException(
                    "State '" + request.getName() + "' already exists."
            );
        }

        State state = stateMapper.toEntity(request);

        State savedState = stateRepository.save(state);

        logger.info("State '{}' created successfully with id {}",
                savedState.getName(),
                savedState.getId());

        return stateMapper.toResponse(savedState);
    }

    @Override
    public List<StateResponse> getAllStates(String name) {

        if (name != null && !name.isBlank()) {

            State state = stateRepository.findByNameIgnoreCase(name)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "State with name '" + name + "' not found"));

            return List.of(stateMapper.toResponse(state));
        }

        return stateRepository.findAll()
                .stream()
                .map(stateMapper::toResponse)
                .toList();
    }

    @Override
    public StateResponse getStateById(Long id) {

        logger.info("Fetching state with id {}", id);

        State state = stateRepository.findById(id)
                .orElseThrow(() -> {

                    logger.warn("State with id {} not found", id);

                    return new ResourceNotFoundException(
                            "State with id " + id + " not found."
                    );
                });

        return stateMapper.toResponse(state);
    }

    @Override
    public StateResponse updateState(Long id, StateRequest request) {

        State state = stateRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "State with id " + id + " not found."
                        ));

        // Prevent duplicate names when updating
        stateRepository.findByName(request.getName())
                .ifPresent(existingState -> {
                    if (!existingState.getId().equals(id)) {
                        throw new DuplicateResourceException(
                                "State '" + request.getName() + "' already exists."
                        );
                    }
                });

        state.setName(request.getName());
        state.setCapital(request.getCapital());
        state.setDescription(request.getDescription());
        state.setThumbnailUrl(request.getThumbnailUrl());

        State updatedState = stateRepository.save(state);

        return stateMapper.toResponse(updatedState);
    }

    @Override
    public void deleteState(Long id) {

        logger.info("Deleting state with id {}", id);

        if (!stateRepository.existsById(id)) {

            logger.warn("Delete failed. State with id {} not found", id);

            throw new ResourceNotFoundException(
                    "State with id " + id + " not found."
            );
        }

        stateRepository.deleteById(id);

        logger.info("State with id {} deleted successfully", id);
    }
}