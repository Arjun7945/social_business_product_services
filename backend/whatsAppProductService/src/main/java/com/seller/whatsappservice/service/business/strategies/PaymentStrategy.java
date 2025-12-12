package com.seller.whatsappservice.service.business.strategies;

import com.seller.whatsappservice.model.CustomerOrder;

public interface PaymentStrategy {
    boolean processPayment(CustomerOrder order);

    String getPaymentMethodName();
}
