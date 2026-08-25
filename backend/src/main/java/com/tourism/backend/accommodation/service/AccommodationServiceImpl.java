package com.tourism.backend.accommodation.service;

import com.tourism.backend.accommodation.dto.AccommodationRequest;
import com.tourism.backend.accommodation.dto.AccommodationResponse;
import com.tourism.backend.accommodation.entity.Accommodation;
import com.tourism.backend.accommodation.entity.AccommodationType;
import com.tourism.backend.accommodation.mapper.AccommodationMapper;
import com.tourism.backend.accommodation.repository.AccommodationRepository;
import com.tourism.backend.accommodation.specification.AccommodationSpecification;
import com.tourism.backend.destination.entity.Destination;
import com.tourism.backend.destination.repository.DestinationRepository;
import com.tourism.backend.exception.DuplicateResourceException;
import com.tourism.backend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AccommodationServiceImpl
        implements AccommodationService {

    private final AccommodationRepository accommodationRepository;
    private final DestinationRepository destinationRepository;
    private final AccommodationMapper mapper;

    @Override
    public AccommodationResponse create(
            AccommodationRequest request) {

        log.info(
                "Creating accommodation '{}'",
                request.getName()
        );

        if (accommodationRepository
                .existsByNameIgnoreCaseAndDestination_Id(
                        request.getName(),
                        request.getDestinationId())) {

            throw new DuplicateResourceException(
                    "Accommodation already exists in this destination."
            );
        }

        Destination destination =
                getDestination(request.getDestinationId());

        Accommodation accommodation =
                mapper.toEntity(request, destination);

        Accommodation saved =
                accommodationRepository.save(accommodation);

        log.info(
                "Accommodation created with id {}",
                saved.getId()
        );

        return mapper.toResponse(saved);
    }

    @Override
    public AccommodationResponse update(
            Long id,
            AccommodationRequest request) {

        log.info(
                "Updating accommodation {}",
                id
        );

        Accommodation accommodation =
                getAccommodation(id);

        if (accommodationRepository
                .existsByNameIgnoreCaseAndDestination_IdAndIdNot(
                        request.getName(),
                        request.getDestinationId(),
                        id)) {

            throw new DuplicateResourceException(
                    "Accommodation already exists in this destination."
            );
        }

        Destination destination =
                getDestination(request.getDestinationId());

        mapper.updateEntity(
                accommodation,
                request,
                destination
        );

        Accommodation updated =
                accommodationRepository.save(accommodation);

        log.info(
                "Accommodation {} updated",
                id
        );

        return mapper.toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public AccommodationResponse getById(Long id) {

        return mapper.toResponse(
                getAccommodation(id)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccommodationResponse> getAll(
            Long destinationId,
            AccommodationType type,
            Boolean available) {

        log.info(
                "Fetching accommodations with filters: " +
                        "destinationId={}, type={}, available={}",
                destinationId,
                type,
                available
        );

        Specification<Accommodation> specification =
                Specification
                        .where(
                                AccommodationSpecification.hasDestinationId(
                                        destinationId
                                )
                        )
                        .and(
                                AccommodationSpecification.hasType(
                                        type
                                )
                        )
                        .and(
                                AccommodationSpecification.isAvailable(
                                        available
                                )
                        );

        return accommodationRepository
                .findAll(
                        specification,
                        Sort.by(
                                Sort.Order.asc("name")
                        )
                )
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccommodationResponse> getByDestination(
            Long destinationId) {

        return getAll(
                destinationId,
                null,
                null
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccommodationResponse> getByType(
            AccommodationType type) {

        return getAll(
                null,
                type,
                null
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccommodationResponse> getByAvailable(
            Boolean available) {

        return getAll(
                null,
                null,
                available
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccommodationResponse> getByDestinationAndType(
            Long destinationId,
            AccommodationType type) {

        return getAll(
                destinationId,
                type,
                null
        );
    }

    @Override
    public void delete(Long id) {

        Accommodation accommodation =
                getAccommodation(id);

        accommodationRepository.delete(accommodation);

        log.info(
                "Accommodation {} deleted",
                id
        );
    }

    private Accommodation getAccommodation(Long id) {

        return accommodationRepository
                .findWithDestinationById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Accommodation not found with id: " + id
                        )
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
}