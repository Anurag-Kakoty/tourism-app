package com.tourism.backend.destination.service;

import com.tourism.backend.destination.dto.DestinationRequest;
import com.tourism.backend.destination.dto.DestinationResponse;
import com.tourism.backend.destination.entity.Destination;
import com.tourism.backend.destination.mapper.DestinationMapper;
import com.tourism.backend.destination.repository.DestinationRepository;
import com.tourism.backend.exception.ResourceNotFoundException;
import com.tourism.backend.state.entity.State;
import com.tourism.backend.state.repository.StateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DestinationServiceImpl implements DestinationService {

    private final DestinationRepository destinationRepository;

    private final StateRepository stateRepository;

    private final DestinationMapper destinationMapper;

    @Override
    public DestinationResponse createDestination(DestinationRequest request) {

        State state = stateRepository.findById(request.getStateId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "State with id "
                                        + request.getStateId()
                                        + " not found"));

        Destination destination =
                destinationMapper.toEntity(request, state);

        destination = destinationRepository.save(destination);

        return destinationMapper.toResponse(destination);
    }

    @Override
    public List<DestinationResponse> getAllDestinations(String state) {
        return List.of();
    }

    @Override
    public DestinationResponse getDestinationById(Long id) {
        return null;
    }

    @Override
    public DestinationResponse updateDestination(Long id, DestinationRequest request) {
        return null;
    }

    @Override
    public void deleteDestination(Long id) {

    }
}