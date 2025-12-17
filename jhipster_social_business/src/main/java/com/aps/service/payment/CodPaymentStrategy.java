package com.aps.service.payment;

import com.aps.config.FlowConstants;
import com.aps.domain.Customer;
import com.aps.domain.CustomerOrder;
import com.aps.domain.TeamMember;
import com.aps.service.DeliveryPersonMessageService;
import com.aps.service.WhatsAppService;
import com.aps.service.dto.WhatsAppMessageDto;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

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
    public void initiatePayment(CustomerOrder order, TeamMember deliveryPerson) {
        // Send COD Confirmation
        whatsAppService.sendSimpleText(deliveryPerson.getWaPhoneNumber(),
                deliveryPersonMessageService.getPaymentModeCodSelected());

        // Immediately show dashboard
        sendDeliveryDashboard(deliveryPerson.getWaPhoneNumber(), order.getId(), order.getCustomer());
    }

    @Override
    public String getPaymentMethodName() {
        return "COD";
    }

    private void sendDeliveryDashboard(String waId, Long orderId, Customer customer) {
        List<WhatsAppMessageDto.ButtonDto> buttons = List.of(
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder()
                                .id(FlowConstants.PREFIX_SHIPPED + orderId)
                                .title(deliveryPersonMessageService
                                        .getButtonMarkShipped())
                                .build())
                        .build(),
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder()
                                .id(FlowConstants.PREFIX_DELIVERED + orderId)
                                .title(deliveryPersonMessageService
                                        .getButtonMarkDelivered())
                                .build())
                        .build());

        String dashboardMsg = deliveryPersonMessageService.getDeliveryDashboardHeader(orderId,
                customer.getName());

        whatsAppService.sendCartActionButtons(waId, dashboardMsg, buttons);
    }
}
