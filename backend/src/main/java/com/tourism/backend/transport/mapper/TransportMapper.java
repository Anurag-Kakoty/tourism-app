package com.tourism.backend.transport.mapper;

import com.tourism.backend.destination.entity.Destination;
import com.tourism.backend.transport.dto.TransportRequest;
import com.tourism.backend.transport.dto.TransportResponse;
import com.tourism.backend.transport.entity.Transport;
import org.springframework.stereotype.Component;

@Component
public class TransportMapper {

    public Transport toEntity(
            TransportRequest request,
            Destination destination) {

        Transport transport = new Transport();

        transport.setType(request.getType());
        transport.setProviderName(request.getProviderName());
        transport.setPickupLocation(request.getPickupLocation());
        transport.setDropLocation(request.getDropLocation());
        transport.setEstimatedDuration(request.getEstimatedDuration());
        transport.setEstimatedFare(request.getEstimatedFare());
        transport.setContactNumber(request.getContactNumber());
        transport.setWebsite(request.getWebsite());
        transport.setBookingUrl(request.getBookingUrl());
        transport.setAvailable(request.getAvailable());
        transport.setDestination(destination);

        return transport;
    }

    public TransportResponse toResponse(Transport transport) {

        TransportResponse response = new TransportResponse();

        response.setId(transport.getId());
        response.setType(transport.getType());
        response.setProviderName(transport.getProviderName());
        response.setPickupLocation(transport.getPickupLocation());
        response.setDropLocation(transport.getDropLocation());
        response.setEstimatedDuration(transport.getEstimatedDuration());
        response.setEstimatedFare(transport.getEstimatedFare());
        response.setContactNumber(transport.getContactNumber());
        response.setWebsite(transport.getWebsite());
        response.setBookingUrl(transport.getBookingUrl());
        response.setAvailable(transport.getAvailable());

        response.setDestinationId(transport.getDestination().getId());
        response.setDestinationName(transport.getDestination().getName());

        response.setStateId(transport.getDestination().getState().getId());
        response.setStateName(transport.getDestination().getState().getName());

        return response;
    }
}