package com.aps.service.order_service_event.event;

import com.aps.domain.Customer;
import com.aps.domain.CustomerOrder;
import com.aps.service.dto.CartItemDetailsDTO;
import java.util.List;
import org.springframework.context.ApplicationEvent;

/**
 * Event published when an order is successfully placed.
 * Decoupled from the WhatsApp messaging logic.
 */
public class OrderPlacedEvent extends ApplicationEvent {

    private final CustomerOrder order;
    private final Customer customer;
    private final List<CartItemDetailsDTO> items;

    public OrderPlacedEvent(Object source, CustomerOrder order, Customer customer, List<CartItemDetailsDTO> items) {
        super(source);
        this.order = order;
        this.customer = customer;
        this.items = items;
    }

    public CustomerOrder getOrder() {
        return order;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<CartItemDetailsDTO> getItems() {
        return items;
    }
}
