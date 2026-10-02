package com.tourism.backend.geo.service;

import com.tourism.backend.geo.model.GeoPoint;
import com.tourism.backend.geo.model.RouteSegment;

public interface RoutingService {

    double calculateDistanceKm(
            GeoPoint origin,
            GeoPoint destination
    );

    RouteSegment calculateRoute(
            GeoPoint origin,
            GeoPoint destination
    );
}