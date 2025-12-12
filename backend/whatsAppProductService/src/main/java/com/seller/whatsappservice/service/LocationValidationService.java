package com.seller.whatsappservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for validating customer location against delivery radius
 * Business location: 10.79427, 76.53016
 * Delivery radius: 50 km
 */
@Service
@Slf4j
public class LocationValidationService {

    // Business location coordinates
    private static final double BUSINESS_LAT = 10.7944769;
    private static final double BUSINESS_LON = 76.5306715;
    private static final double DELIVERY_RADIUS_KM = 70.0;

    /**
     * Calculate distance between two coordinates using Haversine formula
     * 
     * @param lat1 Latitude of first point
     * @param lon1 Longitude of first point
     * @param lat2 Latitude of second point
     * @param lon2 Longitude of second point
     * @return distance in kilometers
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
     * 
     * @param customerLat Customer's latitude
     * @param customerLon Customer's longitude
     * @return true if within 50km, false otherwise
     */
    public boolean isWithinDeliveryRadius(double customerLat, double customerLon) {
        double distance = getDistanceFromBusiness(customerLat, customerLon);
        boolean isWithin = distance <= DELIVERY_RADIUS_KM;

        log.info("Customer location ({}, {}) is {} km from business. Within delivery radius: {}",
                customerLat, customerLon, String.format("%.2f", distance), isWithin);

        return isWithin;
    }

    /**
     * Get distance from business location
     * 
     * @param customerLat Customer's latitude
     * @param customerLon Customer's longitude
     * @return distance in kilometers
     */
    public double getDistanceFromBusiness(double customerLat, double customerLon) {
        return calculateDistance(BUSINESS_LAT, BUSINESS_LON, customerLat, customerLon);
    }

    /**
     * Get business location latitude
     */
    public double getBusinessLatitude() {
        return BUSINESS_LAT;
    }

    /**
     * Get business location longitude
     */
    public double getBusinessLongitude() {
        return BUSINESS_LON;
    }

    /**
     * Get delivery radius in kilometers
     */
    public double getDeliveryRadiusKm() {
        return DELIVERY_RADIUS_KM;
    }
}
