package com.aps.service.payment;

import com.aps.domain.CustomerOrder;

/**
 * Strategy interface for processing payments.
 */
public interface PaymentStrategy {
    /**
     * Initiates the payment process.
     *
     * @param order          The order to process payment for.
     * @param deliveryPerson The delivery person initiating the payment.
     */
    void initiatePayment(CustomerOrder order, com.aps.domain.TeamMember deliveryPerson);

    /**
     * Get the name of the payment method this strategy handles.
     *
     * @return Payment method name (e.g., "COD", "UPI").
     */
    String getPaymentMethodName();
}
