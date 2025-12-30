package com.aps.service.order_service_event.event;

import com.aps.domain.CustomerOrder;
import org.springframework.context.ApplicationEvent;

public class PaymentSuccessEvent extends ApplicationEvent {

    private final CustomerOrder order;
    private final String paymentId;
    private final Double amount;

    public PaymentSuccessEvent(Object source, CustomerOrder order, String paymentId, Double amount) {
        super(source);
        this.order = order;
        this.paymentId = paymentId;
        this.amount = amount;
    }

    public CustomerOrder getOrder() {
        return order;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public Double getAmount() {
        return amount;
    }
}
