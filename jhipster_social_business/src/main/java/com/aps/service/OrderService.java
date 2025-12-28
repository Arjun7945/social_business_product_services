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
import com.aps.service.event.OrderPlacedEvent;
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
    private final WhatsAppService whatsAppService;
    private final CustomerMessageService customerMessageService;
    private final DeliveryPersonMessageService deliveryPersonMessageService;
    private final CustomerFlowService customerFlowService;
    private final OrderStatusHistoryService orderStatusHistoryService;

    public OrderService(
        CustomerOrderRepository customerOrderRepository,
        OrderItemRepository orderItemRepository,
        FishProductRepository fishProductRepository,
        CartService cartService,
        ApplicationEventPublisher eventPublisher,
        WhatsAppService whatsAppService,
        CustomerMessageService customerMessageService,
        DeliveryPersonMessageService deliveryPersonMessageService,
        @org.springframework.context.annotation.Lazy CustomerFlowService customerFlowService,
        OrderStatusHistoryService orderStatusHistoryService
    ) {
        this.customerOrderRepository = customerOrderRepository;
        this.orderItemRepository = orderItemRepository;
        this.fishProductRepository = fishProductRepository;
        this.cartService = cartService;
        this.eventPublisher = eventPublisher;
        this.whatsAppService = whatsAppService;
        this.customerMessageService = customerMessageService;
        this.deliveryPersonMessageService = deliveryPersonMessageService;
        this.customerFlowService = customerFlowService;
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
        order.setPaymentMethod(paymentMethodName.toUpperCase());

        // 4. Set Initial Status
        // Payment processing is handled separately (Async or Delivery Flow)
        order.setStatus(OrderStatus.ORDER_NOT_TAKEN);

        // 5. Save Order
        order = customerOrderRepository.save(order);
        orderStatusHistoryService.addEvent(order);
        log.info("Order {} created for customer {}", order.getId(), customer.getId());

        // 6. Save Order Items
        // 6. Save Order Items
        // Optimization: Fetch all products in one query to avoid N+1
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
     * Processes a successful payment notification.
     */
    @Transactional
    public void processPaymentSuccess(Long orderId, String paymentId, Double amount) {
        customerOrderRepository
            .findById(orderId)
            .ifPresent(order -> {
                log.info("Processing Payment Success for Order: {}", orderId);

                // Update Status to DELIVERED as payment on delivery confirms handover
                order.setStatus(OrderStatus.ORDER_DELIVERED_SUCESSFULLY);
                // Ideally store paymentId in order or payment entity
                order.setTransactionId(paymentId);
                customerOrderRepository.save(order);
                orderStatusHistoryService.addEvent(order);

                // Notify Customer & Delivery Person AFTER transaction commit
                org.springframework.transaction.support.TransactionSynchronizationManager.registerSynchronization(
                    new org.springframework.transaction.support.TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            try {
                                // Notify Customer
                                String custMsg = customerMessageService.getPaymentCapturedMessage(paymentId, amount, orderId);
                                whatsAppService.sendSimpleText(order.getCustomer().getWaPhoneNumber(), custMsg);

                                // Trigger Re-order Flow (Standardized for all payment success)
                                customerFlowService.sendReOrderFlow(order.getCustomer());

                                // Notify Delivery Person (if assigned)
                                if (order.getDeliveryPerson() != null) {
                                    String dpMsg = deliveryPersonMessageService.getPaymentReceivedMessage(paymentId, amount, orderId);
                                    whatsAppService.sendSimpleText(order.getDeliveryPerson().getWaPhoneNumber(), dpMsg);
                                }
                            } catch (Exception e) {
                                log.error("Failed to send WhatsApp notifications after payment success for order {}", orderId, e);
                            }
                        }
                    }
                );
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

                // Update status to FAILED
                order.setStatus(OrderStatus.ORDER_FAILED);
                order.setTransactionId(paymentId);
                customerOrderRepository.save(order);
                orderStatusHistoryService.addEvent(order);

                // Notify Customer & Delivery Person AFTER transaction commit
                org.springframework.transaction.support.TransactionSynchronizationManager.registerSynchronization(
                    new org.springframework.transaction.support.TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            try {
                                // Notify Customer
                                String custMsg = customerMessageService.getPaymentFailedMessage(paymentId, orderId);
                                whatsAppService.sendSimpleText(order.getCustomer().getWaPhoneNumber(), custMsg);

                                // Notify Delivery Person (if assigned)
                                if (order.getDeliveryPerson() != null) {
                                    String dpMsg = deliveryPersonMessageService.getPaymentFailedMessage(paymentId, orderId);
                                    whatsAppService.sendSimpleText(order.getDeliveryPerson().getWaPhoneNumber(), dpMsg);
                                }
                            } catch (Exception e) {
                                log.error("Failed to send WhatsApp notifications after payment failure for order {}", orderId, e);
                            }
                        }
                    }
                );
            });
    }
}
