package com.tourism.backend.festivaloccurrence.mapper;

import com.tourism.backend.festival.entity.Festival;
import com.tourism.backend.festivaloccurrence.dto.FestivalOccurrenceRequest;
import com.tourism.backend.festivaloccurrence.dto.FestivalOccurrenceResponse;
import com.tourism.backend.festivaloccurrence.entity.FestivalOccurrence;
import com.tourism.backend.state.entity.State;
import org.springframework.stereotype.Component;

@Component
public class FestivalOccurrenceMapper {

    public FestivalOccurrence toEntity(
            FestivalOccurrenceRequest request,
            Festival festival,
            State state
    ) {

        return FestivalOccurrence.builder()
                .festival(festival)
                .state(state)
                .year(request.getYear())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .confirmed(request.getConfirmed() != null
                        ? request.getConfirmed()
                        : false)
                .notes(request.getNotes())
                .build();
    }

    public void updateEntity(
            FestivalOccurrence occurrence,
            FestivalOccurrenceRequest request,
            Festival festival,
            State state
    ) {

        occurrence.setFestival(festival);
        occurrence.setState(state);
        occurrence.setYear(request.getYear());
        occurrence.setStartDate(request.getStartDate());
        occurrence.setEndDate(request.getEndDate());
        occurrence.setConfirmed(
                request.getConfirmed() != null
                        ? request.getConfirmed()
                        : false
        );
        occurrence.setNotes(request.getNotes());
    }

    public FestivalOccurrenceResponse toResponse(
            FestivalOccurrence occurrence
    ) {

        return FestivalOccurrenceResponse.builder()
                .id(occurrence.getId())

                .festivalId(occurrence.getFestival().getId())
                .festivalName(occurrence.getFestival().getName())

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