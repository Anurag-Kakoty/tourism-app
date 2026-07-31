package com.tourism.backend.transport.service;

import com.tourism.backend.transport.dto.TransportRequest;
import com.tourism.backend.transport.dto.TransportResponse;
import com.tourism.backend.transport.entity.TransportType;

import java.util.List;

public interface TransportService {

    TransportResponse createTransport(TransportRequest request);

    TransportResponse updateTransport(Long id, TransportRequest request);

    TransportResponse getTransportById(Long id);

    List<TransportResponse> getAllTransportOptions();

    List<TransportResponse> getTransportByDestination(Long destinationId);

    List<TransportResponse> getTransportByType(TransportType type);

    List<TransportResponse> getTransportByAvailability(Boolean available);

    void deleteTransport(Long id);
}