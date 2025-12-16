package com.aps.service.payment;

import com.aps.domain.CustomerOrder;

/**
 * Strategy interface for processing payments.
 */
public interface PaymentStrategy {

    /**
     * Process the payment for the given order.
     * 
     * @param order The order to process payment for.
     * @return true if payment successful, false otherwise.
     */
    boolean processPayment(CustomerOrder order);

    /**
     * Get the name of the payment method this strategy handles.
     * 
     * @return Payment method name (e.g., "COD", "UPI").
     */
    String getPaymentMethodName();
}
