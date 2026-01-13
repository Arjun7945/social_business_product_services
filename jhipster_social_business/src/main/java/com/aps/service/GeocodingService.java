package com.aps.service;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GeocodingService {

    private final Logger log = LoggerFactory.getLogger(GeocodingService.class);
    private final RestTemplate restTemplate;
    private final com.aps.service.util.InputValidator inputValidator;

    private static final String NOMINATIM_API_URL = "https://nominatim.openstreetmap.org/reverse?format=json&lat={lat}&lon={lon}";

    public GeocodingService(RestTemplateBuilder restTemplateBuilder,
            com.aps.service.util.InputValidator inputValidator) {
        this.restTemplate = restTemplateBuilder.build();
        this.inputValidator = inputValidator;
    }

    public String resolveAddress(double lat, double lon, String rawAddressString) {
        // 1. Try to extract Pincode from the raw message (if available)
        String pincode = inputValidator.extractPincode(rawAddressString);
        if (pincode != null) {
            return pincode;
        }

        // 2. Fallback to Nominatim Reverse Geocoding
        String fullAddress = getAddress(lat, lon);
        if (fullAddress != null) {
            // New Step: Try to extract pincode from the full address
            String extractedPincode = inputValidator.extractPincode(fullAddress);
            if (extractedPincode != null) {
                return extractedPincode;
            }
            return fullAddress;
        }

        // 3. Last Resort: Formatted format
        return String.format("Location shared via WhatsApp: %.6f, %.6f", lat, lon);
    }

    public String getAddress(double lat, double lon) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "SocialBusinessApp/1.0");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            Map<String, Object> uriVariables = Map.of("lat", lat, "lon", lon);

            @SuppressWarnings("unchecked")
            ResponseEntity<Map> response = restTemplate.exchange(
                    NOMINATIM_API_URL,
                    HttpMethod.GET,
                    entity,
                    Map.class,
                    uriVariables);

            if (response.getBody() != null) {
                // Return the full "display_name" which is the human readable address
                return (String) response.getBody().get("display_name");
            }
        } catch (Exception e) {
            log.error("Error fetching address from Nominatim", e);
        }
        return null;
    }
}
