package com.tourism.backend.ai.service;

import com.tourism.backend.accommodation.entity.Accommodation;
import com.tourism.backend.accommodation.repository.AccommodationRepository;
import com.tourism.backend.attraction.entity.Attraction;
import com.tourism.backend.attraction.repository.AttractionRepository;
import com.tourism.backend.ai.dto.AiCandidateContext;
import com.tourism.backend.ai.dto.AiItineraryRequest;
import com.tourism.backend.ai.dto.candidate.AccommodationCandidate;
import com.tourism.backend.ai.dto.candidate.AttractionCandidate;
import com.tourism.backend.ai.dto.candidate.FestivalCandidate;
import com.tourism.backend.ai.dto.candidate.GuideCandidate;
import com.tourism.backend.ai.dto.candidate.RestaurantCandidate;
import com.tourism.backend.ai.dto.candidate.TransportCandidate;
import com.tourism.backend.destination.entity.Destination;
import com.tourism.backend.destination.repository.DestinationRepository;
import com.tourism.backend.exception.ResourceNotFoundException;
import com.tourism.backend.festivaloccurrence.entity.FestivalOccurrence;
import com.tourism.backend.festivaloccurrence.repository.FestivalOccurrenceRepository;
import com.tourism.backend.festivaloccurrence.specification.FestivalOccurrenceSpecification;
import com.tourism.backend.guide.entity.Guide;
import com.tourism.backend.guide.repository.GuideRepository;
import com.tourism.backend.guide.specification.GuideSpecification;
import com.tourism.backend.restaurant.entity.Restaurant;
import com.tourism.backend.restaurant.repository.RestaurantRepository;
import com.tourism.backend.transport.entity.Transport;
import com.tourism.backend.transport.repository.TransportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CandidateServiceImpl implements CandidateService {

    private final AttractionRepository attractionRepository;
    private final AccommodationRepository accommodationRepository;
    private final RestaurantRepository restaurantRepository;
    private final GuideRepository guideRepository;
    private final TransportRepository transportRepository;
    private final DestinationRepository destinationRepository;
    private final FestivalOccurrenceRepository festivalOccurrenceRepository;

    @Override
    public AiCandidateContext getCandidates(AiItineraryRequest request) {
        return AiCandidateContext.builder()
                .attractions(getAttractions(request))
                .accommodations(getAccommodations(request))
                .restaurants(getRestaurants(request))
                .guides(getGuides(request))
                .transport(getTransport(request))
                .festivals(getFestivals(request))
                .build();
    }

    @Override
    public List<AttractionCandidate> getAttractions(
            AiItineraryRequest request) {

        List<Attraction> attractions =
                attractionRepository
                        .findAllByDestination_IdOrderByDisplayOrderAscNameAsc(
                                request.getDestinationId()
                        );

        return attractions.stream()
                .map(this::toAttractionCandidate)
                .toList();
    }

    @Override
    public List<AccommodationCandidate> getAccommodations(
            AiItineraryRequest request) {

        Specification<Accommodation> specification =
                Specification.<Accommodation>where(
                        (root, query, criteriaBuilder) ->
                                criteriaBuilder.equal(
                                        root.get("destination").get("id"),
                                        request.getDestinationId()
                                )
                ).and(
                        (root, query, criteriaBuilder) ->
                                criteriaBuilder.equal(
                                        root.get("available"),
                                        true
                                )
                );

        return accommodationRepository.findAll(specification)
                .stream()
                .map(this::toAccommodationCandidate)
                .toList();
    }

    @Override
    public List<RestaurantCandidate> getRestaurants(
            AiItineraryRequest request) {

        return restaurantRepository
                .findAllByDestination_IdOrderByRatingDescNameAsc(
                        request.getDestinationId()
                )
                .stream()
                .map(this::toRestaurantCandidate)
                .toList();
    }

    @Override
    public List<GuideCandidate> getGuides(
            AiItineraryRequest request) {

        Specification<Guide> specification =
                Specification
                        .where(GuideSpecification.hasDestinationId(
                                request.getDestinationId()
                        ))
                        .and(GuideSpecification.isAvailable(true));

        return guideRepository.findAll(specification)
                .stream()
                .map(this::toGuideCandidate)
                .toList();
    }

    @Override
    public List<TransportCandidate> getTransport(
            AiItineraryRequest request) {

        return transportRepository
                .findAllByDestination_IdOrderByTypeAscProviderNameAsc(
                        request.getDestinationId()
                )
                .stream()
                .filter(Transport::getAvailable)
                .map(this::toTransportCandidate)
                .toList();
    }

    @Override
    public List<FestivalCandidate> getFestivals(
            AiItineraryRequest request) {

        Destination destination =
                destinationRepository.findById(request.getDestinationId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Destination not found."
                        ));

        Long stateId = destination.getState().getId();

        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();

        Specification<FestivalOccurrence> specification =
                Specification
                        .<FestivalOccurrence>where(
                                FestivalOccurrenceSpecification
                                        .hasStateId(stateId)
                        )
                        .and(
                                FestivalOccurrenceSpecification
                                        .hasYear(startDate.getYear())
                        )
                        .and(
                                FestivalOccurrenceSpecification
                                        .startsOnOrBefore(endDate)
                        )
                        .and(
                                FestivalOccurrenceSpecification
                                        .endsOnOrAfter(startDate)
                        );

        if (request.getFestivalId() != null) {
            specification = specification.and(
                    FestivalOccurrenceSpecification.hasFestivalId(
                            request.getFestivalId()
                    )
            );
        }

        List<FestivalOccurrence> occurrences =
                festivalOccurrenceRepository.findAll(
                        specification,
                        Sort.by(
                                Sort.Direction.ASC,
                                "startDate"
                        )
                );

        return occurrences.stream()
                .map(this::toFestivalCandidate)
                .toList();
    }

    private AttractionCandidate toAttractionCandidate(
            Attraction attraction) {

        return AttractionCandidate.builder()
                .id(attraction.getId())
                .name(attraction.getName())
                .description(attraction.getDescription())
                .bestSeason(attraction.getBestSeason())
                .entryFee(attraction.getEntryFee())
                .featured(attraction.getFeatured())
                .experienceIds(
                        attraction.getExperiences()
                                .stream()
                                .map(experience -> experience.getId())
                                .toList()
                )
                .experiences(
                        attraction.getExperiences()
                                .stream()
                                .map(experience -> experience.getName())
                                .toList()
                )
                .tags(
                        attraction.getTags()
                                .stream()
                                .map(tag -> tag.getName())
                                .toList()
                )
                .build();
    }

    private AccommodationCandidate toAccommodationCandidate(
            Accommodation accommodation) {

        return AccommodationCandidate.builder()
                .id(accommodation.getId())
                .name(accommodation.getName())
                .description(accommodation.getDescription())
                .type(accommodation.getType())
                .pricePerNight(accommodation.getPricePerNight())
                .rating(accommodation.getRating())
                .build();
    }

    private RestaurantCandidate toRestaurantCandidate(
            Restaurant restaurant) {

        return RestaurantCandidate.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .description(restaurant.getDescription())
                .cuisine(restaurant.getCuisine())
                .vegetarian(restaurant.getVegetarian())
                .rating(restaurant.getRating())
                .priceRange(restaurant.getPriceRange())
                .build();
    }

    private GuideCandidate toGuideCandidate(Guide guide) {

        return GuideCandidate.builder()
                .id(guide.getId())
                .name(guide.getName())
                .bio(guide.getBio())
                .languages(new HashSet<>(guide.getLanguages()))
                .yearsOfExperience(guide.getYearsOfExperience())
                .pricePerDay(guide.getPricePerDay())
                .rating(guide.getRating())
                .available(guide.getAvailable())
                .providesTransport(guide.getProvidesTransport())
                .build();
    }

    private TransportCandidate toTransportCandidate(
            Transport transport) {

        return TransportCandidate.builder()
                .id(transport.getId())
                .type(transport.getType())
                .providerName(transport.getProviderName())
                .pickupLocation(transport.getPickupLocation())
                .dropLocation(transport.getDropLocation())
                .estimatedDuration(transport.getEstimatedDuration())
                .estimatedFare(transport.getEstimatedFare())
                .available(transport.getAvailable())
                .build();
    }

    private FestivalCandidate toFestivalCandidate(
            FestivalOccurrence occurrence) {

        return FestivalCandidate.builder()
                .occurrenceId(occurrence.getId())
                .festivalId(occurrence.getFestival().getId())
                .festivalName(occurrence.getFestival().getName())
                .description(occurrence.getFestival().getDescription())
                .category(occurrence.getFestival().getCategory())
                .stateId(occurrence.getState().getId())
                .stateName(occurrence.getState().getName())
                .year(occurrence.getYear())
                .startDate(occurrence.getStartDate())
                .endDate(occurrence.getEndDate())
                .confirmed(occurrence.getConfirmed())
                .notes(occurrence.getNotes())
                .build();
    }
}