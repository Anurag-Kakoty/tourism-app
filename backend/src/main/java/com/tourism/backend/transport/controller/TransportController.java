package com.tourism.backend.transport.controller;

import com.tourism.backend.constants.ApiPaths;
import com.tourism.backend.transport.dto.TransportRequest;
import com.tourism.backend.transport.dto.TransportResponse;
import com.tourism.backend.transport.entity.TransportType;
import com.tourism.backend.transport.service.TransportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.TRANSPORT)
@RequiredArgsConstructor
@Tag(
        name = "Transport",
        description = "Transport Management APIs"
)
public class TransportController {

    private final TransportService transportService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create transport option")
    public TransportResponse createTransport(
            @Valid @RequestBody TransportRequest request) {

        return transportService.createTransport(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update transport option")
    public TransportResponse updateTransport(
            @PathVariable Long id,
            @Valid @RequestBody TransportRequest request) {

        return transportService.updateTransport(id, request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get transport option by ID")
    public TransportResponse getTransportById(
            @PathVariable Long id) {

        return transportService.getTransportById(id);
    }

    @GetMapping
    @Operation(
            summary = "Get transport options",
            description = """
                    Returns all transport options or filters by a single optional parameter.
                    If multiple filters are supplied, only the first applicable filter is used.
                    """
    )
    public List<TransportResponse> getTransport(

            @RequestParam(required = false)
            Long destinationId,

            @RequestParam(required = false)
            TransportType type,

            @RequestParam(required = false)
            Boolean available) {

        if (destinationId != null) {
            return transportService.getTransportByDestination(destinationId);
        }

        if (type != null) {
            return transportService.getTransportByType(type);
        }

        if (available != null) {
            return transportService.getTransportByAvailability(available);
        }

        return transportService.getAllTransportOptions();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete transport option")
    public void deleteTransport(
            @PathVariable Long id) {

        transportService.deleteTransport(id);
    }
}