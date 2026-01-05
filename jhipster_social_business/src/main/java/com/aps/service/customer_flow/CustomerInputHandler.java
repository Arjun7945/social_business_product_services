package com.aps.service.customer_flow;

import com.aps.domain.BotSession;
import com.aps.domain.Customer;
import com.aps.domain.enumeration.CustomerFlowStage;
import com.aps.repository.CustomerRepository;
import com.aps.service.CustomerMessageService;
import com.aps.service.LocationValidationService;
import com.aps.service.WhatsAppService;
import com.aps.service.dto.WhatsAppWebhookDto;
import com.aps.service.util.InputValidator;
import com.aps.service.GeocodingService;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Handles customer inputs during onboarding and location sharing.
 * Extracted from CustomerFlowService.
 */
@Service
public class CustomerInputHandler {

    private final Logger log = LoggerFactory.getLogger(CustomerInputHandler.class);

    private final CustomerRepository customerRepository;
    private final WhatsAppService whatsAppService;
    private final CustomerMessageService messageService;
    private final InputValidator inputValidator;
    private final LocationValidationService locationValidationService;
    private final GeocodingService geocodingService;
    private final FlowStateService flowStateService;

    public CustomerInputHandler(
            CustomerRepository customerRepository,
            WhatsAppService whatsAppService,
            CustomerMessageService messageService,
            InputValidator inputValidator,
            LocationValidationService locationValidationService,
            GeocodingService geocodingService,
            FlowStateService flowStateService) {
        this.customerRepository = customerRepository;
        this.whatsAppService = whatsAppService;
        this.messageService = messageService;
        this.inputValidator = inputValidator;
        this.locationValidationService = locationValidationService;
        this.geocodingService = geocodingService;
        this.flowStateService = flowStateService;
    }

    public void handleAwaitingName(Customer customer, BotSession session, String text) {
        String inputName = text.trim();
        // Validation: Check for common invalid names
        if (!inputValidator.isValidName(inputName)) {
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                    "Please enter your *real full name* to continue.");
            return;
        }

        customer.setName(inputName);
        customerRepository.save(customer);
        whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), messageService.getNameConfirmation(text.trim()));
        flowStateService.updateStage(session, CustomerFlowStage.AWAITING_PHONE);
    }

    public void handleAwaitingPhone(Customer customer, BotSession session, String text) {
        String phone = text.trim();
        // Allow digits, spaces, and + for country code. Min length 7, max 16.
        if (!inputValidator.isValidPhoneNumber(phone)) {
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), messageService.getInvalidPhoneNumber());
            return;
        }

        customer.setPhoneNumber(phone);
        customerRepository.save(customer);
        whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                messageService.getPhoneConfirmationAndLocationRequest());
        flowStateService.updateStage(session, CustomerFlowStage.AWAITING_LOCATION);
    }

    public void handleLocationMessage(Customer customer, BotSession session, WhatsAppWebhookDto.Location location) {
        double customerLat = location.getLatitude();
        double customerLon = location.getLongitude();

        log.info("Received location from customer {}: lat={}, lon={}", customer.getWaPhoneNumber(), customerLat,
                customerLon);

        customer.setLocationLat(customerLat);
        customer.setLocationLon(customerLon);

        // Extract pincode from address if available
        String pincode = inputValidator.extractPincode(location.getAddress());
        if (pincode == null) {
            pincode = geocodingService.getPincode(customerLat, customerLon);
        }

        if (pincode != null) {
            customer.setAddress(pincode);
        }

        double distance = locationValidationService.getDistanceFromBusiness(customerLat, customerLon);
        customer.setDistanceFromBusinessKm(distance);

        if (locationValidationService.isWithinDeliveryRadius(customerLat, customerLon)) {
            flowStateService.updateStage(session, CustomerFlowStage.REGISTERED);
            customer.setJoinedAt(Instant.now());
            customerRepository.save(customer);

            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                    messageService.getLocationAccepted(customer.getName(), distance));
        } else {
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                    messageService.getLocationRejected(customer.getName(), distance));
            flowStateService.updateStage(session, CustomerFlowStage.NEW);
        }
    }
}
