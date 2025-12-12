package com.seller.whatsappservice.service.notification;

import com.seller.whatsappservice.event.OrderPlacedEvent;
import com.seller.whatsappservice.service.DeliveryFlowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeliveryNotificationListener {

    private final DeliveryFlowService deliveryFlowService;

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
