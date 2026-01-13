package com.aps.service.admin;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aps.domain.BotSession;
import com.aps.domain.DeliveryPerson;
import com.aps.domain.DeliveryZone;
import com.aps.domain.TeamMember;
import com.aps.domain.enumeration.AdminFlowStage;
import com.aps.repository.DeliveryPersonRepository;
import com.aps.repository.DeliveryZoneRepository;
import com.aps.service.BotSessionManager;
import com.aps.service.UserRemovalService;
import com.aps.service.WhatsAppService;
import com.aps.service.util.InputValidator;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeliveryPersonManagementServiceTest {

    @Mock
    private DeliveryPersonRepository deliveryPersonRepository;

    @Mock
    private DeliveryZoneRepository deliveryZoneRepository;

    @Mock
    private WhatsAppService whatsAppService;

    @Mock
    private BotSessionManager sessionManager;

    @Mock
    private InputValidator inputValidator;

    @Mock
    private UserRemovalService userRemovalService;

    @Mock
    private com.aps.service.AdminMessageService adminMessageService;

    @Mock
    private com.aps.repository.CustomerOrderRepository customerOrderRepository;

    @InjectMocks
    private DeliveryPersonManagementService deliveryPersonManagementService;

    private TeamMember admin;
    private BotSession session;

    @BeforeEach
    void setUp() {
        admin = new TeamMember();
        admin.setName("Admin");
        admin.setWaPhoneNumber("1234567890");

        session = new BotSession();
        session.setWaPhoneNumber("1234567890");
    }

    @Test
    void startDeleteDeliveryPerson_NoPersons_SendsNoFoundMessage() {
        when(deliveryPersonRepository.findAllWithEagerRelationships()).thenReturn(Collections.emptyList());
        when(adminMessageService.getNoDeliveryPersonsFound()).thenReturn("No delivery persons found");

        deliveryPersonManagementService.startDeleteDeliveryPerson(admin, session);

        verify(whatsAppService).sendSimpleText(eq("1234567890"), eq("No delivery persons found"));
        // Should return to menu, which sends a menu list
        verify(whatsAppService).sendInteractiveList(eq("1234567890"), anyString(), any());
    }

    @Test
    void startDeleteDeliveryPerson_WithPersons_SendsList() {
        DeliveryPerson dp = new DeliveryPerson();
        dp.setId(1L);
        dp.setName("John Doe");
        dp.setIsActive(true);
        DeliveryZone zone = new DeliveryZone();
        zone.setZoneName("Zone A");
        dp.setZone(zone);

        when(deliveryPersonRepository.findAllWithEagerRelationships()).thenReturn(List.of(dp));
        when(adminMessageService.getDeleteDeliveryPersonHeader()).thenReturn("Delete Delivery Person");
        when(customerOrderRepository.countByDeliveryPersonIdAndStatusIn(any(), any())).thenReturn(0L);

        deliveryPersonManagementService.startDeleteDeliveryPerson(admin, session);

        verify(whatsAppService).sendInteractiveList(eq("1234567890"), eq("Delete Delivery Person"), eq("View List"),
                any());
        verify(sessionManager).updateState(session, AdminFlowStage.AWAITING_DELETE_DELIVERY_SELECTION.name());
    }

    @Test
    void handleDeleteDeliveryPersonSelection_ValidId_NoPendingOrders_SendsConfirmation() {
        String selectionId = "DELETE_DP_1";
        DeliveryPerson dp = new DeliveryPerson();
        dp.setId(1L);
        dp.setName("John Doe");
        dp.setWaPhoneNumber("9876543210");
        dp.setIsActive(true);

        when(deliveryPersonRepository.findOneWithToOneRelationships(1L)).thenReturn(Optional.of(dp));
        when(customerOrderRepository.countByDeliveryPersonIdAndStatusIn(eq(1L), any())).thenReturn(0L);
        when(adminMessageService.getButtonConfirmDelete()).thenReturn("Confirm");
        when(adminMessageService.getButtonCancel()).thenReturn("Cancel");
        when(adminMessageService.getConfirmDeletionHeader(anyString(), any(), anyString(), anyString()))
                .thenReturn("Confirm Deletion");

        deliveryPersonManagementService.handleDeleteDeliveryPersonSelection(admin, session, selectionId);

        verify(whatsAppService).sendCartActionButtons(eq("1234567890"), eq("Confirm Deletion"), any());
    }

    @Test
    void handleDeleteDeliveryPersonSelection_ValidId_HasPendingOrders_SendsWarning() {
        String selectionId = "DELETE_DP_1";
        DeliveryPerson dp = new DeliveryPerson();
        dp.setId(1L);
        dp.setName("John Doe");

        when(deliveryPersonRepository.findOneWithToOneRelationships(1L)).thenReturn(Optional.of(dp));
        when(customerOrderRepository.countByDeliveryPersonIdAndStatusIn(eq(1L), any())).thenReturn(5L);
        when(adminMessageService.getPendingOrdersWarning("John Doe", 5L)).thenReturn("Warning: Pending Orders");
        when(adminMessageService.getButtonGoBack()).thenReturn("Go Back");
        when(adminMessageService.getButtonReport()).thenReturn("Report");

        deliveryPersonManagementService.handleDeleteDeliveryPersonSelection(admin, session, selectionId);

        verify(whatsAppService).sendCartActionButtons(eq("1234567890"), eq("Warning: Pending Orders"), any());
    }

    @Test
    void handleDeleteDeliveryPersonSelection_InvalidId_SendsErrorMessage() {
        String selectionId = "DELETE_DP_999";
        when(deliveryPersonRepository.findOneWithToOneRelationships(999L)).thenReturn(Optional.empty());

        deliveryPersonManagementService.handleDeleteDeliveryPersonSelection(admin, session, selectionId);

        verify(whatsAppService).sendSimpleText(eq("1234567890"), anyString());
    }
}
