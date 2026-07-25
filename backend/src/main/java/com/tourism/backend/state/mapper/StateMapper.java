package com.tourism.backend.state.mapper;

import com.tourism.backend.state.dto.StateRequest;
import com.tourism.backend.state.dto.StateResponse;
import com.tourism.backend.state.entity.State;
import org.springframework.stereotype.Component;

@Component
public class StateMapper {

    public State toEntity(StateRequest request) {

        return State.builder()
                .name(request.getName())
                .capital(request.getCapital())
                .description(request.getDescription())
                .thumbnailUrl(request.getThumbnailUrl())
                .build();
    }

    public StateResponse toResponse(State state) {

        return StateResponse.builder()
                .id(state.getId())
                .name(state.getName())
                .capital(state.getCapital())
                .description(state.getDescription())
                .thumbnailUrl(state.getThumbnailUrl())
                .build();
    }
}