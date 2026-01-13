package com.aps.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.aps.domain.BotSession;
import com.aps.domain.Customer;
import com.aps.domain.DeliveryZone;
import com.aps.domain.enumeration.CustomerFlowStage;
import com.aps.repository.CustomerRepository;
import com.aps.repository.DeliveryZoneRepository;
import com.aps.service.dto.WhatsAppWebhookDto;
import com.aps.service.util.InputValidator;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@ExtendWith(MockitoExtension.class)
class UnknownCustomerFlowServiceTest {

    @Mock
    private WhatsAppService whatsAppService;

    @Mock
    private BotSessionManager sessionManager;

    @Mock
    private InputValidator inputValidator;

    @Mock
    private LocationValidationService locationValidationService;

    @Mock
    private GeocodingService geocodingService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private DeliveryZoneRepository deliveryZoneRepository;

    @Mock
    private CustomerFlowService customerFlowService;

    @InjectMocks
    private UnknownCustomerFlowService unknownCustomerFlowService;

    private BotSession session;

    @BeforeEach
    void setUp() {
        session = new BotSession();
        session.setWaPhoneNumber("1234567890");
        session.setCurrentState(CustomerFlowStage.UNKNOWN_LOCATION.name());
        session.setSessionData("{\"tempName\":\"John\",\"tempPhone\":\"1234567890\"}");

        lenient().when(sessionManager.getSession(anyString())).thenReturn(session);
        TransactionSynchronizationManager.initSynchronization();
    }

    @AfterEach
    void tearDown() {
        TransactionSynchronizationManager.clearSynchronization();
    }

    @Test
    void handleLocationMessage_ShouldAssignRandomZone() {
        // Arrange
        String waPhoneNumber = "1234567890";
        WhatsAppWebhookDto.Location location = new WhatsAppWebhookDto.Location();
        location.setLatitude(10.0);
        location.setLongitude(76.0);
        location.setAddress("Some Address");

        when(sessionManager.getSessionDataString(session, "tempName")).thenReturn("John");
        when(sessionManager.getSessionDataString(session, "tempPhone")).thenReturn("1234567890");
        when(geocodingService.resolveAddress(anyDouble(), anyDouble(), anyString())).thenReturn("Resolved Address");

        DeliveryZone zone1 = new DeliveryZone();
        zone1.setId(1L);
        DeliveryZone zone2 = new DeliveryZone();
        zone2.setId(2L);
        when(deliveryZoneRepository.findAll()).thenReturn(List.of(zone1, zone2));

        // Act
        unknownCustomerFlowService.handleMessage(waPhoneNumber, createLocationMessage(location));

        // Assert
        verify(customerRepository, atLeastOnce()).save(any(Customer.class));
        verify(deliveryZoneRepository).findAll();
    }

    private WhatsAppWebhookDto.Message createLocationMessage(WhatsAppWebhookDto.Location location) {
        WhatsAppWebhookDto.Message message = new WhatsAppWebhookDto.Message();
        message.setType("location");
        message.setLocation(location);
        return message;
    }
}
