package com.aps.service.notification;

import com.aps.service.DeliveryFlowService;
import com.aps.service.event.OrderPlacedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class DeliveryNotificationListener {

    private final Logger log = LoggerFactory.getLogger(DeliveryNotificationListener.class);

    private final DeliveryFlowService deliveryFlowService;

    public DeliveryNotificationListener(DeliveryFlowService deliveryFlowService) {
        this.deliveryFlowService = deliveryFlowService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderPlacedEvent(OrderPlacedEvent event) {
        log.info("Received OrderPlacedEvent for order {}", event.getOrder().getId());
        try {
            deliveryFlowService.sendOrderToAllDeliveryPersons(event.getOrder(), event.getCustomer(), event.getItems());
        } catch (Exception e) {
            log.error("Failed to send delivery notifications for order {}", event.getOrder().getId(), e);
        }
    }
}
