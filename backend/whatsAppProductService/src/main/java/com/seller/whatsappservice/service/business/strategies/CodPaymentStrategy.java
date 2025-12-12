package com.seller.whatsappservice.service.business.strategies;

import com.seller.whatsappservice.model.CustomerOrder;
import org.springframework.stereotype.Component;

@Component("codPaymentStrategy")
public class CodPaymentStrategy implements PaymentStrategy {

    @Override
    public boolean processPayment(CustomerOrder order) {
        // COD logic: Payment is pending/collected on delivery.
        // In a real online payment, we would call an API here.
        return true;
    }

    @Override
    public String getPaymentMethodName() {
        return "COD";
    }
}
