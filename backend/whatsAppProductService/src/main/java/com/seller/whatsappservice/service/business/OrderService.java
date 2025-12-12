package com.seller.whatsappservice.service.business;

import com.seller.whatsappservice.dto.CartItemDto;
import com.seller.whatsappservice.event.OrderPlacedEvent;
import com.seller.whatsappservice.model.Customer;
import com.seller.whatsappservice.model.CustomerOrder;
import com.seller.whatsappservice.model.OrderItem;
import com.seller.whatsappservice.model.enums.OrderStatus;
import com.seller.whatsappservice.repository.CustomerOrderRepository;
import com.seller.whatsappservice.repository.OrderItemRepository;
import com.seller.whatsappservice.service.ShoppingCartService;
import com.seller.whatsappservice.service.business.strategies.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final CustomerOrderRepository customerOrderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ShoppingCartService shoppingCartService;
    private final ApplicationEventPublisher eventPublisher;
    private final Map<String, PaymentStrategy> paymentStrategies;

    /**
     * Creates an order for the customer using the specified payment method.
     * 
     * @param customer          The customer placing the order.
     * @param paymentMethodName The name of the payment method (e.g., "COD").
     * @return The created CustomerOrder, or throws RuntimeException if validation
     *         fails.
     */
    @Transactional
    public CustomerOrder createOrder(Customer customer, String paymentMethodName) {
        // 1. Validate Cart
        if (shoppingCartService.isCartEmpty(customer.getId())) {
            throw new IllegalStateException("Cannot place order with empty cart");
        }

        List<CartItemDto> items = shoppingCartService.getCartItems(customer.getId());
        Double total = shoppingCartService.calculateCartTotal(customer.getId());

        // 2. Select Payment Strategy
        PaymentStrategy strategy = paymentStrategies.get(paymentMethodName.toLowerCase() + "PaymentStrategy");
        if (strategy == null) {
            // Fallback or explicit check. Since we rely on bean names like
            // "codPaymentStrategy".
            // Alternative: Iterate list and check getPaymentMethodName().
            strategy = paymentStrategies.values().stream()
                    .filter(s -> s.getPaymentMethodName().equalsIgnoreCase(paymentMethodName))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid payment method: " + paymentMethodName));
        }

        // 3. Create Draft Order (Transient)
        CustomerOrder order = CustomerOrder.builder()
                .customerId(customer.getId())
                .orderTime(LocalDateTime.now())
                .totalAmount(total)
                .status(OrderStatus.PENDING)
                .paymentMethod(paymentMethodName.toUpperCase())
                .build();

        // 4. Process Payment
        boolean paymentSuccess = strategy.processPayment(order);
        if (!paymentSuccess) {
            throw new RuntimeException("Payment processing failed for method: " + paymentMethodName);
        }

        // 5. Save Order
        order = customerOrderRepository.save(order);
        log.info("Order {} created for customer {}", order.getId(), customer.getId());

        // 6. Save Order Items
        for (CartItemDto item : items) {
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .fishProductId(item.getFishProductId())
                    .quantityKg(item.getQuantityKg())
                    .priceAtOrder(item.getPricePerKg())
                    .build();
            orderItemRepository.save(orderItem);
        }

        // 7. Clear Cart
        shoppingCartService.clearCart(customer.getId());

        // 8. Publish Event (Decoupled Notification)
        eventPublisher.publishEvent(new OrderPlacedEvent(this, order, customer, items));

        return order;
    }
}
