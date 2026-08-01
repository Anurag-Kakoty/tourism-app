package com.tourism.backend.itinerary.service;

import com.tourism.backend.accommodation.entity.Accommodation;
import com.tourism.backend.accommodation.repository.AccommodationRepository;
import com.tourism.backend.attraction.entity.Attraction;
import com.tourism.backend.attraction.repository.AttractionRepository;
import com.tourism.backend.destination.entity.Destination;
import com.tourism.backend.destination.repository.DestinationRepository;
import com.tourism.backend.exception.ResourceNotFoundException;
import com.tourism.backend.festival.entity.Festival;
import com.tourism.backend.festival.repository.FestivalRepository;
import com.tourism.backend.guide.entity.Guide;
import com.tourism.backend.guide.repository.GuideRepository;
import com.tourism.backend.itinerary.dto.ItineraryItemRequest;
import com.tourism.backend.itinerary.dto.ItineraryItemResponse;
import com.tourism.backend.itinerary.dto.ItineraryRequest;
import com.tourism.backend.itinerary.dto.ItineraryResponse;
import com.tourism.backend.itinerary.entity.ActivityType;
import com.tourism.backend.itinerary.entity.Itinerary;
import com.tourism.backend.itinerary.entity.ItineraryItem;
import com.tourism.backend.itinerary.mapper.ItineraryItemMapper;
import com.tourism.backend.itinerary.mapper.ItineraryMapper;
import com.tourism.backend.itinerary.repository.ItineraryItemRepository;
import com.tourism.backend.itinerary.repository.ItineraryRepository;
import com.tourism.backend.restaurant.entity.Restaurant;
import com.tourism.backend.restaurant.repository.RestaurantRepository;
import com.tourism.backend.transport.entity.Transport;
import com.tourism.backend.transport.repository.TransportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ItineraryServiceImpl implements ItineraryService {

    private final ItineraryRepository itineraryRepository;
    private final ItineraryItemRepository itineraryItemRepository;

    private final DestinationRepository destinationRepository;

    private final AttractionRepository attractionRepository;
    private final AccommodationRepository accommodationRepository;
    private final RestaurantRepository restaurantRepository;
    private final GuideRepository guideRepository;
    private final TransportRepository transportRepository;
    private final FestivalRepository festivalRepository;

    private final ItineraryMapper itineraryMapper;
    private final ItineraryItemMapper itineraryItemMapper;

    @Override
    public ItineraryResponse createItinerary(
            ItineraryRequest request) {

        Destination destination =
                getDestination(request.getDestinationId());

        Itinerary itinerary =
                itineraryMapper.toEntity(
                        request,
                        destination);

        Itinerary saved =
                itineraryRepository.save(itinerary);

        log.info(
                "Itinerary created with ID {}",
                saved.getId());

        return buildResponse(saved);
    }

    @Override
    public ItineraryResponse updateItinerary(
            Long id,
            ItineraryRequest request) {

        Itinerary itinerary =
                getItinerary(id);

        Destination destination =
                getDestination(request.getDestinationId());

        itineraryMapper.updateEntity(
                itinerary,
                request,
                destination);

        Itinerary updated =
                itineraryRepository.save(itinerary);

        log.info(
                "Itinerary {} updated",
                id);

        return buildResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public ItineraryResponse getItineraryById(Long id) {

        Itinerary itinerary = getItinerary(id);

        return buildResponse(itinerary);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItineraryResponse> getAllItineraries() {

        return itineraryRepository.findAllBy()
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItineraryResponse> getItinerariesByDestination(
            Long destinationId) {

        return itineraryRepository
                .findAllByDestination_Id(destinationId)
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    @Override
    public void deleteItinerary(Long id) {

        Itinerary itinerary = getItinerary(id);

        itineraryRepository.delete(itinerary);

        log.info(
                "Itinerary {} deleted",
                id);
    }

    @Override
    public ItineraryResponse addItem(
            Long itineraryId,
            ItineraryItemRequest request) {

        Itinerary itinerary = getItinerary(itineraryId);

        validateReference(
                request.getActivityType(),
                request.getReferenceId());

        ItineraryItem item =
                itineraryItemMapper.toEntity(
                        request,
                        itinerary);

        itineraryItemRepository.save(item);

        log.info(
                "Item added to itinerary {}",
                itineraryId);

        return buildResponse(itinerary);
    }

    @Override
    public ItineraryResponse updateItem(
            Long itineraryId,
            Long itemId,
            ItineraryItemRequest request) {

        Itinerary itinerary = getItinerary(itineraryId);

        ItineraryItem item = getItem(itemId);

        if (!item.getItinerary().getId().equals(itineraryId)) {

            throw new ResourceNotFoundException(
                    "Item does not belong to itinerary.");
        }

        validateReference(
                request.getActivityType(),
                request.getReferenceId());

        itineraryItemMapper.updateEntity(
                item,
                request);

        itineraryItemRepository.save(item);

        log.info(
                "Item {} updated",
                itemId);

        return buildResponse(itinerary);
    }

    @Override
    public ItineraryResponse deleteItem(
            Long itineraryId,
            Long itemId) {

        Itinerary itinerary = getItinerary(itineraryId);

        ItineraryItem item = getItem(itemId);

        if (!item.getItinerary().getId().equals(itineraryId)) {

            throw new ResourceNotFoundException(
                    "Item does not belong to itinerary.");
        }

        itineraryItemRepository.delete(item);

        log.info(
                "Item {} deleted",
                itemId);

        return buildResponse(itinerary);
    }

    private ItineraryResponse buildResponse(
            Itinerary itinerary) {

        ItineraryResponse response =
                itineraryMapper.toResponse(itinerary);

        List<ItineraryItemResponse> items =
                itineraryItemRepository
                        .findAllByItinerary_IdOrderByDayNumberAscActivityOrderAsc(
                                itinerary.getId())
                        .stream()
                        .map(item -> {

                            ItineraryItemResponse dto =
                                    itineraryItemMapper.toResponse(item);

                            dto.setReferenceName(
                                    getReferenceName(
                                            item.getActivityType(),
                                            item.getReferenceId()));

                            return dto;
                        })
                        .toList();

        response.setItems(items);

        return response;
    }

    private void validateReference(
            ActivityType activityType,
            Long referenceId) {

        getReferenceEntity(
                activityType,
                referenceId);
    }

    private Object getReferenceEntity(
            ActivityType activityType,
            Long referenceId) {

        return switch (activityType) {

            case ATTRACTION ->
                    attractionRepository.findById(referenceId)
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Attraction not found."));

            case ACCOMMODATION ->
                    accommodationRepository.findById(referenceId)
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Accommodation not found."));

            case RESTAURANT ->
                    restaurantRepository.findById(referenceId)
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Restaurant not found."));

            case GUIDE ->
                    guideRepository.findById(referenceId)
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Guide not found."));

            case TRANSPORT ->
                    transportRepository.findById(referenceId)
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Transport not found."));

            case FESTIVAL ->
                    festivalRepository.findById(referenceId)
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Festival not found."));
        };
    }

    private String getReferenceName(
            ActivityType activityType,
            Long referenceId) {

        Object entity =
                getReferenceEntity(
                        activityType,
                        referenceId);

        return switch (activityType) {

            case ATTRACTION ->
                    ((Attraction) entity).getName();

            case ACCOMMODATION ->
                    ((Accommodation) entity).getName();

            case RESTAURANT ->
                    ((Restaurant) entity).getName();

            case GUIDE ->
                    ((Guide) entity).getName();

            case TRANSPORT ->
                    ((Transport) entity).getProviderName();

            case FESTIVAL ->
                    ((Festival) entity).getName();
        };
    }

    private Destination getDestination(Long id) {

        return destinationRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Destination not found."));
    }

    private Itinerary getItinerary(Long id) {

        return itineraryRepository.findWithItemsById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Itinerary not found."));
    }

    private ItineraryItem getItem(Long id) {

        return itineraryItemRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Itinerary item not found."));
    }
}