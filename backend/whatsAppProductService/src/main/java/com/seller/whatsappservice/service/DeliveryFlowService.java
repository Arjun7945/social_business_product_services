package com.seller.whatsappservice.service;

import com.seller.whatsappservice.dto.CartItemDto;
import com.seller.whatsappservice.model.Customer;
import com.seller.whatsappservice.model.CustomerOrder;
import com.seller.whatsappservice.model.TeamMember;
import com.seller.whatsappservice.model.enums.OrderStatus;
import com.seller.whatsappservice.model.enums.UserRole;
import com.seller.whatsappservice.repository.CustomerOrderRepository;
import com.seller.whatsappservice.repository.CustomerRepository;
import com.seller.whatsappservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service to handle delivery person operations and order assignment flow
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryFlowService {

        private final TeamMemberRepository teamMemberRepository;
        private final CustomerOrderRepository customerOrderRepository;
        private final CustomerRepository customerRepository;
        private final WhatsAppService whatsAppService;
        private final DeliveryPersonMessageService messageService;

        /**
         * Send order notification to all active delivery persons individually
         */
        public void sendOrderToAllDeliveryPersons(CustomerOrder order, Customer customer, List<CartItemDto> items) {
                // Fetch all active delivery persons
                List<TeamMember> deliveryPersons = teamMemberRepository.findByRoleAndIsActive(UserRole.DELIVERY_PERSON,
                                true);

                if (deliveryPersons.isEmpty()) {
                        log.warn("No active delivery persons found!");
                        return;
                }

                StringBuilder orderDetails = new StringBuilder();
                orderDetails.append(messageService.getOrderNotificationHeader(order.getId()));
                orderDetails.append(messageService.getCustomerDetails(customer.getName(), customer.getPhoneNumber()));

                // Always add location details, send "null" as string if data is not available
                String lat = customer.getLocationLat() != null ? String.format("%.5f", customer.getLocationLat())
                                : "null";
                String lon = customer.getLocationLon() != null ? String.format("%.5f", customer.getLocationLon())
                                : "null";
                String distance = customer.getDistanceFromBusinessKm() != null
                                ? String.format("%.2f", customer.getDistanceFromBusinessKm())
                                : "null";

                orderDetails.append(messageService.getLocationDetails(lat, lon, distance));

                orderDetails.append(messageService.getItemsHeader());
                for (CartItemDto item : items) {
                        orderDetails.append(String.format("• %s - %.2f kg × ₹%.2f = ₹%.2f\n",
                                        item.getFishName(), item.getQuantityKg(), item.getPricePerKg(),
                                        item.getSubtotal()));
                }

                orderDetails.append(messageService.getOrderFooter(order.getTotalAmount(),
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
        @Transactional
        public void handleDeliveryConfirmation(String deliveryPersonWaId, Long orderId) {
                // 1. Fetch order
                CustomerOrder order = customerOrderRepository.findById(orderId)
                                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

                // 2. Check if order already confirmed
                if (order.getStatus() == OrderStatus.CONFIRMED) {
                        whatsAppService.sendSimpleText(deliveryPersonWaId,
                                        messageService.getOrderAlreadyTaken(order.getTeamMemberName()));
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
                order.setTeamMemberId(teamMember.getId());
                order.setTeamMemberWaId(deliveryPersonWaId);
                order.setTeamMemberName(teamMember.getName());
                order.setConfirmedAt(LocalDateTime.now());
                customerOrderRepository.save(order);

                // 5. Notify customer with delivery person details
                Customer customer = customerRepository.findById(order.getCustomerId())
                                .orElseThrow(() -> new RuntimeException("Customer not found"));

                whatsAppService.sendDeliveryAssignmentNotification(
                                customer.getWaPhoneNumber(),
                                teamMember.getName(),
                                teamMember.getWaPhoneNumber());

                // 6. Notify delivery person of successful confirmation
                whatsAppService.sendSimpleText(deliveryPersonWaId,
                                messageService.getOrderConfirmationSuccess(orderId, customer.getName(),
                                                customer.getPhoneNumber()));

                log.info("Order {} assigned to delivery person {} ({})",
                                orderId, teamMember.getName(), teamMember.getPhoneNumber());
        }
}
