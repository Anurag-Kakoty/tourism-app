package com.tourism.backend.experience.service;

import com.tourism.backend.exception.DuplicateResourceException;
import com.tourism.backend.exception.ResourceNotFoundException;
import com.tourism.backend.experience.dto.ExperienceRequest;
import com.tourism.backend.experience.dto.ExperienceResponse;
import com.tourism.backend.experience.entity.Experience;
import com.tourism.backend.experience.mapper.ExperienceMapper;
import com.tourism.backend.experience.repository.ExperienceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExperienceServiceImpl implements ExperienceService {

    private static final Logger logger =
            LoggerFactory.getLogger(ExperienceServiceImpl.class);

    private final ExperienceRepository experienceRepository;
    private final ExperienceMapper experienceMapper;

    public ExperienceServiceImpl(
            ExperienceRepository experienceRepository,
            ExperienceMapper experienceMapper) {

        this.experienceRepository = experienceRepository;
        this.experienceMapper = experienceMapper;
    }

    @Override
    public ExperienceResponse createExperience(
            ExperienceRequest request) {

        logger.info("Creating experience '{}'",
                request.getName());

        experienceRepository.findByNameIgnoreCase(request.getName())
                .ifPresent(existing -> {

                    logger.warn(
                            "Duplicate experience '{}' attempted",
                            request.getName());

                    throw new DuplicateResourceException(
                            "Experience '" + request.getName()
                                    + "' already exists."
                    );
                });

        Experience experience =
                experienceMapper.toEntity(request);

        Experience savedExperience =
                experienceRepository.save(experience);

        logger.info(
                "Experience '{}' created successfully with id {}",
                savedExperience.getName(),
                savedExperience.getId());

        return experienceMapper.toResponse(savedExperience);
    }

    @Override
    public List<ExperienceResponse> getAllExperiences() {

        logger.info("Fetching all experiences");

        return experienceRepository.findAll()
                .stream()
                .map(experienceMapper::toResponse)
                .toList();
    }

    @Override
    public ExperienceResponse getExperienceById(Long id) {

        logger.info("Fetching experience with id {}", id);

        Experience experience = experienceRepository.findById(id)
                .orElseThrow(() -> {

                    logger.warn(
                            "Experience with id {} not found",
                            id);

                    return new ResourceNotFoundException(
                            "Experience with id "
                                    + id
                                    + " not found."
                    );
                });

        return experienceMapper.toResponse(experience);
    }

    @Override
    public ExperienceResponse updateExperience(
            Long id,
            ExperienceRequest request) {

        logger.info(
                "Updating experience with id {}",
                id);

        Experience experience = experienceRepository.findById(id)
                .orElseThrow(() -> {

                    logger.warn(
                            "Experience with id {} not found",
                            id);

                    return new ResourceNotFoundException(
                            "Experience with id "
                                    + id
                                    + " not found."
                    );
                });

        experienceRepository.findByNameIgnoreCase(request.getName())
                .ifPresent(existing -> {

                    if (!existing.getId().equals(id)) {

                        logger.warn(
                                "Duplicate experience '{}' attempted during update",
                                request.getName());

                        throw new DuplicateResourceException(
                                "Experience '"
                                        + request.getName()
                                        + "' already exists."
                        );
                    }
                });

        experienceMapper.updateEntity(
                experience,
                request
        );

        Experience updatedExperience =
                experienceRepository.save(experience);

        logger.info(
                "Experience '{}' updated successfully",
                updatedExperience.getName());

        return experienceMapper.toResponse(updatedExperience);
    }

    @Override
    public void deleteExperience(Long id) {

        logger.info("Deleting experience with id {}", id);

        if (!experienceRepository.existsById(id)) {

            logger.warn(
                    "Delete failed. Experience with id {} not found",
                    id);

            throw new ResourceNotFoundException(
                    "Experience with id " + id + " not found."
            );
        }

        experienceRepository.deleteById(id);

        logger.info(
                "Experience with id {} deleted successfully",
                id);
    }

}