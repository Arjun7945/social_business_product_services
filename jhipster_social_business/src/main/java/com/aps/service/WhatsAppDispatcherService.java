package com.aps.service;

import com.aps.domain.Customer;
import com.aps.domain.TeamMember;
import com.aps.domain.enumeration.UserRole;
import com.aps.repository.CustomerRepository;
import com.aps.repository.TeamMemberRepository;
import com.aps.service.dto.WhatsAppWebhookDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@Transactional
public class WhatsAppDispatcherService {

    private final Logger log = LoggerFactory.getLogger(WhatsAppDispatcherService.class);

    private final TeamMemberRepository teamMemberRepository;
    private final CustomerRepository customerRepository;
    private final AdminFlowService adminFlowService;
    private final ExecutiveFlowService executiveFlowService;
    private final DeliveryFlowService deliveryFlowService;
    private final AccountsFlowService accountsFlowService;

    private final CustomerFlowService customerFlowService;

    public WhatsAppDispatcherService(TeamMemberRepository teamMemberRepository,
            CustomerRepository customerRepository,
            AdminFlowService adminFlowService,
            ExecutiveFlowService executiveFlowService,
            DeliveryFlowService deliveryFlowService,
            AccountsFlowService accountsFlowService,
            CustomerFlowService customerFlowService) {
        this.teamMemberRepository = teamMemberRepository;
        this.customerRepository = customerRepository;
        this.adminFlowService = adminFlowService;
        this.executiveFlowService = executiveFlowService;
        this.deliveryFlowService = deliveryFlowService;
        this.accountsFlowService = accountsFlowService;
        this.customerFlowService = customerFlowService;
    }

    public void handleIncomingMessage(WhatsAppWebhookDto.Value payloadValue, WhatsAppWebhookDto.Message message) {
        String from = message.getFrom();
        log.info("Received message from: {}", from);

        // 1. Check if it's a Team Member
        Optional<TeamMember> teamMemberOpt = teamMemberRepository.findByWaPhoneNumber(from);
        if (teamMemberOpt.isPresent()) {
            TeamMember teamMember = teamMemberOpt.get();
            dispatchToTeamMemberFlow(teamMember, message);
            return;
        }

        // 2. Check if it's an existing Customer
        Optional<Customer> customerOpt = customerRepository.findByWaPhoneNumber(from);
        if (customerOpt.isPresent()) {
            log.info("Found existing customer for: {}", from);
            Customer customer = customerOpt.get();

            // [REMOVED] Self-healing logic for "Hi"/"Abort" names/phones.
            // Data integrity should be enforced at the entry point (handleAwaitingName),
            // not patched here.

            customerFlowService.handleCustomerMessage(customer, message);
            return;
        } else {
            log.info("No existing customer found for: {}", from);
        }

        // 3. New Customer?
        // Create a new generic customer or handle as guest.
        log.info("New customer detected: {}", from);
        Customer newCustomer = new Customer();
        newCustomer.setWaPhoneNumber(from);
        newCustomer.setPhoneNumber(from); // Default phone number to WA number until updated
        newCustomer.setRole(UserRole.CUSTOMER);
        newCustomer.setJoinedAt(Instant.now());
        newCustomer.setIsPincodeValid(false); // Default to false until location verified

        // Try to get name from contacts
        String profileName = "Guest";
        if (payloadValue != null && payloadValue.getContacts() != null) {
            Optional<WhatsAppWebhookDto.Contact> contactOpt = payloadValue.getContacts().stream()
                    .filter(c -> c.getWaId().equals(from))
                    .findFirst();
            if (contactOpt.isPresent() && contactOpt.get().getProfile() != null) {
                profileName = contactOpt.get().getProfile().getName();
                log.info("Found profile name for {}: {}", from, profileName);
            }
        }
        newCustomer.setName(profileName);

        customerRepository.save(newCustomer);

        customerFlowService.handleCustomerMessage(newCustomer, message);
    }

    private void dispatchToTeamMemberFlow(TeamMember teamMember, WhatsAppWebhookDto.Message message) {
        UserRole role = teamMember.getRole();
        if (role == null) {
            log.warn("Team member {} has no role assigned. Ignoring.", teamMember.getWaPhoneNumber());
            return;
        }

        switch (role) {
            case ADMIN:
                adminFlowService.handleAdminMessage(teamMember, message);
                break;
            case EXECUTIVE:
                executiveFlowService.handleExecutiveMessage(teamMember, message);
                break;
            case DELIVERY_PERSON:
                deliveryFlowService.handleDeliveryMessage(teamMember, message);
                break;
            case ACCOUNTS_TEAM:
                accountsFlowService.handleAccountsMessage(teamMember, message);
                break;
            default:
                log.warn("Unknown role {} for team member {}. Treating as Customer.", role,
                        teamMember.getWaPhoneNumber());
                // Fallback to customer flow? Or Error?
                // For safety, maybe just ignore or send "Access Denied"
                break;
        }
    }
}
