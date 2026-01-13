package com.aps.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.aps.service.util.InputValidator;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class GeocodingServiceTest {

    @Mock
    private RestTemplateBuilder restTemplateBuilder;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private InputValidator inputValidator;

    private GeocodingService geocodingService;

    @BeforeEach
    void setUp() {
        when(restTemplateBuilder.build()).thenReturn(restTemplate);
        geocodingService = new GeocodingService(restTemplateBuilder, inputValidator);
    }

    @Test
    void resolveAddress_ShouldReturnPincode_WhenExtractedFromRawString() {
        // Arrange
        String rawString = "Test Address 123456";
        when(inputValidator.extractPincode(rawString)).thenReturn("123456");

        // Act
        String result = geocodingService.resolveAddress(10.0, 76.0, rawString);

        // Assert
        assertEquals("123456", result);
        verify(inputValidator).extractPincode(rawString);
        verifyNoInteractions(restTemplate); // Should not call Nominatim if pincode found in raw
    }

    @Test
    void resolveAddress_ShouldExtractPincode_FromNominatimResult() {
        // Arrange
        String rawString = "Raw Address Without Pincode";
        String nominatimFullAddress = "Some Place, District, State, 654321, Country";

        // 1. Fail to extract from raw string
        when(inputValidator.extractPincode(rawString)).thenReturn(null);

        // 2. Setup Nominatim response
        Map<String, Object> nominatimResponse = Map.of("display_name", nominatimFullAddress);
        ResponseEntity<Map> responseEntity = ResponseEntity.ok(nominatimResponse);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Map.class),
                any(Map.class))).thenReturn(responseEntity);

        // 3. Extract pincode from Nominatim address
        when(inputValidator.extractPincode(nominatimFullAddress)).thenReturn("654321");

        // Act
        String result = geocodingService.resolveAddress(10.0, 76.0, rawString);

        // Assert
        assertEquals("654321", result);
        verify(inputValidator).extractPincode(rawString); // Check raw
        verify(inputValidator).extractPincode(nominatimFullAddress); // Check full address
    }

    @Test
    void resolveAddress_ShouldReturnFullAddress_WhenNoPincodeFoundAnywhere() {
        // Arrange
        String rawString = "Raw Address";
        String nominatimFullAddress = "Some Place, District, State, Country";

        when(inputValidator.extractPincode(rawString)).thenReturn(null);

        Map<String, Object> nominatimResponse = Map.of("display_name", nominatimFullAddress);
        ResponseEntity<Map> responseEntity = ResponseEntity.ok(nominatimResponse);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Map.class),
                any(Map.class))).thenReturn(responseEntity);

        when(inputValidator.extractPincode(nominatimFullAddress)).thenReturn(null);

        // Act
        String result = geocodingService.resolveAddress(10.0, 76.0, rawString);

        // Assert
        assertEquals(nominatimFullAddress, result);
    }
}
