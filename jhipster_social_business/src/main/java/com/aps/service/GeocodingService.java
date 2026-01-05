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

    private static final String NOMINATIM_API_URL = "https://nominatim.openstreetmap.org/reverse?format=json&lat={lat}&lon={lon}";

    public GeocodingService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public String getPincode(double lat, double lon) {
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
                @SuppressWarnings("unchecked")
                Map<String, Object> address = (Map<String, Object>) response.getBody().get("address");
                if (address != null && address.containsKey("postcode")) {
                    return (String) address.get("postcode");
                }
            }
        } catch (Exception e) {
            log.error("Error fetching pincode from Nominatim", e);
        }
        return null;
    }
}
