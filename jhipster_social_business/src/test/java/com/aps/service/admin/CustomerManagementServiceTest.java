package com.aps.service.admin;

import static org.mockito.Mockito.*;

import com.aps.domain.Customer;
import com.aps.domain.TeamMember;
import com.aps.domain.enumeration.UserRole;
import com.aps.repository.CustomerRepository;
import com.aps.service.BotSessionManager;
import com.aps.service.CreditCustomerFlowService;
import com.aps.service.LocationValidationService;
import com.aps.service.UserRemovalService;
import com.aps.service.WhatsAppService;
import com.aps.service.util.InputValidator;
import com.aps.repository.DeliveryZoneRepository;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerManagementServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private WhatsAppService whatsAppService;
    @Mock
    private LocationValidationService locationValidationService;
    @Mock
    private BotSessionManager sessionManager;
    @Mock
    private InputValidator inputValidator;
    @Mock
    private UserRemovalService userRemovalService;
    @Mock
    private DeliveryZoneRepository deliveryZoneRepository;

    @Mock
    private CreditCustomerFlowService creditCustomerFlowService;
    @Mock
    private com.aps.service.ExecutiveFlowService executiveFlowService;

    @InjectMocks
    private CustomerManagementService customerManagementService;

    private TeamMember admin;

    @BeforeEach
    void setUp() {
        admin = new TeamMember();
        admin.setWaPhoneNumber("919876543210");
        admin.setName("TestAdmin");
    }

    @Test
    void showCustomersByRole_CreditCustomer_ShouldCallCreditMenu() {
        // Arrange
        List<Customer> customers = new ArrayList<>();
        Customer c1 = new Customer();
        c1.setName("Credit User 1");
        c1.setRole(UserRole.CREDIT_CUSTOMER);
        customers.add(c1);

        when(customerRepository.findAllByRole(UserRole.CREDIT_CUSTOMER)).thenReturn(customers);

        // Act
        customerManagementService.showAllCreditCustomers(admin);

        // Assert
        verify(customerRepository).findAllByRole(UserRole.CREDIT_CUSTOMER);
        verify(whatsAppService).sendSimpleText(eq(admin.getWaPhoneNumber()), contains("(Total: 1)")); // Expecting
                                                                                                      // message
                                                                                                      // call
        verify(creditCustomerFlowService).showCreditCustomerMenu(admin);
    }

    @Test
    void showCustomersByRole_CreditCustomer_Empty_ShouldCallCreditMenu() {
        // Arrange
        when(customerRepository.findAllByRole(UserRole.CREDIT_CUSTOMER)).thenReturn(new ArrayList<>());

        // Act
        customerManagementService.showAllCreditCustomers(admin);

        // Assert
        verify(whatsAppService).sendSimpleText(eq(admin.getWaPhoneNumber()), contains("No CREDIT_CUSTOMERs found"));
        verify(creditCustomerFlowService).showCreditCustomerMenu(admin);
    }

    @Test
    void showCustomersByRole_RegularCustomer_ShouldCallCustomerMenu() {
        // Arrange
        Customer c1 = new Customer();
        c1.setName("Regular User 1");
        c1.setRole(UserRole.CUSTOMER);
        List<Customer> customers = List.of(c1);

        when(customerRepository.findAllByRole(UserRole.CUSTOMER)).thenReturn(customers);

        // Act
        customerManagementService.showAllCustomers(admin);

        // Assert
        verify(customerRepository).findAllByRole(UserRole.CUSTOMER);
        // Should Call Customer Menu
        verify(whatsAppService).sendInteractiveList(eq(admin.getWaPhoneNumber()), contains("Customer Management"),
                anyList());
    }

    @Test
    void showMyCustomers_Executive_ShouldRedirectToExecutiveMenu() {
        // Arrange
        admin.setRole(UserRole.EXECUTIVE);
        when(customerRepository.findByAddedBy(admin)).thenReturn(new ArrayList<>());
        when(sessionManager.getSession(anyString())).thenReturn(mock(com.aps.domain.BotSession.class));

        // Act
        customerManagementService.showMyCustomers(admin);

        // Assert
        verify(executiveFlowService).showMainMenu(eq(admin), any(com.aps.domain.BotSession.class));
        verify(whatsAppService, never()).sendInteractiveList(anyString(), contains("Customer Management"), anyList());
    }

    @Test
    void showMyCustomers_Admin_ShouldShowCustomerMenu() {
        // Arrange
        admin.setRole(UserRole.ADMIN);
        when(customerRepository.findByAddedBy(admin)).thenReturn(new ArrayList<>());

        // Act
        customerManagementService.showMyCustomers(admin);

        // Assert
        verify(whatsAppService).sendInteractiveList(eq(admin.getWaPhoneNumber()), contains("Customer Management"),
                anyList());
        verifyNoInteractions(executiveFlowService);
    }
}
