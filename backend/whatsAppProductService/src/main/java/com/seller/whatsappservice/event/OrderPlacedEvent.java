package com.seller.whatsappservice.event;

import com.seller.whatsappservice.dto.CartItemDto;
import com.seller.whatsappservice.model.Customer;
import com.seller.whatsappservice.model.CustomerOrder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class OrderPlacedEvent extends ApplicationEvent {
    private final CustomerOrder order;
    private final Customer customer;
    private final List<CartItemDto> items;

    public OrderPlacedEvent(Object source, CustomerOrder order, Customer customer, List<CartItemDto> items) {
        super(source);
        this.order = order;
        this.customer = customer;
        this.items = items;
    }
}
