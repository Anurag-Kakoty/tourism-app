package com.tourism.backend.geo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RouteSegment {

    private GeoPoint origin;
    private GeoPoint destination;
    private double distanceKm;
}