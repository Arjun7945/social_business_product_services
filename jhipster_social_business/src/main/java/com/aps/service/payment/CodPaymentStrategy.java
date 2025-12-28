package com.aps.service.payment;

import com.aps.domain.CustomerOrder;
import com.aps.domain.TeamMember;
import com.aps.service.DeliveryPersonMessageService;
import com.aps.service.WhatsAppService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Payment strategy for Cash on Delivery (COD).
 * Sends confirmation and shows dashboard.
 */
@Component("codPaymentStrategy")
public class CodPaymentStrategy implements PaymentStrategy {

    private final WhatsAppService whatsAppService;
    private final DeliveryPersonMessageService deliveryPersonMessageService;

    public CodPaymentStrategy(@Lazy WhatsAppService whatsAppService, DeliveryPersonMessageService deliveryPersonMessageService) {
        this.whatsAppService = whatsAppService;
        this.deliveryPersonMessageService = deliveryPersonMessageService;
    }

    @Override
    public void initiatePayment(CustomerOrder order, TeamMember deliveryPerson) {
        // Send COD Confirmation
        whatsAppService.sendSimpleText(deliveryPerson.getWaPhoneNumber(), deliveryPersonMessageService.getPaymentModeCodSelected());
    }

    @Override
    public String getPaymentMethodName() {
        return "COD";
    }
}
