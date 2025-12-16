package com.aps.service.payment;

import com.aps.domain.CustomerOrder;
import com.aps.domain.enumeration.OrderStatus;
import org.springframework.stereotype.Component;

/**
 * Payment strategy for Cash on Delivery (COD).
 * COD is always successful at the ordering stage.
 */
@Component("codPaymentStrategy")
public class CodPaymentStrategy implements PaymentStrategy {

    @Override
    public boolean processPayment(CustomerOrder order) {
        // COD logic: verification is implicit.
        // We might want to set status to PENDING or CONFIRMED depending on flow.
        // Legacy set it to PENDING.
        order.setStatus(OrderStatus.PENDING);
        // We could log or send specific COD notifications here if needed.
        return true;
    }

    @Override
    public String getPaymentMethodName() {
        return "COD";
    }
}
