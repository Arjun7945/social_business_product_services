package com.aps.service;

import com.aps.domain.BotSession;
import com.aps.domain.Customer;
import com.aps.domain.TeamMember;
import com.aps.domain.DeliveryPerson;
import com.aps.domain.enumeration.UserRole;
import com.aps.repository.CustomerRepository;
import com.aps.repository.TeamMemberRepository;
import com.aps.repository.DeliveryPersonRepository;
import com.aps.config.ApplicationProperties;
import com.aps.config.WhatsAppConfig;
import com.aps.service.dto.WhatsAppWebhookDto;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WhatsAppDispatcherService {

    private final Logger log = LoggerFactory.getLogger(WhatsAppDispatcherService.class);

    private final TeamMemberRepository teamMemberRepository;
    private final DeliveryPersonRepository deliveryPersonRepository;
    private final CustomerRepository customerRepository;
    private final AdminFlowService adminFlowService;
    private final ExecutiveFlowService executiveFlowService;
    private final DeliveryFlowService deliveryFlowService;
    private final AccountsFlowService accountsFlowService;

    private final CustomerFlowService customerFlowService;
    private final UnknownCustomerFlowService unknownCustomerFlowService;
    private final BotSessionManager sessionManager;
    private final ButtonActionService buttonActionService;
    private final WhatsAppService whatsAppService;
    private final LicensingService licensingService;

    public WhatsAppDispatcherService(
            TeamMemberRepository teamMemberRepository,
            DeliveryPersonRepository deliveryPersonRepository,
            CustomerRepository customerRepository,
            AdminFlowService adminFlowService,
            ExecutiveFlowService executiveFlowService,
            DeliveryFlowService deliveryFlowService,
            AccountsFlowService accountsFlowService,
            CustomerFlowService customerFlowService,
            UnknownCustomerFlowService unknownCustomerFlowService,
            BotSessionManager sessionManager,
            ButtonActionService buttonActionService,
            WhatsAppService whatsAppService,
            WhatsAppConfig whatsAppConfig,
            ApplicationProperties applicationProperties,
            LicensingService licensingService) {
        this.teamMemberRepository = teamMemberRepository;
        this.deliveryPersonRepository = deliveryPersonRepository;
        this.customerRepository = customerRepository;
        this.adminFlowService = adminFlowService;
        this.executiveFlowService = executiveFlowService;
        this.deliveryFlowService = deliveryFlowService;
        this.accountsFlowService = accountsFlowService;
        this.customerFlowService = customerFlowService;
        this.unknownCustomerFlowService = unknownCustomerFlowService;
        this.sessionManager = sessionManager;
        this.buttonActionService = buttonActionService;
        this.whatsAppService = whatsAppService;
        this.licensingService = licensingService;
    }

    @Async
    public void processWebhookAsync(WhatsAppWebhookDto webhookDto) {
        if (webhookDto.getEntry() != null) {
            webhookDto
                    .getEntry()
                    .forEach(entry -> {
                        if (entry.getChanges() != null) {
                            entry
                                    .getChanges()
                                    .forEach(change -> {
                                        if (change.getValue() != null && change.getValue().getMessages() != null) {
                                            change
                                                    .getValue()
                                                    .getMessages()
                                                    .forEach(message -> {
                                                        // KILL SWITCH: Check License before processing
                                                        if (!licensingService.isLicenseValid()) {
                                                            sendMaintenanceReply(change.getValue(), message);
                                                        } else {
                                                            handleIncomingMessage(change.getValue(), message);
                                                        }
                                                    });
                                        }
                                    });
                        }
                    });
        }
    }

    private void sendMaintenanceReply(WhatsAppWebhookDto.Value payloadValue, WhatsAppWebhookDto.Message message) {
        String from = message.getFrom();
        log.warn("⛔ REFUSING TO PROCESS WEBHOOK for {}: LICENSE IS INVALID/INACTIVE. Sending Maintenance Reply.", from);

        String userName = resolveSenderName(from, payloadValue);

        String maintenanceMessage = String.format(
                "Hello *%s*,\n\n" +
                        "We sincerely apologize for the inconvenience. 🛑\n\n" +
                        "Our services are currently *offline* for scheduled maintenance.\n" +
                        "We appreciate your patience and kindly request you to check back with us shortly.\n\n" +
                        "Thank you!",
                userName);

        whatsAppService.sendSimpleText(from, maintenanceMessage);
    }

    private String resolveSenderName(String from, WhatsAppWebhookDto.Value payloadValue) {
        try {
            // 1. Check TeamMember
            Optional<TeamMember> tm = teamMemberRepository.findByWaPhoneNumber(from);
            if (tm.isPresent())
                return tm.get().getName();

            // 2. DeliveryPerson
            Optional<DeliveryPerson> dp = deliveryPersonRepository.findByWaPhoneNumber(from);
            if (dp.isPresent())
                return dp.get().getName();

            // 3. Customer
            Optional<Customer> c = customerRepository.findByWaPhoneNumber(from);
            if (c.isPresent() && c.get().getName() != null)
                return c.get().getName();

            // 4. WhatsApp Profile Name
            if (payloadValue != null && payloadValue.getContacts() != null) {
                Optional<WhatsAppWebhookDto.Contact> contactOpt = payloadValue
                        .getContacts()
                        .stream()
                        .filter(contact -> contact.getWaId().equals(from))
                        .findFirst();
                if (contactOpt.isPresent() && contactOpt.get().getProfile() != null
                        && contactOpt.get().getProfile().getName() != null) {
                    return contactOpt.get().getProfile().getName();
                }
            }
        } catch (Exception e) {
            log.error("Error resolving name for maintenance message", e);
        }
        return "Valued Customer"; // Fallback
    }

    private void handleIncomingMessage(WhatsAppWebhookDto.Value payloadValue, WhatsAppWebhookDto.Message message) {
        try {
            String from = message.getFrom();
            log.info("Received message from: {}", from);

            // 0. Global Unique Token / Stale Button Check
            if (isStaleButton(message)) {
                handleStaleButton(from, message);
                return;
            }
            // Record button action if valid (non-stale) interactive response
            recordButtonActionIfInteractive(message);

            // 1. Check if it's a Team Member
            Optional<TeamMember> teamMemberOpt = teamMemberRepository.findByWaPhoneNumber(from);
            if (teamMemberOpt.isPresent()) {
                TeamMember teamMember = teamMemberOpt.get();
                // Ensure we don't accidentally process a Delivery Person if they are still in
                // TeamMember table
                if (teamMember.getRole() != UserRole.DELIVERY_PERSON) {
                    dispatchToTeamMemberFlow(teamMember, message);
                    return;
                }
            }

            // 2. Check if it's a Delivery Person
            Optional<DeliveryPerson> deliveryPersonOpt = deliveryPersonRepository.findByWaPhoneNumber(from);
            if (deliveryPersonOpt.isPresent()) {
                deliveryFlowService.handleDeliveryMessage(deliveryPersonOpt.get(), message);
                return;
            }

            // 3. Check if it's an existing Customer
            Optional<Customer> customerOpt = customerRepository.findByWaPhoneNumber(from);
            if (customerOpt.isPresent()) {
                log.info("Found existing customer for: {}", from);
                Customer customer = customerOpt.get();

                customerFlowService.handleCustomerMessage(customer, message);
                return;
            } else {
                log.info("No existing customer found for: {}", from);
            }

            // 4. New Customer (or in-progress onboarding)
            BotSession session = sessionManager.getSession(from);
            String currentState = session.getCurrentState();

            if (currentState != null && currentState.startsWith("UNKNOWN_")) {
                // Already in unknown flow
                unknownCustomerFlowService.handleMessage(from, message);
            } else {
                // Start new onboarding
                log.info("New unknown customer detected: {}", from);
                String profileName = "Guest";
                if (payloadValue != null && payloadValue.getContacts() != null) {
                    Optional<WhatsAppWebhookDto.Contact> contactOpt = payloadValue
                            .getContacts()
                            .stream()
                            .filter(c -> c.getWaId().equals(from))
                            .findFirst();
                    if (contactOpt.isPresent() && contactOpt.get().getProfile() != null) {
                        profileName = contactOpt.get().getProfile().getName();
                    }
                }
                unknownCustomerFlowService.startOnboarding(from, profileName);
            }
        } catch (Exception e) {
            log.error("CRITICAL: Unexpected error in WhatsAppDispatcherService for message: {}", message, e);
            // Optional: Send error message to system admin or metrics
        }
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
            case ACCOUNTS_TEAM:
                accountsFlowService.handleAccountsMessage(teamMember, message);
                break;
            // DELIVERY_PERSON handled in handleIncomingMessage directly via DeliveryPerson
            // entity
            case DELIVERY_PERSON:
                log.warn(
                        "Delivery Person found in TeamMember table but should be handled via DeliveryPerson entity: {}",
                        teamMember.getWaPhoneNumber());
                break;
            default:
                log.warn("Unknown role {} for team member {}. Treating as Customer.", role,
                        teamMember.getWaPhoneNumber());
                break;
        }
    }

    private boolean isStaleButton(WhatsAppWebhookDto.Message message) {
        if ("interactive".equals(message.getType()) && message.getContext() != null) {
            String waMessageId = message.getContext().getId();
            return buttonActionService.isButtonAlreadyClicked(waMessageId);
        }
        return false;
    }

    private void handleStaleButton(String from, WhatsAppWebhookDto.Message message) {
        log.warn("Ignored stale button click from {} (Msg ID: {})", from, message.getContext().getId());
        whatsAppService.sendSimpleText(from, "⚠️ This option has expired. Please use the latest menu.");

        // Attempt recovery for Customers
        Optional<Customer> customerOpt = customerRepository.findByWaPhoneNumber(from);
        if (customerOpt.isPresent()) {
            customerFlowService.recoverLastState(from);
        }
    }

    private void recordButtonActionIfInteractive(WhatsAppWebhookDto.Message message) {
        if ("interactive".equals(message.getType()) && message.getContext() != null) {
            String waMessageId = message.getContext().getId();
            String buttonId = "UNKNOWN";
            if (message.getInteractive() != null) {
                if (message.getInteractive().getButtonReply() != null) {
                    buttonId = message.getInteractive().getButtonReply().getId();
                } else if (message.getInteractive().getListReply() != null) {
                    buttonId = message.getInteractive().getListReply().getId();
                }
            }
            buttonActionService.recordButtonAction(waMessageId, buttonId, message.getFrom());
        }
    }
}
