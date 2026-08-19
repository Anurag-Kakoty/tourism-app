package com.tourism.backend.destination.service;

import com.tourism.backend.destination.dto.DestinationRequest;
import com.tourism.backend.destination.dto.DestinationResponse;
import com.tourism.backend.destination.entity.Destination;
import com.tourism.backend.destination.entity.DestinationType;
import com.tourism.backend.destination.mapper.DestinationMapper;
import com.tourism.backend.destination.repository.DestinationRepository;
import com.tourism.backend.destination.specification.DestinationSpecification;
import com.tourism.backend.exception.DuplicateResourceException;
import com.tourism.backend.exception.ResourceNotFoundException;
import com.tourism.backend.state.entity.State;
import com.tourism.backend.state.repository.StateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DestinationServiceImpl implements DestinationService {

    private final DestinationRepository destinationRepository;
    private final StateRepository stateRepository;
    private final DestinationMapper mapper;

    @Override
    public DestinationResponse create(DestinationRequest request) {

        log.info(
                "Creating destination '{}'",
                request.getName()
        );

        if (destinationRepository.existsByNameIgnoreCaseAndState_Id(
                request.getName(),
                request.getStateId())) {

            throw new DuplicateResourceException(
                    "Destination already exists in this state."
            );
        }

        State state = getState(request.getStateId());

        Destination destination =
                mapper.toEntity(request, state);

        Destination saved =
                destinationRepository.save(destination);

        log.info(
                "Destination created with id {}",
                saved.getId()
        );

        return mapper.toResponse(saved);
    }

    @Override
    public DestinationResponse update(
            Long id,
            DestinationRequest request) {

        log.info(
                "Updating destination {}",
                id
        );

        Destination destination =
                getDestination(id);

        if (destinationRepository
                .existsByNameIgnoreCaseAndState_IdAndIdNot(
                        request.getName(),
                        request.getStateId(),
                        id)) {

            throw new DuplicateResourceException(
                    "Destination already exists in this state."
            );
        }

        State state =
                getState(request.getStateId());

        mapper.updateEntity(
                destination,
                request,
                state
        );

        Destination updated =
                destinationRepository.save(destination);

        log.info(
                "Destination {} updated",
                id
        );

        return mapper.toResponse(updated);
    }

    @Override
    public DestinationResponse getById(Long id) {

        return mapper.toResponse(
                getDestination(id)
        );
    }

    @Override
    public List<DestinationResponse> getAll(
            Long stateId,
            DestinationType type,
            Boolean featured,
            Boolean popular) {

        log.info(
                "Fetching destinations with filters: " +
                        "stateId={}, type={}, featured={}, popular={}",
                stateId,
                type,
                featured,
                popular
        );

        Specification<Destination> specification =
                Specification
                        .where(
                                DestinationSpecification.hasStateId(
                                        stateId
                                )
                        )
                        .and(
                                DestinationSpecification.hasType(
                                        type
                                )
                        )
                        .and(
                                DestinationSpecification.isFeatured(
                                        featured
                                )
                        )
                        .and(
                                DestinationSpecification.isPopular(
                                        popular
                                )
                        );

        return destinationRepository
                .findAll(
                        specification,
                        Sort.by(
                                Sort.Order.asc("displayOrder"),
                                Sort.Order.asc("name")
                        )
                )
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<DestinationResponse> getByState(
            Long stateId) {

        return getAll(
                stateId,
                null,
                null,
                null
        );
    }

    @Override
    public List<DestinationResponse> getByType(
            DestinationType type) {

        return getAll(
                null,
                type,
                null,
                null
        );
    }

    @Override
    public List<DestinationResponse> getFeatured() {

        return getAll(
                null,
                null,
                true,
                null
        );
    }

    @Override
    public List<DestinationResponse> getPopular() {

        return getAll(
                null,
                null,
                null,
                true
        );
    }

    @Override
    public void delete(Long id) {

        Destination destination =
                getDestination(id);

        destinationRepository.delete(destination);

        log.info(
                "Destination {} deleted",
                id
        );
    }

    private Destination getDestination(Long id) {

        return destinationRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Destination not found with id: " + id
                        )
                );
    }

    private State getState(Long id) {

        return stateRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "State not found with id: " + id
                        )
                );
    }
}