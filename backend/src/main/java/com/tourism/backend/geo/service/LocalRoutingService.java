package com.tourism.backend.geo.service;

import com.tourism.backend.geo.model.GeoPoint;
import com.tourism.backend.geo.model.RouteSegment;
import org.springframework.stereotype.Service;

@Service
public class LocalRoutingService implements RoutingService {

    private static final double EARTH_RADIUS_KM = 6371.0;

    @Override
    public double calculateDistanceKm(
            GeoPoint origin,
            GeoPoint destination
    ) {
        validatePoint(origin);
        validatePoint(destination);

        double latitudeDifference = Math.toRadians(
                destination.getLatitude() - origin.getLatitude()
        );

        double longitudeDifference = Math.toRadians(
                destination.getLongitude() - origin.getLongitude()
        );

        double originLatitude = Math.toRadians(
                origin.getLatitude()
        );

        double destinationLatitude = Math.toRadians(
                destination.getLatitude()
        );

        double a =
                Math.sin(latitudeDifference / 2)
                        * Math.sin(latitudeDifference / 2)
                        + Math.cos(originLatitude)
                        * Math.cos(destinationLatitude)
                        * Math.sin(longitudeDifference / 2)
                        * Math.sin(longitudeDifference / 2);

        double centralAngle = 2 * Math.atan2(
                Math.sqrt(a),
                Math.sqrt(1 - a)
        );

        return EARTH_RADIUS_KM * centralAngle;
    }

    @Override
    public RouteSegment calculateRoute(
            GeoPoint origin,
            GeoPoint destination
    ) {
        double distanceKm = calculateDistanceKm(
                origin,
                destination
        );

        return new RouteSegment(
                origin,
                destination,
                distanceKm
        );
    }

    private void validatePoint(GeoPoint point) {
        if (point == null) {
            throw new IllegalArgumentException(
                    "Geographic point cannot be null."
            );
        }

        if (point.getLatitude() < -90
                || point.getLatitude() > 90) {
            throw new IllegalArgumentException(
                    "Latitude must be between -90 and 90."
            );
        }

        if (point.getLongitude() < -180
                || point.getLongitude() > 180) {
            throw new IllegalArgumentException(
                    "Longitude must be between -180 and 180."
            );
        }
    }
}