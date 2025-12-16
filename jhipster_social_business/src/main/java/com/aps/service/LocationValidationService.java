package com.aps.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service for validating customer location against delivery radius
 * Business location: 10.79427, 76.53016
 * Delivery radius: 50 km
 */
@Service
public class LocationValidationService {

    private final Logger log = LoggerFactory.getLogger(LocationValidationService.class);

    // Business location coordinates
    private static final double BUSINESS_LAT = 10.7944769;
    private static final double BUSINESS_LON = 76.5306715;
    private static final double DELIVERY_RADIUS_KM = 70.0;

    /**
     * Calculate distance between two coordinates using Haversine formula
     */
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS_KM = 6371; // Earth's radius in kilometers

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = EARTH_RADIUS_KM * c;

        log.debug("Distance calculated: {} km between ({}, {}) and ({}, {})",
                String.format("%.2f", distance), lat1, lon1, lat2, lon2);

        return distance;
    }

    /**
     * Check if customer location is within delivery radius
     */
    public boolean isWithinDeliveryRadius(double customerLat, double customerLon) {
        double distance = getDistanceFromBusiness(customerLat, customerLon);
        boolean isWithin = distance <= DELIVERY_RADIUS_KM;

        log.info("Customer location ({}, {}) is {} km from business. Within delivery radius: {}",
                customerLat, customerLon, String.format("%.2f", distance), isWithin);

        return isWithin;
    }

    public double getDistanceFromBusiness(double customerLat, double customerLon) {
        return calculateDistance(BUSINESS_LAT, BUSINESS_LON, customerLat, customerLon);
    }

    public double getBusinessLatitude() {
        return BUSINESS_LAT;
    }

    public double getBusinessLongitude() {
        return BUSINESS_LON;
    }

    public double getDeliveryRadiusKm() {
        return DELIVERY_RADIUS_KM;
    }
}
