package com.tourism.backend.transport.service;

import com.tourism.backend.destination.entity.Destination;
import com.tourism.backend.destination.repository.DestinationRepository;
import com.tourism.backend.exception.DuplicateResourceException;
import com.tourism.backend.exception.ResourceNotFoundException;
import com.tourism.backend.transport.dto.TransportRequest;
import com.tourism.backend.transport.dto.TransportResponse;
import com.tourism.backend.transport.entity.Transport;
import com.tourism.backend.transport.entity.TransportType;
import com.tourism.backend.transport.mapper.TransportMapper;
import com.tourism.backend.transport.repository.TransportRepository;
import com.tourism.backend.transport.specification.TransportSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TransportServiceImpl implements TransportService {

    private final TransportRepository transportRepository;
    private final DestinationRepository destinationRepository;
    private final TransportMapper transportMapper;

    @Override
    public TransportResponse createTransport(
            TransportRequest request) {

        if (transportRepository
                .existsByProviderNameIgnoreCaseAndDestination_Id(
                        request.getProviderName(),
                        request.getDestinationId())) {

            throw new DuplicateResourceException(
                    "Transport provider already exists for this destination."
            );
        }

        Destination destination =
                destinationRepository.findById(
                        request.getDestinationId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Destination not found."
                        )
                );

        Transport transport =
                transportMapper.toEntity(
                        request,
                        destination
                );

        Transport savedTransport =
                transportRepository.save(transport);

        log.info(
                "Transport created with ID {}",
                savedTransport.getId()
        );

        return transportMapper.toResponse(savedTransport);
    }

    @Override
    public TransportResponse updateTransport(
            Long id,
            TransportRequest request) {

        Transport transport =
                transportRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Transport not found."
                                )
                        );

        if (transportRepository
                .existsByProviderNameIgnoreCaseAndDestination_IdAndIdNot(
                        request.getProviderName(),
                        request.getDestinationId(),
                        id)) {

            throw new DuplicateResourceException(
                    "Transport provider already exists for this destination."
            );
        }

        Destination destination =
                destinationRepository.findById(
                        request.getDestinationId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Destination not found."
                        )
                );

        transport.setType(request.getType());
        transport.setProviderName(request.getProviderName());
        transport.setPickupLocation(request.getPickupLocation());
        transport.setDropLocation(request.getDropLocation());
        transport.setEstimatedDuration(request.getEstimatedDuration());
        transport.setEstimatedFare(request.getEstimatedFare());
        transport.setContactNumber(request.getContactNumber());
        transport.setWebsite(request.getWebsite());
        transport.setBookingUrl(request.getBookingUrl());
        transport.setAvailable(request.getAvailable());
        transport.setDestination(destination);

        Transport updatedTransport =
                transportRepository.save(transport);

        log.info(
                "Transport updated with ID {}",
                id
        );

        return transportMapper.toResponse(updatedTransport);
    }

    @Override
    @Transactional(readOnly = true)
    public TransportResponse getTransportById(
            Long id) {

        Transport transport =
                transportRepository
                        .findWithDestinationById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Transport not found."
                                )
                        );

        return transportMapper.toResponse(transport);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransportResponse> getAllTransportOptions(
            Long destinationId,
            TransportType type,
            Boolean available) {

        log.info(
                "Fetching transport with filters: " +
                        "destinationId={}, type={}, available={}",
                destinationId,
                type,
                available
        );

        Specification<Transport> specification =
                Specification
                        .where(
                                TransportSpecification.hasDestinationId(
                                        destinationId
                                )
                        )
                        .and(
                                TransportSpecification.hasType(
                                        type
                                )
                        )
                        .and(
                                TransportSpecification.isAvailable(
                                        available
                                )
                        );

        return transportRepository
                .findAll(
                        specification,
                        Sort.by(
                                Sort.Order.asc("type"),
                                Sort.Order.asc("providerName")
                        )
                )
                .stream()
                .map(transportMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransportResponse> getTransportByDestination(
            Long destinationId) {

        return getAllTransportOptions(
                destinationId,
                null,
                null
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransportResponse> getTransportByType(
            TransportType type) {

        return getAllTransportOptions(
                null,
                type,
                null
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransportResponse> getTransportByAvailability(
            Boolean available) {

        return getAllTransportOptions(
                null,
                null,
                available
        );
    }

    @Override
    public void deleteTransport(Long id) {

        Transport transport =
                transportRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Transport not found."
                                )
                        );

        transportRepository.delete(transport);

        log.info(
                "Transport deleted with ID {}",
                id
        );
    }
}