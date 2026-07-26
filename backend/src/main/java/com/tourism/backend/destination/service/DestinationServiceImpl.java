package com.tourism.backend.destination.service;

import com.tourism.backend.destination.dto.DestinationRequest;
import com.tourism.backend.destination.dto.DestinationResponse;
import com.tourism.backend.destination.entity.Destination;
import com.tourism.backend.destination.mapper.DestinationMapper;
import com.tourism.backend.destination.repository.DestinationRepository;
import com.tourism.backend.exception.DuplicateResourceException;
import com.tourism.backend.exception.ResourceNotFoundException;
import com.tourism.backend.state.entity.State;
import com.tourism.backend.state.repository.StateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DestinationServiceImpl implements DestinationService {

    private static final Logger logger =
            LoggerFactory.getLogger(DestinationServiceImpl.class);

    private final DestinationRepository destinationRepository;
    private final StateRepository stateRepository;
    private final DestinationMapper destinationMapper;

    public DestinationServiceImpl(DestinationRepository destinationRepository,
                                  StateRepository stateRepository,
                                  DestinationMapper destinationMapper) {
        this.destinationRepository = destinationRepository;
        this.stateRepository = stateRepository;
        this.destinationMapper = destinationMapper;
    }

    @Override
    public DestinationResponse createDestination(DestinationRequest request) {

        logger.info("Creating destination '{}'", request.getName());

        State state = stateRepository.findById(request.getStateId())
                .orElseThrow(() -> {

                    logger.warn("State with id {} not found",
                            request.getStateId());

                    return new ResourceNotFoundException(
                            "State with id " + request.getStateId() + " not found."
                    );
                });

        destinationRepository
                .findByNameIgnoreCaseAndState_Id(
                        request.getName(),
                        request.getStateId())
                .ifPresent(existing -> {

                    logger.warn(
                            "Duplicate destination '{}' attempted for state '{}'",
                            request.getName(),
                            state.getName());

                    throw new DuplicateResourceException(
                            "Destination '" + request.getName()
                                    + "' already exists in state '"
                                    + state.getName() + "'.");
                });

        Destination destination =
                destinationMapper.toEntity(request, state);

        Destination savedDestination =
                destinationRepository.save(destination);

        logger.info("Destination '{}' created successfully with id {}",
                savedDestination.getName(),
                savedDestination.getId());

        return destinationMapper.toResponse(savedDestination);
    }

    @Override
    public List<DestinationResponse> getAllDestinations(String state) {

        logger.info("Fetching destinations");

        if (state != null && !state.isBlank()) {

            logger.info("Filtering destinations by state '{}'", state);

            return destinationRepository.findByState_NameIgnoreCase(state)
                    .stream()
                    .map(destinationMapper::toResponse)
                    .toList();
        }

        return destinationRepository.findAll()
                .stream()
                .map(destinationMapper::toResponse)
                .toList();
    }

    @Override
    public DestinationResponse getDestinationById(Long id) {

        logger.info("Fetching destination with id {}", id);

        Destination destination = destinationRepository.findById(id)
                .orElseThrow(() -> {

                    logger.warn("Destination with id {} not found", id);

                    return new ResourceNotFoundException(
                            "Destination with id " + id + " not found."
                    );
                });

        return destinationMapper.toResponse(destination);
    }

    @Override
    public DestinationResponse updateDestination(Long id,
                                                 DestinationRequest request) {

        logger.info("Updating destination with id {}", id);

        Destination destination = destinationRepository.findById(id)
                .orElseThrow(() -> {

                    logger.warn("Destination with id {} not found", id);

                    return new ResourceNotFoundException(
                            "Destination with id " + id + " not found."
                    );
                });

        State state = stateRepository.findById(request.getStateId())
                .orElseThrow(() -> {

                    logger.warn("State with id {} not found",
                            request.getStateId());

                    return new ResourceNotFoundException(
                            "State with id " + request.getStateId() + " not found."
                    );
                });

        destinationRepository
                .findByNameIgnoreCaseAndState_Id(
                        request.getName(),
                        request.getStateId())
                .ifPresent(existing -> {

                    if (!existing.getId().equals(id)) {

                        logger.warn(
                                "Duplicate destination '{}' attempted during update",
                                request.getName());

                        throw new DuplicateResourceException(
                                "Destination '" + request.getName()
                                        + "' already exists in state '"
                                        + state.getName() + "'.");
                    }
                });

        destinationMapper.updateEntity(destination, request, state);

        Destination updatedDestination =
                destinationRepository.save(destination);

        logger.info("Destination '{}' updated successfully",
                updatedDestination.getName());

        return destinationMapper.toResponse(updatedDestination);
    }

    @Override
    public void deleteDestination(Long id) {

        logger.info("Deleting destination with id {}", id);

        if (!destinationRepository.existsById(id)) {

            logger.warn("Delete failed. Destination with id {} not found", id);

            throw new ResourceNotFoundException(
                    "Destination with id " + id + " not found."
            );
        }

        destinationRepository.deleteById(id);

        logger.info("Destination with id {} deleted successfully", id);
    }
}