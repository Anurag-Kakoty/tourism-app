package com.tourism.backend.ai.validator;

import com.tourism.backend.ai.client.AiGeneratedItinerary;
import com.tourism.backend.ai.client.AiModelClient;
import com.tourism.backend.ai.dto.AiCandidateContext;
import com.tourism.backend.ai.dto.AiItineraryRequest;
import com.tourism.backend.ai.dto.AiItineraryResponse;
import com.tourism.backend.ai.dto.RecommendedCandidateContext;
import com.tourism.backend.ai.dto.candidate.AttractionCandidate;
import com.tourism.backend.ai.dto.candidate.RestaurantCandidate;
import com.tourism.backend.ai.recommendation.RecommendationService;
import com.tourism.backend.ai.service.AiItineraryServiceImpl;
import com.tourism.backend.ai.service.CandidateService;
import com.tourism.backend.attraction.util.AttractionScheduleValidator;
import com.tourism.backend.itinerary.entity.ActivityType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiItineraryScheduleResilienceTest {

    @Mock
    private CandidateService candidateService;

    @Mock
    private RecommendationService recommendationService;

    @Mock
    private AiModelClient aiModelClient;

    private AiItineraryServiceImpl aiItineraryService;

    private final LocalDate testStartDate = LocalDate.of(2026, 10, 12); // Monday
    private final LocalDate testEndDate = LocalDate.of(2026, 10, 14);   // Wednesday

    private AttractionCandidate templeCandidate;
    private AttractionCandidate museumCandidate;
    private AttractionCandidate overnightNightclubCandidate;
    private AttractionCandidate roundTheClockParkCandidate;
    private AttractionCandidate unknownHoursMonumentCandidate;
    private RestaurantCandidate cafeCandidate;

    private RecommendedCandidateContext testCandidateContext;

    @BeforeEach
    void setUp() {
        aiItineraryService = new AiItineraryServiceImpl(
                candidateService,
                recommendationService,
                aiModelClient
        );

        // Temple: Open 09:00 - 18:00, Closed Mondays
        templeCandidate = AttractionCandidate.builder()
                .id(1L)
                .name("Meenakshi Temple")
                .closedDays(Set.of(DayOfWeek.MONDAY))
                .openingTime(LocalTime.of(9, 0))
                .closingTime(LocalTime.of(18, 0))
                .build();

        // Museum: Open 09:00 - 17:00, Closed Tuesdays
        museumCandidate = AttractionCandidate.builder()
                .id(2L)
                .name("City Museum")
                .closedDays(Set.of(DayOfWeek.TUESDAY))
                .openingTime(LocalTime.of(9, 0))
                .closingTime(LocalTime.of(17, 0))
                .build();

        // Overnight venue: Open 20:00 - 04:00, No closed days
        overnightNightclubCandidate = AttractionCandidate.builder()
                .id(3L)
                .name("Night Safari Lounge")
                .closedDays(Set.of())
                .openingTime(LocalTime.of(20, 0))
                .closingTime(LocalTime.of(4, 0))
                .build();

        // 24-hour venue: Open 00:00 - 23:59:59
        roundTheClockParkCandidate = AttractionCandidate.builder()
                .id(4L)
                .name("Public Beach Park")
                .closedDays(Set.of())
                .openingTime(LocalTime.of(0, 0))
                .closingTime(LocalTime.of(23, 59, 59))
                .build();

        // Unknown hours attraction: null hours
        unknownHoursMonumentCandidate = AttractionCandidate.builder()
                .id(5L)
                .name("Historic Clock Tower")
                .closedDays(Set.of())
                .openingTime(null)
                .closingTime(null)
                .build();

        // Cafe: Open 08:00 - 22:00, No closed days
        cafeCandidate = RestaurantCandidate.builder()
                .id(10L)
                .name("Corner Bistro")
                .closedDays(Set.of())
                .openingTime(LocalTime.of(8, 0))
                .closingTime(LocalTime.of(22, 0))
                .build();

        testCandidateContext = RecommendedCandidateContext.builder()
                .attractions(List.of(
                        templeCandidate,
                        museumCandidate,
                        overnightNightclubCandidate,
                        roundTheClockParkCandidate,
                        unknownHoursMonumentCandidate
                ))
                .restaurants(List.of(cafeCandidate))
                .accommodations(List.of())
                .guides(List.of())
                .transport(List.of())
                .festivals(List.of())
                .build();
    }

    private AiItineraryRequest createRequest() {
        AiItineraryRequest req = new AiItineraryRequest();
        req.setStartDate(testStartDate);
        req.setEndDate(testEndDate);
        req.setNumberOfTravelers(2);
        req.setBudget(BigDecimal.valueOf(10000));
        req.setDestinationId(1L);
        return req;
    }

    @Test
    @DisplayName("1. Valid itinerary within operating hours passes without violations or retry")
    void testValidItinerary_withinOperatingHours_passes() {
        AiItineraryRequest request = createRequest();

        // Day 2 is Tuesday (2026-10-13): Temple is OPEN (closed only Monday)
        AiGeneratedItinerary itinerary = AiGeneratedItinerary.builder()
                .title("Trip to Madurai")
                .description("Sample valid trip")
                .startDate(testStartDate)
                .endDate(testEndDate)
                .numberOfTravelers(2)
                .estimatedBudget(BigDecimal.valueOf(5000))
                .destinationId(1L)
                .items(List.of(
                        AiGeneratedItinerary.GeneratedItem.builder()
                                .dayNumber(2)
                                .activityOrder(1)
                                .time(LocalTime.of(10, 0))
                                .activityType(ActivityType.ATTRACTION)
                                .referenceId(1L)
                                .notes("Morning visit")
                                .build()
                ))
                .build();

        ItineraryScheduleValidationResult result =
                AiItineraryScheduleValidator.validateAndCorrect(request, itinerary, testCandidateContext);

        assertThat(result.valid()).isTrue();
        assertThat(result.violations()).isEmpty();
        assertThat(result.hasAutoCorrections()).isFalse();
    }

    @Test
    @DisplayName("2. Minor early arrival (<= 60 mins) on open day is auto-corrected to opening time")
    void testMinorEarlyArrival_autoCorrectsToOpeningTime() {
        AiItineraryRequest request = createRequest();

        // Day 2 (Tuesday): Temple opens at 09:00. Arriving at 08:30 (30 mins early <= 60 mins).
        AiGeneratedItinerary itinerary = AiGeneratedItinerary.builder()
                .title("Trip to Madurai")
                .description("Sample trip")
                .startDate(testStartDate)
                .endDate(testEndDate)
                .numberOfTravelers(2)
                .estimatedBudget(BigDecimal.valueOf(5000))
                .destinationId(1L)
                .items(List.of(
                        AiGeneratedItinerary.GeneratedItem.builder()
                                .dayNumber(2)
                                .activityOrder(1)
                                .time(LocalTime.of(8, 30))
                                .activityType(ActivityType.ATTRACTION)
                                .referenceId(1L)
                                .notes("Slightly early arrival")
                                .build()
                ))
                .build();

        ItineraryScheduleValidationResult result =
                AiItineraryScheduleValidator.validateAndCorrect(request, itinerary, testCandidateContext);

        assertThat(result.valid()).isTrue();
        assertThat(result.hasAutoCorrections()).isTrue();
        assertThat(result.itinerary().getItems().get(0).getTime()).isEqualTo(LocalTime.of(9, 0));
        assertThat(result.violations()).hasSize(1);
        assertThat(result.violations().get(0).autoCorrected()).isTrue();
        assertThat(result.violations().get(0).type()).isEqualTo(ScheduleViolationType.OUTSIDE_OPERATING_HOURS);
    }

    @Test
    @DisplayName("3. Major early arrival (> 60 mins) is not auto-corrected and flags violation")
    void testMajorEarlyArrival_cannotBeCorrected() {
        AiItineraryRequest request = createRequest();

        // Day 2 (Tuesday): Temple opens at 09:00. Arriving at 07:00 (120 mins early > 60 mins).
        AiGeneratedItinerary itinerary = AiGeneratedItinerary.builder()
                .title("Trip to Madurai")
                .description("Sample trip")
                .startDate(testStartDate)
                .endDate(testEndDate)
                .numberOfTravelers(2)
                .estimatedBudget(BigDecimal.valueOf(5000))
                .destinationId(1L)
                .items(List.of(
                        AiGeneratedItinerary.GeneratedItem.builder()
                                .dayNumber(2)
                                .activityOrder(1)
                                .time(LocalTime.of(7, 0))
                                .activityType(ActivityType.ATTRACTION)
                                .referenceId(1L)
                                .notes("Way too early")
                                .build()
                ))
                .build();

        ItineraryScheduleValidationResult result =
                AiItineraryScheduleValidator.validateAndCorrect(request, itinerary, testCandidateContext);

        assertThat(result.valid()).isFalse();
        assertThat(result.violations()).hasSize(1);
        assertThat(result.violations().get(0).autoCorrected()).isFalse();
        assertThat(result.violations().get(0).type()).isEqualTo(ScheduleViolationType.OUTSIDE_OPERATING_HOURS);
        assertThat(result.getFeedbackSummary()).contains("outside operating hours");
    }

    @Test
    @DisplayName("4. Late arrival after closing time is never auto-corrected")
    void testLateArrival_cannotBeCorrected() {
        AiItineraryRequest request = createRequest();

        // Day 2 (Tuesday): Temple closes at 18:00. Scheduled at 18:30.
        AiGeneratedItinerary itinerary = AiGeneratedItinerary.builder()
                .title("Trip to Madurai")
                .description("Sample trip")
                .startDate(testStartDate)
                .endDate(testEndDate)
                .numberOfTravelers(2)
                .estimatedBudget(BigDecimal.valueOf(5000))
                .destinationId(1L)
                .items(List.of(
                        AiGeneratedItinerary.GeneratedItem.builder()
                                .dayNumber(2)
                                .activityOrder(1)
                                .time(LocalTime.of(18, 30))
                                .activityType(ActivityType.ATTRACTION)
                                .referenceId(1L)
                                .notes("Late visit")
                                .build()
                ))
                .build();

        ItineraryScheduleValidationResult result =
                AiItineraryScheduleValidator.validateAndCorrect(request, itinerary, testCandidateContext);

        assertThat(result.valid()).isFalse();
        assertThat(result.violations()).hasSize(1);
        assertThat(result.violations().get(0).autoCorrected()).isFalse();
        assertThat(result.violations().get(0).type()).isEqualTo(ScheduleViolationType.OUTSIDE_OPERATING_HOURS);
    }

    @Test
    @DisplayName("5. Closed day scheduling flags CLOSED_DAY violation and is never auto-corrected")
    void testClosedDay_cannotBeCorrected() {
        AiItineraryRequest request = createRequest();

        // Day 1 is Monday (2026-10-12): Temple is CLOSED on Mondays.
        AiGeneratedItinerary itinerary = AiGeneratedItinerary.builder()
                .title("Trip to Madurai")
                .description("Sample trip")
                .startDate(testStartDate)
                .endDate(testEndDate)
                .numberOfTravelers(2)
                .estimatedBudget(BigDecimal.valueOf(5000))
                .destinationId(1L)
                .items(List.of(
                        AiGeneratedItinerary.GeneratedItem.builder()
                                .dayNumber(1)
                                .activityOrder(1)
                                .time(LocalTime.of(10, 0))
                                .activityType(ActivityType.ATTRACTION)
                                .referenceId(1L)
                                .notes("Visiting on closed day")
                                .build()
                ))
                .build();

        ItineraryScheduleValidationResult result =
                AiItineraryScheduleValidator.validateAndCorrect(request, itinerary, testCandidateContext);

        assertThat(result.valid()).isFalse();
        assertThat(result.violations()).hasSize(1);
        assertThat(result.violations().get(0).autoCorrected()).isFalse();
        assertThat(result.violations().get(0).type()).isEqualTo(ScheduleViolationType.CLOSED_DAY);
        assertThat(result.getFeedbackSummary()).contains("is closed on MONDAYs");
    }

    @Test
    @DisplayName("6. Minor early arrival is NOT corrected if shifting would collide with another item")
    void testMinorEarlyArrival_whenCollidesWithExistingActivity_notCorrected() {
        AiItineraryRequest request = createRequest();

        // Day 2 (Tuesday): Temple opens at 09:00.
        // Item 1: Cafe already at 09:00
        // Item 2: Temple at 08:30 (early). Shifting item 2 to 09:00 would cause collision with item 1!
        AiGeneratedItinerary itinerary = AiGeneratedItinerary.builder()
                .title("Trip to Madurai")
                .description("Sample trip")
                .startDate(testStartDate)
                .endDate(testEndDate)
                .numberOfTravelers(2)
                .estimatedBudget(BigDecimal.valueOf(5000))
                .destinationId(1L)
                .items(List.of(
                        AiGeneratedItinerary.GeneratedItem.builder()
                                .dayNumber(2)
                                .activityOrder(1)
                                .time(LocalTime.of(9, 0))
                                .activityType(ActivityType.RESTAURANT)
                                .referenceId(10L)
                                .notes("Breakfast")
                                .build(),
                        AiGeneratedItinerary.GeneratedItem.builder()
                                .dayNumber(2)
                                .activityOrder(2)
                                .time(LocalTime.of(8, 30))
                                .activityType(ActivityType.ATTRACTION)
                                .referenceId(1L)
                                .notes("Early temple visit")
                                .build()
                ))
                .build();

        ItineraryScheduleValidationResult result =
                AiItineraryScheduleValidator.validateAndCorrect(request, itinerary, testCandidateContext);

        assertThat(result.valid()).isFalse();
        assertThat(result.violations()).anyMatch(v -> v.type() == ScheduleViolationType.OUTSIDE_OPERATING_HOURS && !v.autoCorrected());
    }

    @Test
    @DisplayName("7. Duplicate start times on same day flag TIME_COLLISION violation")
    void testDuplicateStartTimes_flagsTimeCollision() {
        AiItineraryRequest request = createRequest();

        // Day 2 (Tuesday): Both items start at 10:00
        AiGeneratedItinerary itinerary = AiGeneratedItinerary.builder()
                .title("Trip to Madurai")
                .description("Sample trip")
                .startDate(testStartDate)
                .endDate(testEndDate)
                .numberOfTravelers(2)
                .estimatedBudget(BigDecimal.valueOf(5000))
                .destinationId(1L)
                .items(List.of(
                        AiGeneratedItinerary.GeneratedItem.builder()
                                .dayNumber(2)
                                .activityOrder(1)
                                .time(LocalTime.of(10, 0))
                                .activityType(ActivityType.ATTRACTION)
                                .referenceId(1L)
                                .notes("Temple")
                                .build(),
                        AiGeneratedItinerary.GeneratedItem.builder()
                                .dayNumber(2)
                                .activityOrder(2)
                                .time(LocalTime.of(10, 0))
                                .activityType(ActivityType.RESTAURANT)
                                .referenceId(10L)
                                .notes("Cafe at exact same time")
                                .build()
                ))
                .build();

        ItineraryScheduleValidationResult result =
                AiItineraryScheduleValidator.validateAndCorrect(request, itinerary, testCandidateContext);

        assertThat(result.valid()).isFalse();
        assertThat(result.violations()).anyMatch(v -> v.type() == ScheduleViolationType.TIME_COLLISION);
        assertThat(result.getFeedbackSummary()).contains("Multiple activities have the exact same start time (10:00)");
    }

    @Test
    @DisplayName("8. 24-hour attraction accepts any time of day")
    void test24HourAttraction_acceptsAnyTime() {
        AiItineraryRequest request = createRequest();

        // Round the clock park (00:00 - 23:59:59) scheduled at 02:00
        AiGeneratedItinerary itinerary = AiGeneratedItinerary.builder()
                .title("Trip to Madurai")
                .description("Sample trip")
                .startDate(testStartDate)
                .endDate(testEndDate)
                .numberOfTravelers(2)
                .estimatedBudget(BigDecimal.valueOf(5000))
                .destinationId(1L)
                .items(List.of(
                        AiGeneratedItinerary.GeneratedItem.builder()
                                .dayNumber(1)
                                .activityOrder(1)
                                .time(LocalTime.of(2, 0))
                                .activityType(ActivityType.ATTRACTION)
                                .referenceId(4L)
                                .notes("Late night park visit")
                                .build()
                ))
                .build();

        ItineraryScheduleValidationResult result =
                AiItineraryScheduleValidator.validateAndCorrect(request, itinerary, testCandidateContext);

        assertThat(result.valid()).isTrue();
        assertThat(result.violations()).isEmpty();
    }

    @Test
    @DisplayName("9. Overnight operating hours venue operates past midnight")
    void testOvernightOperatingHours_venueOperatesPastMidnight() {
        AiItineraryRequest request = createRequest();

        // Nightclub (20:00 - 04:00) scheduled at 22:00 (before midnight) and 02:00 (after midnight)
        AiGeneratedItinerary itineraryEvening = AiGeneratedItinerary.builder()
                .title("Trip to Madurai")
                .description("Sample trip")
                .startDate(testStartDate)
                .endDate(testEndDate)
                .numberOfTravelers(2)
                .estimatedBudget(BigDecimal.valueOf(5000))
                .destinationId(1L)
                .items(List.of(
                        AiGeneratedItinerary.GeneratedItem.builder()
                                .dayNumber(1)
                                .activityOrder(1)
                                .time(LocalTime.of(22, 0))
                                .activityType(ActivityType.ATTRACTION)
                                .referenceId(3L)
                                .notes("Evening lounge")
                                .build()
                ))
                .build();

        ItineraryScheduleValidationResult resultEvening =
                AiItineraryScheduleValidator.validateAndCorrect(request, itineraryEvening, testCandidateContext);
        assertThat(resultEvening.valid()).isTrue();

        AiGeneratedItinerary itineraryNight = AiGeneratedItinerary.builder()
                .title("Trip to Madurai")
                .description("Sample trip")
                .startDate(testStartDate)
                .endDate(testEndDate)
                .numberOfTravelers(2)
                .estimatedBudget(BigDecimal.valueOf(5000))
                .destinationId(1L)
                .items(List.of(
                        AiGeneratedItinerary.GeneratedItem.builder()
                                .dayNumber(1)
                                .activityOrder(1)
                                .time(LocalTime.of(2, 0))
                                .activityType(ActivityType.ATTRACTION)
                                .referenceId(3L)
                                .notes("Post-midnight lounge")
                                .build()
                ))
                .build();

        ItineraryScheduleValidationResult resultNight =
                AiItineraryScheduleValidator.validateAndCorrect(request, itineraryNight, testCandidateContext);
        assertThat(resultNight.valid()).isTrue();

        // Midday visit (12:00) should be invalid
        AiGeneratedItinerary itineraryMidday = AiGeneratedItinerary.builder()
                .title("Trip to Madurai")
                .description("Sample trip")
                .startDate(testStartDate)
                .endDate(testEndDate)
                .numberOfTravelers(2)
                .estimatedBudget(BigDecimal.valueOf(5000))
                .destinationId(1L)
                .items(List.of(
                        AiGeneratedItinerary.GeneratedItem.builder()
                                .dayNumber(1)
                                .activityOrder(1)
                                .time(LocalTime.of(12, 0))
                                .activityType(ActivityType.ATTRACTION)
                                .referenceId(3L)
                                .notes("Midday visit to nightclub")
                                .build()
                ))
                .build();

        ItineraryScheduleValidationResult resultMidday =
                AiItineraryScheduleValidator.validateAndCorrect(request, itineraryMidday, testCandidateContext);
        assertThat(resultMidday.valid()).isFalse();
        assertThat(resultMidday.violations()).anyMatch(v -> v.type() == ScheduleViolationType.OUTSIDE_OPERATING_HOURS);
    }

    @Test
    @DisplayName("10. Unknown operating hours (null) are treated gracefully without violations")
    void testUnknownOperatingHours_treatedGracefully() {
        AiItineraryRequest request = createRequest();

        // Clock tower has null openingTime and null closingTime
        AiGeneratedItinerary itinerary = AiGeneratedItinerary.builder()
                .title("Trip to Madurai")
                .description("Sample trip")
                .startDate(testStartDate)
                .endDate(testEndDate)
                .numberOfTravelers(2)
                .estimatedBudget(BigDecimal.valueOf(5000))
                .destinationId(1L)
                .items(List.of(
                        AiGeneratedItinerary.GeneratedItem.builder()
                                .dayNumber(1)
                                .activityOrder(1)
                                .time(LocalTime.of(14, 0))
                                .activityType(ActivityType.ATTRACTION)
                                .referenceId(5L)
                                .notes("Clock tower")
                                .build()
                ))
                .build();

        ItineraryScheduleValidationResult result =
                AiItineraryScheduleValidator.validateAndCorrect(request, itinerary, testCandidateContext);

        assertThat(result.valid()).isTrue();
        assertThat(result.violations()).isEmpty();
    }

    @Test
    @DisplayName("11. AiItineraryServiceImpl: Valid initial draft succeeds without retry")
    void testService_validDraft_noRetry() {
        AiItineraryRequest request = createRequest();
        AiCandidateContext rawCandidates = AiCandidateContext.builder().build();

        when(candidateService.getCandidates(request)).thenReturn(rawCandidates);
        when(recommendationService.recommend(request, rawCandidates)).thenReturn(testCandidateContext);

        // Day 2 (Tuesday): Temple is open at 10:00
        AiGeneratedItinerary validItinerary = AiGeneratedItinerary.builder()
                .title("Trip")
                .description("Desc")
                .startDate(testStartDate)
                .endDate(testEndDate)
                .numberOfTravelers(2)
                .estimatedBudget(BigDecimal.valueOf(5000))
                .destinationId(1L)
                .items(List.of(
                        AiGeneratedItinerary.GeneratedItem.builder()
                                .dayNumber(2)
                                .activityOrder(1)
                                .time(LocalTime.of(10, 0))
                                .activityType(ActivityType.ATTRACTION)
                                .referenceId(1L)
                                .notes("Temple")
                                .build()
                ))
                .build();

        when(aiModelClient.generateItinerary(request, testCandidateContext)).thenReturn(validItinerary);

        AiItineraryResponse response = aiItineraryService.generateItinerary(request);

        assertThat(response).isNotNull();
        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getTime()).isEqualTo(LocalTime.of(10, 0));
        verify(aiModelClient, times(1)).generateItinerary(request, testCandidateContext);
        verify(aiModelClient, never()).generateItinerary(eq(request), eq(testCandidateContext), any());
    }

    @Test
    @DisplayName("12. AiItineraryServiceImpl: Minor early arrival auto-corrected in response without retry")
    void testService_minorEarlyArrival_autoCorrectedInResponse_noRetry() {
        AiItineraryRequest request = createRequest();
        AiCandidateContext rawCandidates = AiCandidateContext.builder().build();

        when(candidateService.getCandidates(request)).thenReturn(rawCandidates);
        when(recommendationService.recommend(request, rawCandidates)).thenReturn(testCandidateContext);

        // Day 2 (Tuesday): Temple opens at 09:00. AI generated 08:30.
        AiGeneratedItinerary draftItinerary = AiGeneratedItinerary.builder()
                .title("Trip")
                .description("Desc")
                .startDate(testStartDate)
                .endDate(testEndDate)
                .numberOfTravelers(2)
                .estimatedBudget(BigDecimal.valueOf(5000))
                .destinationId(1L)
                .items(List.of(
                        AiGeneratedItinerary.GeneratedItem.builder()
                                .dayNumber(2)
                                .activityOrder(1)
                                .time(LocalTime.of(8, 30))
                                .activityType(ActivityType.ATTRACTION)
                                .referenceId(1L)
                                .notes("Temple")
                                .build()
                ))
                .build();

        when(aiModelClient.generateItinerary(request, testCandidateContext)).thenReturn(draftItinerary);

        AiItineraryResponse response = aiItineraryService.generateItinerary(request);

        assertThat(response).isNotNull();
        assertThat(response.getItems()).hasSize(1);
        // Time was safely corrected to 09:00:00
        assertThat(response.getItems().get(0).getTime()).isEqualTo(LocalTime.of(9, 0));
        verify(aiModelClient, times(1)).generateItinerary(request, testCandidateContext);
        verify(aiModelClient, never()).generateItinerary(eq(request), eq(testCandidateContext), any());
    }

    @Test
    @DisplayName("13. AiItineraryServiceImpl: Closed day violation triggers 1 retry with feedback; retry succeeds")
    void testService_closedDay_triggersRetryWithFeedback_retrySucceeds() {
        AiItineraryRequest request = createRequest();
        AiCandidateContext rawCandidates = AiCandidateContext.builder().build();

        when(candidateService.getCandidates(request)).thenReturn(rawCandidates);
        when(recommendationService.recommend(request, rawCandidates)).thenReturn(testCandidateContext);

        // Draft 1: Temple on Day 1 (Monday) -> CLOSED!
        AiGeneratedItinerary draft1 = AiGeneratedItinerary.builder()
                .title("Trip")
                .description("Desc")
                .startDate(testStartDate)
                .endDate(testEndDate)
                .numberOfTravelers(2)
                .estimatedBudget(BigDecimal.valueOf(5000))
                .destinationId(1L)
                .items(List.of(
                        AiGeneratedItinerary.GeneratedItem.builder()
                                .dayNumber(1)
                                .activityOrder(1)
                                .time(LocalTime.of(10, 0))
                                .activityType(ActivityType.ATTRACTION)
                                .referenceId(1L)
                                .notes("Temple on Monday")
                                .build()
                ))
                .build();

        // Draft 2: Temple on Day 2 (Tuesday) -> OPEN!
        AiGeneratedItinerary draft2 = AiGeneratedItinerary.builder()
                .title("Trip")
                .description("Desc")
                .startDate(testStartDate)
                .endDate(testEndDate)
                .numberOfTravelers(2)
                .estimatedBudget(BigDecimal.valueOf(5000))
                .destinationId(1L)
                .items(List.of(
                        AiGeneratedItinerary.GeneratedItem.builder()
                                .dayNumber(2)
                                .activityOrder(1)
                                .time(LocalTime.of(10, 0))
                                .activityType(ActivityType.ATTRACTION)
                                .referenceId(1L)
                                .notes("Temple on Tuesday")
                                .build()
                ))
                .build();

        when(aiModelClient.generateItinerary(request, testCandidateContext)).thenReturn(draft1);
        when(aiModelClient.generateItinerary(eq(request), eq(testCandidateContext), any())).thenReturn(draft2);

        AiItineraryResponse response = aiItineraryService.generateItinerary(request);

        assertThat(response).isNotNull();
        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getDayNumber()).isEqualTo(2);

        // Verify exactly 1 initial call and exactly 1 retry call with feedback
        verify(aiModelClient, times(1)).generateItinerary(request, testCandidateContext);
        verify(aiModelClient, times(1)).generateItinerary(eq(request), eq(testCandidateContext), any());
    }

    @Test
    @DisplayName("14. AiItineraryServiceImpl: Bounded recovery exhausted (retry fails) throws IllegalArgumentException")
    void testService_retryFails_throwsIllegalArgumentException() {
        AiItineraryRequest request = createRequest();
        AiCandidateContext rawCandidates = AiCandidateContext.builder().build();

        when(candidateService.getCandidates(request)).thenReturn(rawCandidates);
        when(recommendationService.recommend(request, rawCandidates)).thenReturn(testCandidateContext);

        // Draft 1 has closed day violation
        AiGeneratedItinerary draft1 = AiGeneratedItinerary.builder()
                .title("Trip")
                .description("Desc")
                .startDate(testStartDate)
                .endDate(testEndDate)
                .numberOfTravelers(2)
                .estimatedBudget(BigDecimal.valueOf(5000))
                .destinationId(1L)
                .items(List.of(
                        AiGeneratedItinerary.GeneratedItem.builder()
                                .dayNumber(1)
                                .activityOrder(1)
                                .time(LocalTime.of(10, 0))
                                .activityType(ActivityType.ATTRACTION)
                                .referenceId(1L)
                                .notes("Temple on Monday")
                                .build()
                ))
                .build();

        // Draft 2 still has closed day violation
        AiGeneratedItinerary draft2 = AiGeneratedItinerary.builder()
                .title("Trip")
                .description("Desc")
                .startDate(testStartDate)
                .endDate(testEndDate)
                .numberOfTravelers(2)
                .estimatedBudget(BigDecimal.valueOf(5000))
                .destinationId(1L)
                .items(List.of(
                        AiGeneratedItinerary.GeneratedItem.builder()
                                .dayNumber(1)
                                .activityOrder(1)
                                .time(LocalTime.of(11, 0))
                                .activityType(ActivityType.ATTRACTION)
                                .referenceId(1L)
                                .notes("Temple still on Monday")
                                .build()
                ))
                .build();

        when(aiModelClient.generateItinerary(request, testCandidateContext)).thenReturn(draft1);
        when(aiModelClient.generateItinerary(eq(request), eq(testCandidateContext), any())).thenReturn(draft2);

        assertThatThrownBy(() -> aiItineraryService.generateItinerary(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("AI itinerary schedule validation failed")
                .hasMessageContaining("is closed on MONDAYs");

        // Verify bound: only 1 retry was attempted
        verify(aiModelClient, times(1)).generateItinerary(request, testCandidateContext);
        verify(aiModelClient, times(1)).generateItinerary(eq(request), eq(testCandidateContext), any());
    }

    @Test
    @DisplayName("15. Preservation check: Strict manual validation still strictly rejects early arrival without auto-correction")
    void testStrictManualValidation_remainsUnchanged() {
        // Direct call to AttractionScheduleValidator.validateSchedule with 08:30 (opens at 09:00)
        // Must throw IllegalArgumentException (no relaxation for manual itineraries)
        LocalDate openDate = LocalDate.of(2026, 10, 13); // Tuesday

        assertThatThrownBy(() -> AttractionScheduleValidator.validateSchedule(
                "Meenakshi Temple",
                openDate,
                LocalTime.of(8, 30),
                Set.of(DayOfWeek.MONDAY),
                LocalTime.of(9, 0),
                LocalTime.of(18, 0)
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("outside operating hours");
    }

    @Test
    @DisplayName("16. Regression: Attraction early arrival with subsequent activity scheduled earlier than opening time prevents inversion")
    void testAttraction_earlyArrivalWithSubsequentActivity_inversionPrevented() {
        AiItineraryRequest request = createRequest();

        // Day 2 (Tuesday): Temple opens at 09:00.
        // Item 1: Temple at 08:30 (order 1)
        // Item 2: Bistro at 08:45 (order 2)
        // Shifting item 1 to 09:00 would place order 1 (09:00) AFTER order 2 (08:45) -> Inversion!
        AiGeneratedItinerary itinerary = AiGeneratedItinerary.builder()
                .title("Trip")
                .description("Desc")
                .startDate(testStartDate)
                .endDate(testEndDate)
                .numberOfTravelers(2)
                .estimatedBudget(BigDecimal.valueOf(5000))
                .destinationId(1L)
                .items(List.of(
                        AiGeneratedItinerary.GeneratedItem.builder()
                                .dayNumber(2)
                                .activityOrder(1)
                                .time(LocalTime.of(8, 30))
                                .activityType(ActivityType.ATTRACTION)
                                .referenceId(1L)
                                .notes("Early temple visit")
                                .build(),
                        AiGeneratedItinerary.GeneratedItem.builder()
                                .dayNumber(2)
                                .activityOrder(2)
                                .time(LocalTime.of(8, 45))
                                .activityType(ActivityType.RESTAURANT)
                                .referenceId(10L)
                                .notes("Subsequent bistro visit")
                                .build()
                ))
                .build();

        ItineraryScheduleValidationResult result =
                AiItineraryScheduleValidator.validateAndCorrect(request, itinerary, testCandidateContext);

        // Auto-correction must be prevented to avoid chronological inversion
        assertThat(result.valid()).isFalse();
        assertThat(result.violations()).anyMatch(v -> v.type() == ScheduleViolationType.OUTSIDE_OPERATING_HOURS && !v.autoCorrected());
    }

    @Test
    @DisplayName("17. Regression: Restaurant early arrival with subsequent activity scheduled earlier than opening time prevents inversion")
    void testRestaurant_earlyArrivalWithSubsequentActivity_inversionPrevented() {
        AiItineraryRequest request = createRequest();

        // Corner Bistro opens at 08:00.
        // Item 1: Corner Bistro at 07:30 (order 1, 30 mins early)
        // Item 2: Clock tower at 07:45 (order 2, unknown hours monument)
        // Shifting item 1 to 08:00 would place order 1 (08:00) AFTER order 2 (07:45) -> Inversion!
        AiGeneratedItinerary itinerary = AiGeneratedItinerary.builder()
                .title("Trip")
                .description("Desc")
                .startDate(testStartDate)
                .endDate(testEndDate)
                .numberOfTravelers(2)
                .estimatedBudget(BigDecimal.valueOf(5000))
                .destinationId(1L)
                .items(List.of(
                        AiGeneratedItinerary.GeneratedItem.builder()
                                .dayNumber(2)
                                .activityOrder(1)
                                .time(LocalTime.of(7, 30))
                                .activityType(ActivityType.RESTAURANT)
                                .referenceId(10L)
                                .notes("Early bistro visit")
                                .build(),
                        AiGeneratedItinerary.GeneratedItem.builder()
                                .dayNumber(2)
                                .activityOrder(2)
                                .time(LocalTime.of(7, 45))
                                .activityType(ActivityType.ATTRACTION)
                                .referenceId(5L)
                                .notes("Clock tower")
                                .build()
                ))
                .build();

        ItineraryScheduleValidationResult result =
                AiItineraryScheduleValidator.validateAndCorrect(request, itinerary, testCandidateContext);

        // Auto-correction must be prevented to avoid chronological inversion
        assertThat(result.valid()).isFalse();
        assertThat(result.violations()).anyMatch(v -> v.type() == ScheduleViolationType.OUTSIDE_OPERATING_HOURS && !v.autoCorrected());
    }

    @Test
    @DisplayName("18. Regression: Early arrival correction that preserves chronological order succeeds and remains valid")
    void testAutoCorrection_preservesChronologicalOrder_valid() {
        AiItineraryRequest request = createRequest();

        // Day 2 (Tuesday): Temple opens at 09:00.
        // Item 1: Temple at 08:30 (order 1) -> Shifting to 09:00
        // Item 2: Bistro at 12:00 (order 2) -> 12:00 is well after 09:00
        AiGeneratedItinerary itinerary = AiGeneratedItinerary.builder()
                .title("Trip")
                .description("Desc")
                .startDate(testStartDate)
                .endDate(testEndDate)
                .numberOfTravelers(2)
                .estimatedBudget(BigDecimal.valueOf(5000))
                .destinationId(1L)
                .items(List.of(
                        AiGeneratedItinerary.GeneratedItem.builder()
                                .dayNumber(2)
                                .activityOrder(1)
                                .time(LocalTime.of(8, 30))
                                .activityType(ActivityType.ATTRACTION)
                                .referenceId(1L)
                                .notes("Temple")
                                .build(),
                        AiGeneratedItinerary.GeneratedItem.builder()
                                .dayNumber(2)
                                .activityOrder(2)
                                .time(LocalTime.of(12, 0))
                                .activityType(ActivityType.RESTAURANT)
                                .referenceId(10L)
                                .notes("Lunch")
                                .build()
                ))
                .build();

        ItineraryScheduleValidationResult result =
                AiItineraryScheduleValidator.validateAndCorrect(request, itinerary, testCandidateContext);

        assertThat(result.valid()).isTrue();
        assertThat(result.hasAutoCorrections()).isTrue();
        assertThat(result.itinerary().getItems().get(0).getTime()).isEqualTo(LocalTime.of(9, 0));
        assertThat(result.itinerary().getItems().get(1).getTime()).isEqualTo(LocalTime.of(12, 0));
    }

    @Test
    @DisplayName("19. Regression: Preceding activity scheduled after opening time makes early arrival correction unsafe")
    void testPrecedingActivity_makesCorrectionUnsafe_inversionPrevented() {
        AiItineraryRequest request = createRequest();

        // Day 2 (Tuesday): Temple opens at 09:00.
        // Item 1: Bistro at 09:30 (order 1)
        // Item 2: Temple at 08:30 (order 2) -> Shifting to 09:00 would place order 2 at 09:00, before order 1 at 09:30 -> Inversion!
        AiGeneratedItinerary itinerary = AiGeneratedItinerary.builder()
                .title("Trip")
                .description("Desc")
                .startDate(testStartDate)
                .endDate(testEndDate)
                .numberOfTravelers(2)
                .estimatedBudget(BigDecimal.valueOf(5000))
                .destinationId(1L)
                .items(List.of(
                        AiGeneratedItinerary.GeneratedItem.builder()
                                .dayNumber(2)
                                .activityOrder(1)
                                .time(LocalTime.of(9, 30))
                                .activityType(ActivityType.RESTAURANT)
                                .referenceId(10L)
                                .notes("Bistro")
                                .build(),
                        AiGeneratedItinerary.GeneratedItem.builder()
                                .dayNumber(2)
                                .activityOrder(2)
                                .time(LocalTime.of(8, 30))
                                .activityType(ActivityType.ATTRACTION)
                                .referenceId(1L)
                                .notes("Temple")
                                .build()
                ))
                .build();

        ItineraryScheduleValidationResult result =
                AiItineraryScheduleValidator.validateAndCorrect(request, itinerary, testCandidateContext);

        assertThat(result.valid()).isFalse();
        assertThat(result.violations()).anyMatch(v -> v.type() == ScheduleViolationType.OUTSIDE_OPERATING_HOURS && !v.autoCorrected());
    }

    @Test
    @DisplayName("20. Regression: Null item in generated activities list is handled safely without NullPointerException")
    void testNullItem_handledSafelyWithoutNPE() {
        AiItineraryRequest request = createRequest();

        List<AiGeneratedItinerary.GeneratedItem> itemsWithNull = new ArrayList<>();
        itemsWithNull.add(null);
        itemsWithNull.add(AiGeneratedItinerary.GeneratedItem.builder()
                .dayNumber(2)
                .activityOrder(1)
                .time(LocalTime.of(10, 0))
                .activityType(ActivityType.ATTRACTION)
                .referenceId(1L)
                .notes("Valid temple visit")
                .build());

        AiGeneratedItinerary itinerary = AiGeneratedItinerary.builder()
                .title("Trip with null item")
                .description("Desc")
                .startDate(testStartDate)
                .endDate(testEndDate)
                .numberOfTravelers(2)
                .estimatedBudget(BigDecimal.valueOf(5000))
                .destinationId(1L)
                .items(itemsWithNull)
                .build();

        ItineraryScheduleValidationResult result =
                AiItineraryScheduleValidator.validateAndCorrect(request, itinerary, testCandidateContext);

        assertThat(result.valid()).isFalse();
        assertThat(result.violations()).anyMatch(v -> v.type() == ScheduleViolationType.INVALID_STRUCTURE
                && v.message().contains("null item"));
    }
}

