package com.aps.service.payment;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * Factory to retrieve the appropriate PaymentStrategy.
 */
@Service
public class PaymentStrategyFactory {

    private final Map<com.aps.domain.enumeration.PaymentMode, PaymentStrategy> strategyMap;

    public PaymentStrategyFactory(List<PaymentStrategy> strategies) {
        // Map by method name (e.g., "COD" -> CodPaymentStrategy)
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(PaymentStrategy::getPaymentMode, Function.identity()));
    }

    public PaymentStrategy getStrategy(com.aps.domain.enumeration.PaymentMode paymentMode) {
        return strategyMap.get(paymentMode);
    }
}
