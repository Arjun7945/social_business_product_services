package com.aps.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.aps.service.util.InputValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class GeocodingServiceTest {

    @Mock
    private RestTemplateBuilder restTemplateBuilder;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private InputValidator inputValidator;

    @InjectMocks
    private GeocodingService geocodingService;

    @Test
    void resolveAddress_ShouldReturnPincode_WhenExtracted() {
        // Arrange
        when(inputValidator.extractPincode("Test Address 123456")).thenReturn("123456");

        // Act
        String result = geocodingService.resolveAddress(10.0, 76.0, "Test Address 123456");

        // Assert
        assertEquals("123456", result);
        verify(inputValidator).extractPincode("Test Address 123456");
    }
}
