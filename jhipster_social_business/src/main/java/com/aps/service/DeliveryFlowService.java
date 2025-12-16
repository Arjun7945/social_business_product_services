package com.aps.service;

import com.aps.domain.BotSession;
import com.aps.domain.Customer;
import com.aps.domain.CustomerOrder;
import com.aps.domain.TeamMember;
import com.aps.domain.enumeration.OrderStatus;
import com.aps.domain.enumeration.UserRole;
import com.aps.repository.CustomerOrderRepository;
import com.aps.repository.CustomerRepository;
import com.aps.repository.TeamMemberRepository;
import com.aps.service.dto.CartItemDetailsDTO;
import com.aps.service.dto.WhatsAppWebhookDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import com.aps.config.FlowConstants;
import com.aps.service.dto.WhatsAppMessageDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DeliveryFlowService {

        private final Logger log = LoggerFactory.getLogger(DeliveryFlowService.class);

        private final WhatsAppService whatsAppService;
        private final BotSessionManager sessionManager;
        private final DeliveryPersonMessageService deliveryPersonMessageService;
        private final TeamMemberRepository teamMemberRepository;
        private final CustomerOrderRepository customerOrderRepository;
        private final CustomerRepository customerRepository;

        public DeliveryFlowService(@Lazy WhatsAppService whatsAppService,
                        BotSessionManager sessionManager,
                        DeliveryPersonMessageService deliveryPersonMessageService,
                        TeamMemberRepository teamMemberRepository,
                        CustomerOrderRepository customerOrderRepository,
                        CustomerRepository customerRepository) {
                this.whatsAppService = whatsAppService;
                this.sessionManager = sessionManager;
                this.deliveryPersonMessageService = deliveryPersonMessageService;
                this.teamMemberRepository = teamMemberRepository;
                this.customerOrderRepository = customerOrderRepository;
                this.customerRepository = customerRepository;
        }

        public void handleDeliveryMessage(TeamMember deliveryPerson, WhatsAppWebhookDto.Message message) {
                log.info("Processing delivery message from: {}", deliveryPerson.getName());

                BotSession session = sessionManager.getSession(deliveryPerson.getWaPhoneNumber());

                if (message.getType().equals("text")) {
                        String text = message.getText().getBody();
                        if (text.equalsIgnoreCase("hi") || text.equalsIgnoreCase("hello")) {
                                String welcomeMsg = deliveryPersonMessageService
                                                .getDeliveryPersonWelcomeMessage(deliveryPerson.getName());
                                whatsAppService.sendSimpleText(deliveryPerson.getWaPhoneNumber(), welcomeMsg);
                        } else {
                                whatsAppService.sendSimpleText(deliveryPerson.getWaPhoneNumber(),
                                                "Use buttons to interact with orders.");
                        }
                } else if (message.getType().equals("interactive")) {
                        if (message.getInteractive().getType().equals("button_reply")) {
                                handleButtonReply(deliveryPerson, session, message.getInteractive().getButtonReply());
                        }
                }

                session.setLastActiveAt(Instant.now());
        }

        private void handleButtonReply(TeamMember deliveryPerson, BotSession session,
                        WhatsAppWebhookDto.ButtonReply buttonReply) {
                String buttonId = buttonReply.getId();
                log.info("Delivery button reply: {}", buttonId);

                // Handle order confirmation (taking the order)
                if (buttonId.startsWith(FlowConstants.PREFIX_DELIVERY_TAKE)) {
                        Long orderId = Long.parseLong(buttonId.replace(FlowConstants.PREFIX_DELIVERY_TAKE, ""));
                        handleDeliveryConfirmation(deliveryPerson.getWaPhoneNumber(), orderId);
                        return;
                }

                // Live implementation of Shipped/Delivered Logic
                if (buttonId.startsWith(FlowConstants.PREFIX_SHIPPED)) {
                        Long orderId = Long.parseLong(buttonId.replace(FlowConstants.PREFIX_SHIPPED, ""));
                        // We don't have updateOrderStatus public in OrderService yet? OrderService
                        // usually has createOrder.
                        // Let's check OrderService. If not present, we will fix it.
                        // Assuming for now we can fetch and save.
                        updateOrderStatus(deliveryPerson, orderId, OrderStatus.CONFIRMED); // Wait, SHIPPED status
                                                                                           // needed?
                        // JDL only has PENDING, CONFIRMED, DELIVERED, CANCELLED.
                        // So "Shipped" might just be a notification without status change or we map to
                        // CONFIRMED (idempotent).

                        // Actually, let's just send a notification to customer that it's on the way.
                        notifyCustomerOrderShipped(orderId);
                        whatsAppService.sendSimpleText(deliveryPerson.getWaPhoneNumber(),
                                        "✅ Customer notified: Order is on the way!");

                } else if (buttonId.startsWith(FlowConstants.PREFIX_DELIVERED)) {
                        Long orderId = Long.parseLong(buttonId.replace(FlowConstants.PREFIX_DELIVERED, ""));
                        updateOrderStatus(deliveryPerson, orderId, OrderStatus.DELIVERED);
                }
        }

        private void updateOrderStatus(TeamMember deliveryPerson, Long orderId, OrderStatus newStatus) {
                CustomerOrder order = customerOrderRepository.findById(orderId)
                                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

                // Verify ownership?
                if (order.getDeliveryPerson() == null
                                || !order.getDeliveryPerson().getId().equals(deliveryPerson.getId())) {
                        whatsAppService.sendSimpleText(deliveryPerson.getWaPhoneNumber(),
                                        "⚠️ You are not assigned to this order.");
                        return;
                }

                order.setStatus(newStatus);
                customerOrderRepository.save(order);

                if (newStatus == OrderStatus.DELIVERED) {
                        whatsAppService.sendSimpleText(deliveryPerson.getWaPhoneNumber(),
                                        "✅ Order marked as Delivered! Great job!");
                        // Notify Customer
                        whatsAppService.sendSimpleText(order.getCustomer().getWaPhoneNumber(),
                                        "📦 *Order Delivered!*\n\nYour order has been delivered. Thank you for shopping with us! 🐟");
                }
        }

        private void notifyCustomerOrderShipped(Long orderId) {
                CustomerOrder order = customerOrderRepository.findById(orderId)
                                .orElseThrow(() -> new RuntimeException("Order not found"));
                whatsAppService.sendSimpleText(order.getCustomer().getWaPhoneNumber(),
                                "🚚 *Order On The Way!*\n\nYour order is out for delivery!");
        }

        /**
         * Send order notification to all active delivery persons individually
         */
        public void sendOrderToAllDeliveryPersons(CustomerOrder order, Customer customer,
                        List<CartItemDetailsDTO> items) {
                // Fetch all active delivery persons
                List<TeamMember> deliveryPersons = teamMemberRepository.findByRoleAndIsActive(UserRole.DELIVERY_PERSON,
                                true);

                if (deliveryPersons.isEmpty()) {
                        log.warn("No active delivery persons found!");
                        return;
                }

                StringBuilder orderDetails = new StringBuilder();
                orderDetails.append(deliveryPersonMessageService.getOrderNotificationHeader(order.getId()));
                orderDetails
                                .append(deliveryPersonMessageService.getCustomerDetails(customer.getName(),
                                                customer.getPhoneNumber()));

                // Always add location details, send "null" as string if data is not available
                String lat = customer.getLocationLat() != null ? String.format("%.5f", customer.getLocationLat())
                                : "null";
                String lon = customer.getLocationLon() != null ? String.format("%.5f", customer.getLocationLon())
                                : "null";
                String distance = customer.getDistanceFromBusinessKm() != null
                                ? String.format("%.2f", customer.getDistanceFromBusinessKm())
                                : "null";

                orderDetails.append(deliveryPersonMessageService.getLocationDetails(lat, lon, distance));

                orderDetails.append(deliveryPersonMessageService.getItemsHeader());
                for (CartItemDetailsDTO item : items) {
                        orderDetails.append(String.format("• %s - %.2f kg × ₹%.2f = ₹%.2f\n",
                                        item.getFishName(), item.getQuantityKg(), item.getPricePerKg(),
                                        item.getSubtotal()));
                }

                // Use BigDecimal for total
                orderDetails.append(deliveryPersonMessageService.getOrderFooter(order.getTotalAmount().doubleValue(),
                                order.getOrderTime().toString()));

                // Send to each delivery person individually
                for (TeamMember deliveryPerson : deliveryPersons) {
                        whatsAppService.sendInteractiveOrderAlert(
                                        deliveryPerson.getWaPhoneNumber(),
                                        orderDetails.toString(),
                                        order.getId());
                        log.info("Order {} sent to delivery person: {} ({})",
                                        order.getId(), deliveryPerson.getName(), deliveryPerson.getWaPhoneNumber());
                }

                log.info("Order {} sent to {} active delivery persons", order.getId(), deliveryPersons.size());
        }

        /**
         * Handle delivery person confirmation with role validation
         */
        public void handleDeliveryConfirmation(String deliveryPersonWaId, Long orderId) {
                // 1. Fetch order
                CustomerOrder order = customerOrderRepository.findById(orderId)
                                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

                // 2. Check if order already confirmed
                if (order.getStatus() == OrderStatus.CONFIRMED) {
                        String takenBy = order.getDeliveryPerson() != null ? order.getDeliveryPerson().getName()
                                        : "another delivery person";
                        whatsAppService.sendSimpleText(deliveryPersonWaId,
                                        deliveryPersonMessageService.getOrderAlreadyTaken(takenBy));
                        return;
                }

                // 3. Validate delivery person role
                Optional<TeamMember> teamMemberOpt = teamMemberRepository.findByWaPhoneNumber(deliveryPersonWaId);

                if (teamMemberOpt.isEmpty()) {
                        // User not registered in system
                        whatsAppService.sendUnauthorizedDeliveryMessage(deliveryPersonWaId);
                        log.warn("Unauthorized delivery confirmation attempt by unregistered user: {}",
                                        deliveryPersonWaId);
                        return;
                }

                TeamMember teamMember = teamMemberOpt.get();

                // Check if user has DELIVERY_PERSON role
                if (teamMember.getRole() != UserRole.DELIVERY_PERSON) {
                        whatsAppService.sendUnauthorizedDeliveryMessage(deliveryPersonWaId);
                        log.warn("Unauthorized delivery confirmation attempt by user {} with role: {}",
                                        deliveryPersonWaId, teamMember.getRole());
                        return;
                }

                // 4. Update order with delivery person details
                order.setStatus(OrderStatus.CONFIRMED);
                order.setDeliveryPerson(teamMember);
                order.setConfirmedAt(Instant.now());
                customerOrderRepository.save(order);

                // 5. Notify customer with delivery person details
                Customer customer = customerRepository.findById(order.getCustomer().getId())
                                .orElseThrow(() -> new RuntimeException("Customer not found"));

                whatsAppService.sendDeliveryAssignmentNotification(
                                customer.getWaPhoneNumber(),
                                teamMember.getName(),
                                teamMember.getWaPhoneNumber());

                // 6. Notify delivery person of successful confirmation
                whatsAppService.sendSimpleText(deliveryPersonWaId,
                                deliveryPersonMessageService.getOrderConfirmationSuccess(orderId, customer.getName(),
                                                customer.getPhoneNumber()));

                log.info("Order {} assigned to delivery person {} ({})",
                                orderId, teamMember.getName(), teamMember.getPhoneNumber());

                // 7. Send Dashboard Buttons to Delivery Person
                sendDeliveryDashboard(deliveryPersonWaId, orderId, customer);
        }

        private void sendDeliveryDashboard(String waId, Long orderId, Customer customer) {
                List<WhatsAppMessageDto.ButtonDto> buttons = List.of(
                                WhatsAppMessageDto.ButtonDto.builder()
                                                .type("reply")
                                                .reply(WhatsAppMessageDto.ReplyDto.builder()
                                                                .id(FlowConstants.PREFIX_SHIPPED + orderId)
                                                                .title("Mark Shipped")
                                                                .build())
                                                .build(),
                                WhatsAppMessageDto.ButtonDto.builder()
                                                .type("reply")
                                                .reply(WhatsAppMessageDto.ReplyDto.builder()
                                                                .id(FlowConstants.PREFIX_DELIVERED + orderId)
                                                                .title("Mark Delivered")
                                                                .build())
                                                .build());

                String dashboardMsg = String.format("🚀 *Delivery Actions*\n\nOrder #%d for %s\n\nUpdate status:",
                                orderId, customer.getName());

                whatsAppService.sendCartActionButtons(waId, dashboardMsg, buttons);
        }
}
