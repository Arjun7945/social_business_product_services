package com.aps.service.payment;

import com.aps.domain.CustomerOrder;
import com.aps.domain.DeliveryPerson;
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

    public CodPaymentStrategy(@Lazy WhatsAppService whatsAppService,
            DeliveryPersonMessageService deliveryPersonMessageService) {
        this.whatsAppService = whatsAppService;
        this.deliveryPersonMessageService = deliveryPersonMessageService;
    }

    @Override
    public void initiatePayment(CustomerOrder order, DeliveryPerson deliveryPerson) {
        // COD logic: No external payment gateway interaction needed.
        // Just log and possibly send a confirmation message to the delivery person.
        String message = String.format(
                "💰 *Collect Cash: ₹%.2f*\n\nOrder #%d confirmed as COD.\nPlease collect cash from customer upon delivery.",
                order.getTotalAmount(),
                order.getId());

        whatsAppService.sendSimpleText(deliveryPerson.getWaPhoneNumber(), message);
    }

    @Override
    public String getPaymentMethodName() {
        return "COD";
    }
}
