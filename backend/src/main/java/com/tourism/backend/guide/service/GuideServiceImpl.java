package com.tourism.backend.guide.service;

import com.tourism.backend.destination.entity.Destination;
import com.tourism.backend.destination.repository.DestinationRepository;
import com.tourism.backend.exception.DuplicateResourceException;
import com.tourism.backend.exception.ResourceNotFoundException;
import com.tourism.backend.guide.dto.GuideRequest;
import com.tourism.backend.guide.dto.GuideResponse;
import com.tourism.backend.guide.entity.Guide;
import com.tourism.backend.guide.entity.Language;
import com.tourism.backend.guide.mapper.GuideMapper;
import com.tourism.backend.guide.repository.GuideRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class GuideServiceImpl implements GuideService {

    private final GuideRepository guideRepository;
    private final DestinationRepository destinationRepository;
    private final GuideMapper guideMapper;

    @Override
    public GuideResponse createGuide(GuideRequest request) {

        if (guideRepository.existsByPhone(request.getPhone()))
            throw new DuplicateResourceException("Phone number already exists.");

        if (guideRepository.existsByEmailIgnoreCase(request.getEmail()))
            throw new DuplicateResourceException("Email already exists.");

        if (guideRepository.existsByLicenseNumber(request.getLicenseNumber()))
            throw new DuplicateResourceException("License number already exists.");

        Destination destination = destinationRepository.findById(request.getDestinationId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination not found."));

        Guide guide = guideMapper.toEntity(request, destination);

        Guide savedGuide = guideRepository.save(guide);

        log.info("Guide created with ID {}", savedGuide.getId());

        return guideMapper.toResponse(savedGuide);
    }

    @Override
    public GuideResponse updateGuide(Long id, GuideRequest request) {

        Guide guide = guideRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guide not found."));

        if (guideRepository.existsByPhoneAndIdNot(request.getPhone(), id))
            throw new DuplicateResourceException("Phone number already exists.");

        if (guideRepository.existsByEmailIgnoreCaseAndIdNot(request.getEmail(), id))
            throw new DuplicateResourceException("Email already exists.");

        if (guideRepository.existsByLicenseNumberAndIdNot(request.getLicenseNumber(), id))
            throw new DuplicateResourceException("License number already exists.");

        Destination destination = destinationRepository.findById(request.getDestinationId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination not found."));

        guide.setName(request.getName());
        guide.setBio(request.getBio());
        guide.setPhone(request.getPhone());
        guide.setEmail(request.getEmail());
        guide.setLanguages(request.getLanguages());
        guide.setYearsOfExperience(request.getYearsOfExperience());
        guide.setPricePerDay(request.getPricePerDay());
        guide.setRating(request.getRating() != null ? request.getRating() : 0.0);
        guide.setAvailable(request.getAvailable());
        guide.setLicenseNumber(request.getLicenseNumber());
        guide.setProvidesTransport(request.getProvidesTransport());
        guide.setImageUrl(request.getImageUrl());
        guide.setDestination(destination);

        log.info("Guide updated with ID {}", id);

        return guideMapper.toResponse(guideRepository.save(guide));
    }

    @Override
    @Transactional(readOnly = true)
    public GuideResponse getGuideById(Long id) {

        Guide guide = guideRepository.findWithDestinationById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guide not found."));

        return guideMapper.toResponse(guide);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GuideResponse> getAllGuides() {
        return guideRepository.findAllByOrderByRatingDescNameAsc()
                .stream()
                .map(guideMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GuideResponse> getGuidesByDestination(Long destinationId) {
        return guideRepository.findAllByDestination_IdOrderByRatingDescNameAsc(destinationId)
                .stream()
                .map(guideMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GuideResponse> getGuidesByAvailability(Boolean available) {
        return guideRepository.findAllByAvailableOrderByRatingDescNameAsc(available)
                .stream()
                .map(guideMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GuideResponse> getGuidesByProvidesTransport(Boolean providesTransport) {
        return guideRepository.findAllByProvidesTransportOrderByRatingDescNameAsc(providesTransport)
                .stream()
                .map(guideMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GuideResponse> getGuidesByLanguage(Language language) {
        return guideRepository.findAllByLanguage(language)
                .stream()
                .map(guideMapper::toResponse)
                .toList();
    }

    @Override
    public void deleteGuide(Long id) {

        Guide guide = guideRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guide not found."));

        guideRepository.delete(guide);

        log.info("Guide deleted with ID {}", id);
    }
}