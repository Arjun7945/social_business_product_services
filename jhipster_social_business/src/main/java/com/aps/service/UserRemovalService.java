package com.aps.service;

import com.aps.domain.Customer;
import com.aps.domain.CustomerOrder;
import com.aps.domain.DeliveryPerson;
import com.aps.domain.RemovedOrderSummary;
import com.aps.domain.RemovedUser;
import com.aps.domain.TeamMember;
import com.aps.domain.enumeration.AccountStatus;
import com.aps.repository.*;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for handling the safe removal and archival of users (Customers,
 * TeamMembers, and DeliveryPersons).
 */
@Service
@Transactional
public class UserRemovalService {

    private static final int MAX_REASON_LENGTH = 255;

    private final Logger log = LoggerFactory.getLogger(UserRemovalService.class);

    private final CustomerRepository customerRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final DeliveryPersonRepository deliveryPersonRepository;
    private final CustomerOrderRepository customerOrderRepository;
    private final RemovedUserRepository removedUserRepository;
    private final RemovedOrderSummaryRepository removedOrderSummaryRepository;
    private final CustomerFlowService customerFlowService;
    private final ShoppingCartRepository shoppingCartRepository;
    private final ReturnedOrderRepository returnedOrderRepository;
    private final DeliveryZoneRepository deliveryZoneRepository;

    public UserRemovalService(
            CustomerRepository customerRepository,
            TeamMemberRepository teamMemberRepository,
            DeliveryPersonRepository deliveryPersonRepository,
            CustomerOrderRepository customerOrderRepository,
            RemovedUserRepository removedUserRepository,
            RemovedOrderSummaryRepository removedOrderSummaryRepository,
            CustomerFlowService customerFlowService,
            ShoppingCartRepository shoppingCartRepository,
            ReturnedOrderRepository returnedOrderRepository,
            DeliveryZoneRepository deliveryZoneRepository) {
        this.customerRepository = customerRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.deliveryPersonRepository = deliveryPersonRepository;
        this.customerOrderRepository = customerOrderRepository;
        this.removedUserRepository = removedUserRepository;
        this.removedOrderSummaryRepository = removedOrderSummaryRepository;
        this.customerFlowService = customerFlowService;
        this.shoppingCartRepository = shoppingCartRepository;
        this.returnedOrderRepository = returnedOrderRepository;
        this.deliveryZoneRepository = deliveryZoneRepository;
    }

    /**
     * Archive and delete a Customer.
     */
    public void removeCustomer(Long id, String reason) {
        log.info("Request to remove Customer : {}", id);
        try {
            // 0. Pessimistic Lock to prevent concurrent orders/updates
            Customer customer = customerRepository
                    .findByIdForUpdate(id)
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + id));

            // 1. Calculate Stats (Placed / Spent) - Optimized DB Query
            List<Object[]> stats = customerOrderRepository.getCustomerStats(
                    id,
                    com.aps.domain.enumeration.OrderStatus.ORDER_DELIVERED_SUCESSFULLY);

            long totalOrders = 0;
            double totalAmount = 0.0;

            if (!stats.isEmpty()) {
                Object[] row = stats.get(0);
                if (row[0] != null)
                    totalOrders = (Long) row[0];
                if (row[1] != null)
                    totalAmount = ((java.math.BigDecimal) row[1]).doubleValue(); // Fix: SUM returns BigDecimal
            }

            RemovedOrderSummary summary = new RemovedOrderSummary().userName(customer.getName())
                    .totalAmount(totalAmount);

            summary.setUserOriginalId(customer.getId());
            summary.setUserRole(com.aps.domain.enumeration.UserRole.CUSTOMER);
            summary.setTotalOrders((int) totalOrders);
            summary.setFirstInteractionAt(customer.getJoinedAt());
            summary.setLastInteractionAt(customer.getLastInteractionAt());
            summary.setRemovedAt(Instant.now());

            summary = removedOrderSummaryRepository.save(summary);

            // 2. Archive User
            String safeReason = reason != null && reason.length() > MAX_REASON_LENGTH
                    ? reason.substring(0, MAX_REASON_LENGTH)
                    : reason;

            // Logic for 'addedBy'
            String addedByStr = "SELF";
            if (customer.getAddedBy() != null) {
                addedByStr = customer.getAddedBy().getName();
            }

            // Update Summary addedBy
            summary.setAddedBy(addedByStr);
            removedOrderSummaryRepository.save(summary);

            RemovedUser removedUser = new RemovedUser()
                    .name(customer.getName())
                    .addedBy(addedByStr)
                    .role(com.aps.domain.enumeration.UserRole.CUSTOMER)
                    .status(AccountStatus.ACCOUNT_REMOVED);

            removedUser.setOriginalId(customer.getId());
            removedUser.setWhatsappNumber(customer.getWaPhoneNumber());
            removedUser.setPhoneNumber(customer.getPhoneNumber());
            removedUser.setAddress(customer.getAddress());
            removedUser.setLocationLat(customer.getLocationLat());
            removedUser.setLocationLon(customer.getLocationLon());
            removedUser.setDistanceFromBusinessKm(customer.getDistanceFromBusinessKm());
            removedUser.setIsPincodeValid(customer.getIsPincodeValid());
            removedUser.setJoinedAt(customer.getJoinedAt());
            removedUser.setRemovedAt(Instant.now());
            removedUser.setReasonForRemoval(safeReason);
            removedUser.setOrderHistoryId(summary.getId());

            if (customer.getZone() != null) {
                removedUser.setZoneId(customer.getZone().getId());
                removedUser.setZoneName(customer.getZone().getZoneName());
            }

            // Capture session data
            try {
                String sessionData = customerFlowService.getSessionDataForArchival(customer.getWaPhoneNumber());
                removedUser.setLastSessionData(sessionData);
            } catch (Exception e) {
                log.warn("Failed to archive session data for customer {}", id, e);
            }

            removedUser = removedUserRepository.save(removedUser);

            // 3. Link Transfer - BULK UPDATE
            customerOrderRepository.unlinkCustomer(customer.getId(), removedUser.getId());
            returnedOrderRepository.unlinkCustomer(customer.getId(), removedUser.getId());

            // 4. Delete Shopping Cart
            shoppingCartRepository.findByCustomerId(customer.getId()).ifPresent(shoppingCartRepository::delete);

            // 5. Delete Original
            customerRepository.delete(customer);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            log.error("Data integrity violation removing Customer {}", id, e);
            throw new IllegalStateException(
                    "Cannot remove customer. Ensure all pending live orders are completed or cancelled first.");
        } catch (Exception e) {
            log.error("Unexpected error removing Customer {}", id, e);
            throw new RuntimeException("System error during customer removal: " + e.getMessage());
        }
    }

    /**
     * Archive and delete a Delivery Person.
     */
    public void removeDeliveryPerson(Long id, String reason) {
        log.info("Request to remove Delivery Person : {}", id);
        DeliveryPerson member = deliveryPersonRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("DeliveryPerson not found with id: " + id));

        // 0. Safeguard: Check for Active Orders
        List<com.aps.domain.enumeration.OrderStatus> activeStatuses = java.util.Arrays
                .stream(com.aps.domain.enumeration.OrderStatus.values())
                .filter(s -> s != com.aps.domain.enumeration.OrderStatus.ORDER_DELIVERED_SUCESSFULLY
                        && s != com.aps.domain.enumeration.OrderStatus.ORDER_FAILED
                        && s != com.aps.domain.enumeration.OrderStatus.ORDER_NOT_TAKEN)
                .collect(java.util.stream.Collectors.toList());

        List<com.aps.domain.CustomerOrder> activeOrders = customerOrderRepository
                .findAllByDeliveryPersonIdAndStatusIn(id, activeStatuses);

        if (!activeOrders.isEmpty()) {
            long activeCount = activeOrders.size();
            double activeAmount = activeOrders.stream()
                    .mapToDouble(o -> o.getTotalAmount() != null ? o.getTotalAmount().doubleValue() : 0.0)
                    .sum();

            String orderIds = activeOrders.stream()
                    .map(o -> String.valueOf(o.getId()))
                    .collect(java.util.stream.Collectors.joining(", "));

            throw new IllegalStateException(String.format(
                    "Delivery Person %s has %d pending orders to complete. Please complete or reassign. Orders: [%s]. Total Amount: \u20B9%.2f.",
                    member.getName(), activeCount, orderIds, activeAmount));
        }

        // 1. Calculate Stats & Archive User - optimized DB stats
        // DB Aggregation for performance
        List<Object[]> stats = customerOrderRepository.findStatsByDeliveryPersonIdAndStatus(
                id,
                List.of(com.aps.domain.enumeration.OrderStatus.ORDER_DELIVERED_SUCESSFULLY));

        long totalOrders = 0;
        double totalAmount = 0.0;
        Instant firstInteraction = null;
        Instant lastInteraction = null;

        if (!stats.isEmpty()) {
            Object[] row = stats.get(0);
            if (row[0] != null)
                totalOrders = (Long) row[0];
            if (row[1] != null)
                totalAmount = ((java.math.BigDecimal) row[1]).doubleValue();
            if (row[2] != null)
                firstInteraction = (Instant) row[2];
            if (row[3] != null)
                lastInteraction = (Instant) row[3];
        }

        RemovedOrderSummary summary = new RemovedOrderSummary()
                .userName(member.getName())
                .totalOrders((int) totalOrders)
                .totalAmount(totalAmount);

        summary.setUserOriginalId(member.getId());
        summary.setUserRole(com.aps.domain.enumeration.UserRole.DELIVERY_PERSON);
        summary.setRemovedAt(Instant.now());
        summary.setFirstInteractionAt(firstInteraction);
        summary.setLastInteractionAt(lastInteraction);

        summary = removedOrderSummaryRepository.save(summary);

        // Logic for 'addedBy'
        String addedByStr = "SELF";
        if (member.getAddedBy() != null) {
            addedByStr = member.getAddedBy().getName();
        }

        // Update Summary addedBy
        summary.setAddedBy(addedByStr);
        removedOrderSummaryRepository.save(summary);

        RemovedUser removedUser = new RemovedUser().name(member.getName())
                .role(com.aps.domain.enumeration.UserRole.DELIVERY_PERSON).status(AccountStatus.ACCOUNT_REMOVED);

        removedUser.setOriginalId(member.getId());
        removedUser.setWhatsappNumber(member.getWaPhoneNumber());
        removedUser.setPhoneNumber(member.getPhoneNumber());
        removedUser.setAddedBy(addedByStr);
        removedUser.setJoinedAt(member.getJoinedAt());
        removedUser.setRemovedAt(Instant.now());
        removedUser.setReasonForRemoval(reason);
        removedUser.setOrderHistoryId(summary.getId());

        if (member.getZone() != null) {
            removedUser.setZoneId(member.getZone().getId());
            removedUser.setZoneName(member.getZone().getZoneName());
        }

        // Capture session data
        try {
            String sessionData = customerFlowService.getSessionDataForArchival(member.getWaPhoneNumber());
            removedUser.setLastSessionData(sessionData);
        } catch (Exception e) {
            log.warn("Failed to archive session data for delivery person {}", id, e);
        }

        removedUser = removedUserRepository.save(removedUser);

        // 3. Link Transfer - BULK UPDATE
        customerOrderRepository.unlinkDeliveryPerson(member.getId(), removedUser.getId());

        // 4. Delete Original
        deliveryPersonRepository.delete(member);
    }

    /**
     * Restore a removed user (Customer, TeamMember, or DeliveryPerson) from the
     * archive.
     */
    public Long restoreUser(Long removedUserId) {
        log.info("Request to restore RemovedUser : {}", removedUserId);
        RemovedUser removedUser = removedUserRepository
                .findById(removedUserId)
                .orElseThrow(() -> new IllegalArgumentException("RemovedUser not found with id: " + removedUserId));

        if (removedUser.getStatus() == AccountStatus.RESTORE_ACCOUNT) {
            throw new IllegalStateException("User is already restored.");
        }

        // Check if WhatsApp number OR Phone Number is already in use
        String whatsappNumber = removedUser.getWhatsappNumber();
        String phoneNumber = removedUser.getPhoneNumber();

        if (whatsappNumber != null) {
            if (customerRepository.existsByWaPhoneNumber(whatsappNumber) ||
                    teamMemberRepository.existsByWaPhoneNumber(whatsappNumber) ||
                    deliveryPersonRepository.findByWaPhoneNumber(whatsappNumber).isPresent()) {
                throw new IllegalStateException(
                        "Cannot restore: WhatsApp number " + whatsappNumber + " is actively used by another account.");
            }
        }
        if (phoneNumber != null) {
            // Note: deliveryPersonRepository might need existsByPhoneNumber or just
            // findByPhoneNumber?
            // Assuming uniqueness constraint check.
            // Using logic: if any match via findAll/stream or repo method.
            // For now assuming repo has it or I skip explicit DP check if not available, OR
            // rely on exception.
            // But better safe:
            if (customerRepository.existsByPhoneNumber(phoneNumber)
                    || teamMemberRepository.existsByPhoneNumber(phoneNumber)) {
                throw new IllegalStateException(
                        "Cannot restore: Phone number " + phoneNumber + " is actively used by another account.");
            }
        }

        Long newId = null;

        if (removedUser.getRole() == com.aps.domain.enumeration.UserRole.CUSTOMER) {
            // Restore Customer
            Customer newCustomer = new Customer();
            newCustomer.setName(removedUser.getName());
            newCustomer.setWaPhoneNumber(removedUser.getWhatsappNumber());
            newCustomer.setPhoneNumber(removedUser.getPhoneNumber());
            newCustomer.setAddress(removedUser.getAddress());
            newCustomer.setLocationLat(removedUser.getLocationLat());
            newCustomer.setLocationLon(removedUser.getLocationLon());
            newCustomer.setRole(com.aps.domain.enumeration.UserRole.CUSTOMER);
            newCustomer.setDistanceFromBusinessKm(removedUser.getDistanceFromBusinessKm());
            newCustomer.setIsPincodeValid(true);
            newCustomer.setJoinedAt(removedUser.getJoinedAt());
            newCustomer.setLastInteractionAt(Instant.now());

            if (removedUser.getZoneId() != null) {
                deliveryZoneRepository.findById(removedUser.getZoneId()).ifPresent(newCustomer::setZone);
            }

            newCustomer = customerRepository.save(newCustomer);
            newId = newCustomer.getId();

            // Re-link Orders
            List<CustomerOrder> orders = customerOrderRepository.findAllByRemovedCustomerId(removedUserId);
            for (CustomerOrder order : orders) {
                order.setCustomer(newCustomer);
                order.setRemovedCustomerId(null);
                customerOrderRepository.save(order);
            }

            // Re-link Returned Orders
            List<com.aps.domain.ReturnedOrder> returnedOrders = returnedOrderRepository
                    .findAllByRemovedCustomerId(removedUserId);
            for (com.aps.domain.ReturnedOrder order : returnedOrders) {
                order.setCustomer(newCustomer);
                order.setRemovedCustomerId(null);
                returnedOrderRepository.save(order);
            }

            // Restore session data
            if (removedUser.getLastSessionData() != null) {
                try {
                    customerFlowService.restoreSessionData(newCustomer.getWaPhoneNumber(),
                            removedUser.getLastSessionData());
                } catch (Exception e) {
                    log.warn("Failed to restore session data for user {}: {}", newId, e.getMessage());
                }
            }
        } else if (removedUser.getRole() == com.aps.domain.enumeration.UserRole.DELIVERY_PERSON) {
            // Restore Delivery Person
            if (removedUser.getPhoneNumber() == null) {
                throw new IllegalStateException("Cannot restore DeliveryPerson: Archived phone number is missing.");
            }

            DeliveryPerson newMember = new DeliveryPerson();
            newMember.setName(removedUser.getName());
            newMember.setWaPhoneNumber(removedUser.getWhatsappNumber());
            newMember.setPhoneNumber(removedUser.getPhoneNumber());
            newMember.setIsActive(true);
            newMember.setStatus(com.aps.domain.enumeration.DeliveryStatus.FREE);
            newMember.setJoinedAt(removedUser.getJoinedAt());

            if (removedUser.getAddedBy() != null && !"SELF".equalsIgnoreCase(removedUser.getAddedBy())) {
                teamMemberRepository.findByName(removedUser.getAddedBy()).ifPresent(newMember::setAddedBy);
            }

            if (removedUser.getZoneId() != null) {
                deliveryZoneRepository.findById(removedUser.getZoneId()).ifPresent(newMember::setZone);
            }

            newMember = deliveryPersonRepository.save(newMember);
            newId = newMember.getId();

            // Re-link Orders
            List<CustomerOrder> orders = customerOrderRepository.findAllByRemovedDeliveryPersonId(removedUserId);
            for (CustomerOrder order : orders) {
                order.setDeliveryPerson(newMember);
                order.setRemovedDeliveryPersonId(null);
                customerOrderRepository.save(order);
            }

        } else {
            // Restore TeamMember (Executive, Admin, etc.)
            if (removedUser.getPhoneNumber() == null) {
                throw new IllegalStateException("Cannot restore TeamMember: Archived phone number is missing.");
            }

            TeamMember newMember = new TeamMember();
            newMember.setName(removedUser.getName());
            newMember.setWaPhoneNumber(removedUser.getWhatsappNumber());
            newMember.setPhoneNumber(removedUser.getPhoneNumber());
            newMember.setRole(removedUser.getRole());
            newMember.setIsActive(true);

            newMember = teamMemberRepository.save(newMember);
            newId = newMember.getId();

            // Note: TeamMembers don't have direct orders, so no re-linking needed here
            // usually.
        }

        // Cleanup Archive
        Long orderHistoryId = removedUser.getOrderHistoryId();
        removedUserRepository.delete(removedUser);

        if (orderHistoryId != null) {
            removedOrderSummaryRepository.deleteById(orderHistoryId);
        }

        log.info("Successfully restored RemovedUser {} to new Entity ID {}", removedUserId, newId);
        return newId;
    }
}
