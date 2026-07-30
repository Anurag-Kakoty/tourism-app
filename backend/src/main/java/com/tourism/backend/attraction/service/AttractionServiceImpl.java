package com.tourism.backend.attraction.service;

import com.tourism.backend.attraction.dto.AttractionRequest;
import com.tourism.backend.attraction.dto.AttractionResponse;
import com.tourism.backend.attraction.entity.Attraction;
import com.tourism.backend.attraction.mapper.AttractionMapper;
import com.tourism.backend.attraction.repository.AttractionRepository;
import com.tourism.backend.destination.entity.Destination;
import com.tourism.backend.destination.repository.DestinationRepository;
import com.tourism.backend.exception.DuplicateResourceException;
import com.tourism.backend.exception.ResourceNotFoundException;
import com.tourism.backend.experience.entity.Experience;
import com.tourism.backend.experience.repository.ExperienceRepository;
import com.tourism.backend.tag.entity.Tag;
import com.tourism.backend.tag.repository.TagRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class AttractionServiceImpl implements AttractionService {

    private static final Logger logger =
            LoggerFactory.getLogger(AttractionServiceImpl.class);

    private final AttractionRepository attractionRepository;
    private final DestinationRepository destinationRepository;
    private final TagRepository tagRepository;
    private final ExperienceRepository experienceRepository;
    private final AttractionMapper attractionMapper;

    public AttractionServiceImpl(
            AttractionRepository attractionRepository,
            DestinationRepository destinationRepository,
            TagRepository tagRepository,
            ExperienceRepository experienceRepository,
            AttractionMapper attractionMapper) {

        this.attractionRepository = attractionRepository;
        this.destinationRepository = destinationRepository;
        this.tagRepository = tagRepository;
        this.experienceRepository = experienceRepository;
        this.attractionMapper = attractionMapper;
    }

    @Override
    public AttractionResponse createAttraction(AttractionRequest request) {

        logger.info("Creating attraction '{}'", request.getName());

        Destination destination = destinationRepository
                .findById(request.getDestinationId())
                .orElseThrow(() -> {

                    logger.warn(
                            "Destination with id {} not found",
                            request.getDestinationId());

                    return new ResourceNotFoundException(
                            "Destination with id "
                                    + request.getDestinationId()
                                    + " not found."
                    );
                });

        attractionRepository
                .findByNameIgnoreCaseAndDestination_Id(
                        request.getName(),
                        request.getDestinationId())
                .ifPresent(existing -> {

                    logger.warn(
                            "Duplicate attraction '{}' attempted for destination '{}'",
                            request.getName(),
                            destination.getName());

                    throw new DuplicateResourceException(
                            "Attraction '"
                                    + request.getName()
                                    + "' already exists in destination '"
                                    + destination.getName()
                                    + "'.");
                });

        Set<Tag> tags = getTagsFromRequest(request);
        Set<Experience> experiences = getExperiencesFromRequest(request);

        Attraction attraction = attractionMapper.toEntity(
                request,
                destination,
                tags,
                experiences
        );

        Attraction savedAttraction =
                attractionRepository.save(attraction);

        logger.info(
                "Attraction '{}' created successfully with id {}",
                savedAttraction.getName(),
                savedAttraction.getId());

        return attractionMapper.toResponse(savedAttraction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttractionResponse> getAllAttractions(Long destinationId) {

        logger.info("Fetching attractions");

        if (destinationId != null) {

            logger.info(
                    "Filtering attractions by destination {}",
                    destinationId);

            return attractionRepository
                    .findAllByDestination_IdOrderByDisplayOrderAscNameAsc(
                            destinationId)
                    .stream()
                    .map(attractionMapper::toResponse)
                    .toList();
        }

        return attractionRepository
                .findAllByOrderByDisplayOrderAscNameAsc()
                .stream()
                .map(attractionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttractionResponse> getFeaturedAttractions() {

        logger.info("Fetching featured attractions");

        return attractionRepository
                .findAllByFeaturedTrueOrderByDisplayOrderAscNameAsc()
                .stream()
                .map(attractionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AttractionResponse getAttractionById(Long id) {

        logger.info("Fetching attraction with id {}", id);

        Attraction attraction = attractionRepository
                .findWithDestinationById(id)
                .orElseThrow(() -> {

                    logger.warn("Attraction with id {} not found", id);

                    return new ResourceNotFoundException(
                            "Attraction with id " + id + " not found."
                    );
                });

        return attractionMapper.toResponse(attraction);
    }

    @Override
    public AttractionResponse updateAttraction(
            Long id,
            AttractionRequest request) {

        logger.info("Updating attraction with id {}", id);

        Attraction attraction = attractionRepository
                .findWithDestinationById(id)
                .orElseThrow(() -> {

                    logger.warn("Attraction with id {} not found", id);

                    return new ResourceNotFoundException(
                            "Attraction with id " + id + " not found."
                    );
                });

        Destination destination = destinationRepository
                .findById(request.getDestinationId())
                .orElseThrow(() -> {

                    logger.warn(
                            "Destination with id {} not found",
                            request.getDestinationId());

                    return new ResourceNotFoundException(
                            "Destination with id "
                                    + request.getDestinationId()
                                    + " not found."
                    );
                });

        attractionRepository
                .findByNameIgnoreCaseAndDestination_Id(
                        request.getName(),
                        request.getDestinationId())
                .ifPresent(existing -> {

                    if (!existing.getId().equals(id)) {

                        logger.warn(
                                "Duplicate attraction '{}' attempted during update",
                                request.getName());

                        throw new DuplicateResourceException(
                                "Attraction '"
                                        + request.getName()
                                        + "' already exists in destination '"
                                        + destination.getName()
                                        + "'.");
                    }
                });

        Set<Tag> tags = getTagsFromRequest(request);
        Set<Experience> experiences = getExperiencesFromRequest(request);

        attractionMapper.updateEntity(
                attraction,
                request,
                destination,
                tags,
                experiences
        );

        Attraction updatedAttraction =
                attractionRepository.save(attraction);

        logger.info(
                "Attraction '{}' updated successfully",
                updatedAttraction.getName());

        return attractionMapper.toResponse(updatedAttraction);
    }

    @Override
    public void deleteAttraction(Long id) {

        logger.info("Deleting attraction with id {}", id);

        Attraction attraction = attractionRepository
                .findById(id)
                .orElseThrow(() -> {

                    logger.warn("Attraction with id {} not found", id);

                    return new ResourceNotFoundException(
                            "Attraction with id " + id + " not found."
                    );
                });

        attractionRepository.delete(attraction);

        logger.info(
                "Attraction with id {} deleted successfully",
                id);
    }

    private Set<Tag> getTagsFromRequest(AttractionRequest request) {

        if (request.getTagIds() == null || request.getTagIds().isEmpty()) {
            return Set.of();
        }

        return request.getTagIds()
                .stream()
                .map(id -> tagRepository.findById(id)
                        .orElseThrow(() -> {

                            logger.warn("Tag with id {} not found", id);

                            return new ResourceNotFoundException(
                                    "Tag with id " + id + " not found."
                            );
                        }))
                .collect(Collectors.toSet());
    }

    private Set<Experience> getExperiencesFromRequest(
            AttractionRequest request) {

        if (request.getExperienceIds() == null
                || request.getExperienceIds().isEmpty()) {
            return Set.of();
        }

        return request.getExperienceIds()
                .stream()
                .map(id -> experienceRepository.findById(id)
                        .orElseThrow(() -> {

                            logger.warn(
                                    "Experience with id {} not found",
                                    id);

                            return new ResourceNotFoundException(
                                    "Experience with id "
                                            + id
                                            + " not found."
                            );
                        }))
                .collect(Collectors.toSet());
    }
}