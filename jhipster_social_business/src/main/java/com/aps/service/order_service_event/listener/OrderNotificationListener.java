package com.aps.service.order_service_event.listener;

import com.aps.domain.CustomerOrder;
import com.aps.service.CustomerFlowService;
import com.aps.service.CustomerMessageService;
import com.aps.service.DeliveryPersonMessageService;
import com.aps.service.WhatsAppService;
import com.aps.service.order_service_event.event.OrderPlacedEvent;
import com.aps.service.order_service_event.event.PaymentFailureEvent;
import com.aps.service.order_service_event.event.PaymentSuccessEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Listener for handling Order-related notifications and flow triggers.
 * Decouples OrderService from CustomerFlowService and Notification services.
 */
@Component
public class OrderNotificationListener {

    private final Logger log = LoggerFactory.getLogger(OrderNotificationListener.class);

    private final WhatsAppService whatsAppService;
    private final CustomerMessageService customerMessageService;
    private final DeliveryPersonMessageService deliveryPersonMessageService;
    private final CustomerFlowService customerFlowService;

    public OrderNotificationListener(
            WhatsAppService whatsAppService,
            CustomerMessageService customerMessageService,
            DeliveryPersonMessageService deliveryPersonMessageService,
            CustomerFlowService customerFlowService) {
        this.whatsAppService = whatsAppService;
        this.customerMessageService = customerMessageService;
        this.deliveryPersonMessageService = deliveryPersonMessageService;
        this.customerFlowService = customerFlowService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderPlacedEvent(OrderPlacedEvent event) {
        log.info("Handling OrderPlacedEvent for order {}", event.getOrder().getId());
        // Logic specific to Order Placed (if any additional notifications needed)
        // Currently, OrderFlowHandler sends confirmation directly for immediate
        // feedback.
        // We can migrate that here later if strict decoupling is desired for placement
        // too.
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentSuccessEvent(PaymentSuccessEvent event) {
        CustomerOrder order = event.getOrder();
        log.info("Handling PaymentSuccessEvent for order {}", order.getId());

        try {
            // Notify Customer
            String custMsg = customerMessageService.getPaymentCapturedMessage(
                    event.getPaymentId(),
                    event.getAmount(),
                    order.getId());
            whatsAppService.sendSimpleText(order.getCustomer().getWaPhoneNumber(), custMsg);

            // Trigger Re-order Flow
            customerFlowService.sendReOrderFlow(order.getCustomer());

            // Notify Delivery Person (if assigned)
            if (order.getDeliveryPerson() != null && order.getDeliveryPerson().getWaPhoneNumber() != null) {
                String dpMsg = deliveryPersonMessageService.getPaymentReceivedMessage(
                        event.getPaymentId(),
                        event.getAmount(),
                        order.getId());
                whatsAppService.sendSimpleText(order.getDeliveryPerson().getWaPhoneNumber(), dpMsg);
            }
        } catch (Exception e) {
            log.error("Failed to send WhatsApp notifications for payment success event, order {}", order.getId(), e);
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentFailureEvent(PaymentFailureEvent event) {
        CustomerOrder order = event.getOrder();
        log.info("Handling PaymentFailureEvent for order {}", order.getId());

        try {
            // Notify Customer
            String custMsg = customerMessageService.getPaymentFailedMessage(event.getPaymentId(), order.getId());
            whatsAppService.sendSimpleText(order.getCustomer().getWaPhoneNumber(), custMsg);

            // Notify Delivery Person (if assigned)
            if (order.getDeliveryPerson() != null && order.getDeliveryPerson().getWaPhoneNumber() != null) {
                String dpMsg = deliveryPersonMessageService.getPaymentFailedMessage(event.getPaymentId(),
                        order.getId());
                whatsAppService.sendSimpleText(order.getDeliveryPerson().getWaPhoneNumber(), dpMsg);
            }
        } catch (Exception e) {
            log.error("Failed to send WhatsApp notifications for payment failure event, order {}", order.getId(), e);
        }
    }
}
