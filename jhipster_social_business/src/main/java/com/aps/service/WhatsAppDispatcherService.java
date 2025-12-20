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

import org.springframework.scheduling.annotation.Async;
import java.util.Optional;
import com.aps.domain.BotSession;

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
    private final UnknownCustomerFlowService unknownCustomerFlowService;
    private final BotSessionManager sessionManager;
    private final ButtonActionService buttonActionService;
    private final WhatsAppService whatsAppService;

    public WhatsAppDispatcherService(TeamMemberRepository teamMemberRepository,
            CustomerRepository customerRepository,
            AdminFlowService adminFlowService,
            ExecutiveFlowService executiveFlowService,
            DeliveryFlowService deliveryFlowService,
            AccountsFlowService accountsFlowService,
            CustomerFlowService customerFlowService,
            UnknownCustomerFlowService unknownCustomerFlowService,
            BotSessionManager sessionManager,
            ButtonActionService buttonActionService,
            WhatsAppService whatsAppService) {
        this.teamMemberRepository = teamMemberRepository;
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
    }

    @Async
    public void handleIncomingMessage(WhatsAppWebhookDto.Value payloadValue, WhatsAppWebhookDto.Message message) {
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
                dispatchToTeamMemberFlow(teamMember, message);
                return;
            }

            // 2. Check if it's an existing Customer
            Optional<Customer> customerOpt = customerRepository.findByWaPhoneNumber(from);
            if (customerOpt.isPresent()) {
                log.info("Found existing customer for: {}", from);
                Customer customer = customerOpt.get();

                customerFlowService.handleCustomerMessage(customer, message);
                return;
            } else {
                log.info("No existing customer found for: {}", from);
            }

            // 3. New Customer (or in-progress onboarding)
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
                    Optional<WhatsAppWebhookDto.Contact> contactOpt = payloadValue.getContacts().stream()
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
