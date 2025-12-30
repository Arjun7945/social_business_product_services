package com.aps.service.order_service_event.event;

import com.aps.domain.CustomerOrder;
import org.springframework.context.ApplicationEvent;

public class PaymentFailureEvent extends ApplicationEvent {

    private final CustomerOrder order;
    private final String paymentId;

    public PaymentFailureEvent(Object source, CustomerOrder order, String paymentId) {
        super(source);
        this.order = order;
        this.paymentId = paymentId;
    }

    public CustomerOrder getOrder() {
        return order;
    }

    public String getPaymentId() {
        return paymentId;
    }
}
