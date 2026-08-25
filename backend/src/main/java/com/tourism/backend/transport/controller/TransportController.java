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

        return transportService.updateTransport(
                id,
                request
        );
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
                    Returns transport options using optional combined filters.

                    Available filters:
                    - destinationId
                    - type
                    - available

                    Filters can be combined.

                    Examples:

                    /api/transport?destinationId=1

                    /api/transport?type=BUS

                    /api/transport?available=true

                    /api/transport?destinationId=1&type=BUS

                    /api/transport?destinationId=1&type=BUS&available=true
                    """
    )
    public List<TransportResponse> getTransport(

            @RequestParam(required = false)
            Long destinationId,

            @RequestParam(required = false)
            TransportType type,

            @RequestParam(required = false)
            Boolean available) {

        return transportService.getAllTransportOptions(
                destinationId,
                type,
                available
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete transport option")
    public void deleteTransport(
            @PathVariable Long id) {

        transportService.deleteTransport(id);
    }
}