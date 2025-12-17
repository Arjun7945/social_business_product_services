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
import com.aps.service.event.OrderPlacedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

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

    public OrderService(CustomerOrderRepository customerOrderRepository,
            OrderItemRepository orderItemRepository,
            FishProductRepository fishProductRepository,
            CartService cartService,
            ApplicationEventPublisher eventPublisher,
            WhatsAppService whatsAppService,
            CustomerMessageService customerMessageService,
            DeliveryPersonMessageService deliveryPersonMessageService) {
        this.customerOrderRepository = customerOrderRepository;
        this.orderItemRepository = orderItemRepository;
        this.fishProductRepository = fishProductRepository;
        this.cartService = cartService;
        this.eventPublisher = eventPublisher;
        this.whatsAppService = whatsAppService;
        this.customerMessageService = customerMessageService;
        this.deliveryPersonMessageService = deliveryPersonMessageService;
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
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentMethod(paymentMethodName.toUpperCase());

        // 4. Set Initial Status
        // Payment processing is handled separately (Async or Delivery Flow)
        order.setStatus(OrderStatus.PENDING);

        // 5. Save Order
        order = customerOrderRepository.save(order);
        log.info("Order {} created for customer {}", order.getId(), customer.getId());

        // 6. Save Order Items
        for (CartItemDetailsDTO item : items) {
            FishProduct product = fishProductRepository.findById(item.getFishProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getFishProductId()));

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
        customerOrderRepository.findById(orderId).ifPresent(order -> {
            log.info("Processing Payment Success for Order: {}", orderId);

            // Update Status
            order.setStatus(OrderStatus.CONFIRMED);
            // Ideally store paymentId in order or payment entity, but redundant for MVP
            customerOrderRepository.save(order);

            // Notify Customer
            String custMsg = customerMessageService.getPaymentCapturedMessage(
                    paymentId, amount, orderId);
            whatsAppService.sendSimpleText(order.getCustomer().getWaPhoneNumber(), custMsg);

            // Notify Delivery Person (if assigned)
            if (order.getDeliveryPerson() != null) {
                String dpMsg = deliveryPersonMessageService.getPaymentReceivedMessage(
                        paymentId, amount, orderId);
                whatsAppService.sendSimpleText(order.getDeliveryPerson().getWaPhoneNumber(), dpMsg);
            }
        });
    }

    /**
     * Processes a failed payment notification.
     */
    @Transactional
    public void processPaymentFailure(Long orderId, String paymentId) {
        customerOrderRepository.findById(orderId).ifPresent(order -> {
            log.warn("Processing Payment Failure for Order: {}", orderId);

            // Keep status as PENDING or move to PAYMENT_FAILED if exists
            // order.setStatus(OrderStatus.PAYMENT_FAILED); // If enum exists

            // Notify Customer
            String custMsg = customerMessageService.getPaymentFailedMessage(paymentId, orderId);
            whatsAppService.sendSimpleText(order.getCustomer().getWaPhoneNumber(), custMsg);

            // Notify Delivery Person (if assigned)
            if (order.getDeliveryPerson() != null) {
                String dpMsg = deliveryPersonMessageService.getPaymentFailedMessage(paymentId, orderId);
                whatsAppService.sendSimpleText(order.getDeliveryPerson().getWaPhoneNumber(), dpMsg);
            }
        });
    }
}
