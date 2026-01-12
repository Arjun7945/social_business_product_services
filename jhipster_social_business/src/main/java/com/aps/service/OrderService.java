package com.aps.service;

import com.aps.domain.Customer;
import com.aps.domain.CustomerOrder;
import com.aps.domain.FishProduct;
import com.aps.domain.OrderItem;
import com.aps.domain.enumeration.OrderStatus;
import com.aps.repository.CustomerOrderRepository;
import com.aps.repository.FishProductRepository;
import com.aps.repository.OrderItemRepository;
import com.aps.service.dto.CartItemDetailsDTO;
import com.aps.service.errors.ProductUnavailableException;
import com.aps.service.order_service_event.event.OrderPlacedEvent;
import com.aps.service.order_service_event.event.PaymentFailureEvent;
import com.aps.service.order_service_event.event.PaymentSuccessEvent;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for business logic related to Orders.
 * Handles order creation, validation, and payment processing.
 * REFACTORED: Decoupled from Notification/Flow logic via Events.
 */
@Service
@Transactional
public class OrderService {

    private final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final CustomerOrderRepository customerOrderRepository;
    private final OrderItemRepository orderItemRepository;
    private final FishProductRepository fishProductRepository;
    private final CartService cartService;
    private final ApplicationEventPublisher eventPublisher;
    private final OrderStatusHistoryService orderStatusHistoryService;

    public OrderService(
            CustomerOrderRepository customerOrderRepository,
            OrderItemRepository orderItemRepository,
            FishProductRepository fishProductRepository,
            CartService cartService,
            ApplicationEventPublisher eventPublisher,
            OrderStatusHistoryService orderStatusHistoryService) {
        this.customerOrderRepository = customerOrderRepository;
        this.orderItemRepository = orderItemRepository;
        this.fishProductRepository = fishProductRepository;
        this.cartService = cartService;
        this.eventPublisher = eventPublisher;
        this.orderStatusHistoryService = orderStatusHistoryService;
    }

    /**
     * Creates an order for the customer using the specified payment method.
     */
    public CustomerOrder createOrder(Customer customer, String paymentMethodName) {
        // 1. Validate Cart
        if (cartService.isCartEmpty(customer.getId())) {
            throw new IllegalStateException("Cannot place order with empty cart");
        }

        List<CartItemDetailsDTO> items = cartService.getCartItems(customer.getId());
        Double totalDouble = cartService.calculateCartTotal(customer.getId());
        BigDecimal total = BigDecimal.valueOf(totalDouble);

        CustomerOrder order = new CustomerOrder();
        order.setCustomer(customer);
        order.setOrderTime(Instant.now());
        order.setTotalAmount(total);
        order.setStatus(OrderStatus.ORDER_NOT_TAKEN);
        order.setPaymentMethod(com.aps.domain.enumeration.PaymentMode.valueOf(paymentMethodName.toUpperCase()));
        order.setConfirmedAt(Instant.now()); // Set confirmedAt for initial status

        // 5. Save Order
        order = customerOrderRepository.save(order);
        orderStatusHistoryService.addEvent(order);
        log.info("Order {} created for customer {}", order.getId(), customer.getId());

        // 6. Save Order Items
        java.util.Set<Long> productIds = items
                .stream()
                .map(CartItemDetailsDTO::getFishProductId)
                .collect(java.util.stream.Collectors.toSet());

        java.util.Map<Long, FishProduct> productMap = fishProductRepository
                .findAllById(productIds)
                .stream()
                .collect(java.util.stream.Collectors.toMap(FishProduct::getId, java.util.function.Function.identity()));

        for (CartItemDetailsDTO item : items) {
            FishProduct product = productMap.get(item.getFishProductId());
            if (product == null) {
                throw new RuntimeException("Product not found: " + item.getFishProductId());
            }

            if (!Boolean.TRUE.equals(product.getIsAvailable())) {
                throw new ProductUnavailableException("Product " + product.getName() + " is no longer available.");
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantityKg(item.getQuantityKg());
            // Store the price at the time of order (per kg)
            orderItem.setPriceAtOrder(BigDecimal.valueOf(item.getPricePerKg()));

            orderItemRepository.save(orderItem);
        }

        // 7. Clear Cart
        cartService.clearCart(customer.getId());

        // 8. Publish Event
        eventPublisher.publishEvent(new OrderPlacedEvent(this, order, customer, items));

        return order;
    }

    /**
     * Updates order status and correctly sets confirmedAt timestamp.
     * Use this method for ALL status changes to ensure consistency.
     */
    public void updateOrderStatus(CustomerOrder order, OrderStatus newStatus) {
        if (order.getStatus() != newStatus) {
            log.info("Updating status for Order {}: {} -> {}", order.getId(), order.getStatus(), newStatus);
            order.setStatus(newStatus);
            order.setConfirmedAt(Instant.now());
            customerOrderRepository.save(order);
            orderStatusHistoryService.addEvent(order);
        }
    }

    /**
     * Processes a successful payment notification.
     */
    @Transactional
    public void processPaymentSuccess(Long orderId, String paymentId, Double amount) {
        customerOrderRepository
                .findById(orderId)
                .ifPresent(order -> {
                    log.info("Processing Payment Success for Order: {}", orderId);

                    updateOrderStatus(order, OrderStatus.ORDER_DELIVERED_SUCESSFULLY);

                    order.setTransactionId(paymentId);
                    customerOrderRepository.save(order);

                    // Publish Event instead of direct calls
                    eventPublisher.publishEvent(new PaymentSuccessEvent(this, order, paymentId, amount));
                });
    }

    /**
     * Processes a failed payment notification.
     */
    @Transactional
    public void processPaymentFailure(Long orderId, String paymentId) {
        customerOrderRepository
                .findById(orderId)
                .ifPresent(order -> {
                    log.warn("Processing Payment Failure for Order: {}", orderId);

                    updateOrderStatus(order, OrderStatus.ORDER_FAILED);

                    order.setTransactionId(paymentId);
                    customerOrderRepository.save(order);

                    // Publish Event instead of direct calls
                    eventPublisher.publishEvent(new PaymentFailureEvent(this, order, paymentId));
                });
    }
}
