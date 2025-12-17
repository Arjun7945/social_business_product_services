package com.aps.service.payment;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Factory to retrieve the appropriate PaymentStrategy.
 */
@Service
public class PaymentStrategyFactory {

    private final Map<String, PaymentStrategy> strategyMap;

    public PaymentStrategyFactory(List<PaymentStrategy> strategies) {
        // Map by method name (e.g., "COD" -> CodPaymentStrategy)
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(PaymentStrategy::getPaymentMethodName, Function.identity()));
    }

    public PaymentStrategy getStrategy(String paymentMethod) {
        return strategyMap.get(paymentMethod);
    }
}
