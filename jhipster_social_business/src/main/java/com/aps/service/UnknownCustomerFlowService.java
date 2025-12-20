package com.aps.service;

import com.aps.domain.BotSession;
import com.aps.domain.Customer;
import com.aps.domain.enumeration.CustomerFlowStage;
import com.aps.domain.enumeration.UserRole;
import com.aps.repository.CustomerRepository;
import com.aps.service.dto.WhatsAppMessageDto;
import com.aps.service.dto.WhatsAppWebhookDto;
import com.aps.service.util.InputValidator;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class UnknownCustomerFlowService {

    private final WhatsAppService whatsAppService;
    private final BotSessionManager sessionManager;
    private final InputValidator inputValidator;
    private final LocationValidationService locationValidationService;
    private final CustomerRepository customerRepository;
    private final CustomerFlowService customerFlowService; // To transition to registered flow

    public UnknownCustomerFlowService(WhatsAppService whatsAppService,
            BotSessionManager sessionManager,
            InputValidator inputValidator,
            LocationValidationService locationValidationService,
            CustomerRepository customerRepository,
            CustomerFlowService customerFlowService) {
        this.whatsAppService = whatsAppService;
        this.sessionManager = sessionManager;
        this.inputValidator = inputValidator;
        this.locationValidationService = locationValidationService;
        this.customerRepository = customerRepository;
        this.customerFlowService = customerFlowService;
    }

    public void startOnboarding(String waPhoneNumber, String profileName) {
        BotSession session = sessionManager.getSession(waPhoneNumber);

        // Store profile name in session for confirmation
        String nameToConfirm = (profileName != null && !profileName.isEmpty()) ? profileName : "Guest";
        sessionManager.setSessionData(session, "tempName", nameToConfirm);

        // Send Greeting & Name Confirmation
        String message = String.format("നമസ്കാരം %s! 🌟✨\n" +
                "ഞങ്ങളുടെ ബിസിനസ്സിലേക്ക് സ്വാഗതം! 🙏\n" +
                "നിങ്ങൾക്ക് ഓർഡർ ചെയ്യുന്നതിന് മുൻപായി ചില വിവരങ്ങൾ നൽകേണ്ടതുണ്ട്.\n\n" +
                "നിങ്ങളുടെ പേര് *%s* എന്നാണോ? 🤔", nameToConfirm, nameToConfirm);

        List<WhatsAppMessageDto.ButtonDto> buttons = List.of(
                createButton("CONFIRM_NAME_YES", "അതെ (Yes)"),
                createButton("CONFIRM_NAME_NO", "അല്ല (No)"));

        whatsAppService.sendCartActionButtons(waPhoneNumber, message, buttons);
        updateStage(session, CustomerFlowStage.UNKNOWN_NAME_CONFIRM);
    }

    public void handleMessage(String waPhoneNumber, WhatsAppWebhookDto.Message message) {
        BotSession session = sessionManager.getSession(waPhoneNumber);
        CustomerFlowStage stage = getStage(session);

        if (message.getType().equals("interactive")) {
            if (message.getInteractive().getType().equals("button_reply")) {
                handleButtonReply(waPhoneNumber, session, stage, message.getInteractive().getButtonReply());
            }
        } else if (message.getType().equals("text")) {
            handleTextMessage(waPhoneNumber, session, stage, message.getText().getBody());
        } else if (message.getType().equals("location")) {
            handleLocationMessage(waPhoneNumber, session, stage, message.getLocation());
        }
    }

    private void handleButtonReply(String waPhoneNumber, BotSession session, CustomerFlowStage stage,
            WhatsAppWebhookDto.ButtonReply buttonReply) {
        String id = buttonReply.getId();

        if (stage == CustomerFlowStage.UNKNOWN_NAME_CONFIRM) {
            if ("CONFIRM_NAME_YES".equals(id)) {
                // Name confirmed, proceed to phone check
                askPhoneCheck(waPhoneNumber, session);
            } else if ("CONFIRM_NAME_NO".equals(id)) {
                // Name wrong, ask for input
                whatsAppService.sendSimpleText(waPhoneNumber, "ശരി, നിങ്ങളുടെ മുഴുവൻ പേര് ടൈപ്പ് ചെയ്ത് അയക്കുക: 👇");
                updateStage(session, CustomerFlowStage.UNKNOWN_NAME_INPUT);
            }
        } else if (stage == CustomerFlowStage.UNKNOWN_PHONE_CHECK) {
            if ("PHONE_SAME".equals(id)) {
                // Use WA number (User confirmed "Same")
                sessionManager.setSessionData(session, "tempPhone", waPhoneNumber);
                askLocation(waPhoneNumber, session);
            } else if ("PHONE_CHANGE".equals(id)) {
                whatsAppService.sendSimpleText(waPhoneNumber,
                        "ദയവായി നിങ്ങളുടെ മൊബൈൽ നമ്പർ ടൈപ്പ് ചെയ്യുക (Eg: 9876543210): 🔢");
                updateStage(session, CustomerFlowStage.UNKNOWN_PHONE_INPUT);
            }
        }
    }

    private void handleTextMessage(String waPhoneNumber, BotSession session, CustomerFlowStage stage, String text) {
        if (stage == CustomerFlowStage.UNKNOWN_NAME_INPUT) {
            String name = text.trim();
            if (!inputValidator.isValidName(name)) {
                whatsAppService.sendSimpleText(waPhoneNumber,
                        "ദയവായി ശരിയായ പേര് നൽകുക. (അക്ഷരങ്ങൾ മാത്രം ഉപയോഗിക്കുക)");
                return;
            }
            sessionManager.setSessionData(session, "tempName", name);
            askPhoneCheck(waPhoneNumber, session);

        } else if (stage == CustomerFlowStage.UNKNOWN_PHONE_INPUT) {
            String phone = text.trim();
            if (!inputValidator.isValidPhoneNumber(phone)) { // You might need to make isValidPhoneNumber public or
                                                             // duplicate logic
                whatsAppService.sendSimpleText(waPhoneNumber, "ദയവായി ശരിയായ 10 അക്ക മൊബൈൽ നമ്പർ നൽകുക.");
                return;
            }
            sessionManager.setSessionData(session, "tempPhone", phone);
            askLocation(waPhoneNumber, session);
        }
    }

    private void handleLocationMessage(String waPhoneNumber, BotSession session, CustomerFlowStage stage,
            WhatsAppWebhookDto.Location location) {
        if (stage == CustomerFlowStage.UNKNOWN_LOCATION) {
            // FINALIZE REGISTRATION
            String name = sessionManager.getSessionDataString(session, "tempName");
            String phone = sessionManager.getSessionDataString(session, "tempPhone");

            Customer newCustomer = new Customer();
            newCustomer.setWaPhoneNumber(waPhoneNumber);
            newCustomer.setName(name);
            newCustomer.setPhoneNumber(phone);
            newCustomer.setRole(UserRole.CUSTOMER);
            newCustomer.setJoinedAt(Instant.now());

            // Location
            newCustomer.setLocationLat(location.getLatitude());
            newCustomer.setLocationLon(location.getLongitude());

            double distance = locationValidationService.getDistanceFromBusiness(location.getLatitude(),
                    location.getLongitude());
            newCustomer.setDistanceFromBusinessKm(distance);
            newCustomer.setIsPincodeValid(
                    locationValidationService.isWithinDeliveryRadius(location.getLatitude(), location.getLongitude()));

            customerRepository.save(newCustomer);

            // Clear temp session data? Optional.

            // Send Success Message
            // Send Success Message
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    whatsAppService.sendSimpleText(waPhoneNumber,
                            String.format("നന്ദി! നിങ്ങളുടെ രജിസ്‌ട്രേഷൻ പൂർത്തിയായി. 🎉\n" +
                                    "ഇനി നിങ്ങൾക്ക് സാധനങ്ങൾ ഓർഡർ ചെയ്യാാം! 🐟🦐\n\n", name));

                    // Optional: Automatically show catalog? - Moved inside afterCommit to ensure
                    // data is ready
                    customerFlowService.handleCustomerMessage(newCustomer, createDummyTextMessage("start"));
                }
            });

            // Transition to Registered Flow
            updateStage(session, CustomerFlowStage.REGISTERED);
        }
    }

    // --- Helpers ---

    private void askPhoneCheck(String waPhoneNumber, BotSession session) {
        String message = String.format("നന്ദി! ✅\n" +
                "വാട്സ്ആപ്പ് ഉപയോഗിക്കുന്ന *%s* തന്നെയാണോ വിളിക്കാനും ഉപയോഗിക്കേണ്ടത്? അതോ വേറെ നമ്പർ ഉണ്ടോ? 📱",
                waPhoneNumber);

        List<WhatsAppMessageDto.ButtonDto> buttons = List.of(
                createButton("PHONE_SAME", "ഇത് മതി (Same)"),
                createButton("PHONE_CHANGE", "വേറെ നമ്പർ (Change)"));
        whatsAppService.sendCartActionButtons(waPhoneNumber, message, buttons);
        updateStage(session, CustomerFlowStage.UNKNOWN_PHONE_CHECK);
    }

    private void askLocation(String waPhoneNumber, BotSession session) {
        whatsAppService.sendSimpleText(waPhoneNumber, "ശരി! 👍\n" +
                "അവസാനമായി, സാധനങ്ങൾ എത്തിക്കുന്നതിനായി നിങ്ങളുടെ *Location* അയക്കുക. 📍\n" +
                "_(Paperclip 📎 -> Location -> Send Your Current Location അമർത്തുക)_");
        updateStage(session, CustomerFlowStage.UNKNOWN_LOCATION);
    }

    private void updateStage(BotSession session, CustomerFlowStage stage) {
        sessionManager.updateState(session, stage.name());
    }

    private CustomerFlowStage getStage(BotSession session) {
        try {
            return CustomerFlowStage.valueOf(session.getCurrentState());
        } catch (Exception e) {
            return CustomerFlowStage.NEW;
        }
    }

    private WhatsAppMessageDto.ButtonDto createButton(String id, String title) {
        return WhatsAppMessageDto.ButtonDto.builder()
                .type("reply")
                .reply(WhatsAppMessageDto.ReplyDto.builder()
                        .id(id)
                        .title(title)
                        .build())
                .build();
    }

    // Helper to trigger start flow
    private WhatsAppWebhookDto.Message createDummyTextMessage(String text) {
        WhatsAppWebhookDto.Message msg = new WhatsAppWebhookDto.Message();
        msg.setType("text");
        WhatsAppWebhookDto.Text textObj = new WhatsAppWebhookDto.Text();
        textObj.setBody(text);
        msg.setText(textObj);
        return msg;
    }
}
