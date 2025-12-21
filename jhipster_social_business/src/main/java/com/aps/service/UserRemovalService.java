package com.aps.service;

import com.aps.domain.Customer;
import com.aps.domain.CustomerOrder;
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
 * Service for handling the safe removal and archival of users (Customers and
 * TeamMembers).
 * <p>
 * This service ensures:
 * 1. Financial data integrity is preserved via {@link RemovedOrderSummary}.
 * 2. Historical links are maintained via {@link CustomerOrder} updates.
 * 3. Atomic deletion and archival using {@link Transactional}.
 * </p>
 */
@Service
@Transactional
public class UserRemovalService {

    private static final int MAX_REASON_LENGTH = 255;

    private final Logger log = LoggerFactory.getLogger(UserRemovalService.class);

    private final CustomerRepository customerRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final CustomerOrderRepository customerOrderRepository;
    private final RemovedUserRepository removedUserRepository;
    private final RemovedOrderSummaryRepository removedOrderSummaryRepository;
    private final CustomerFlowService customerFlowService;
    private final ShoppingCartRepository shoppingCartRepository;

    public UserRemovalService(
            CustomerRepository customerRepository,
            TeamMemberRepository teamMemberRepository,
            CustomerOrderRepository customerOrderRepository,
            RemovedUserRepository removedUserRepository,
            RemovedOrderSummaryRepository removedOrderSummaryRepository,
            CustomerFlowService customerFlowService,
            ShoppingCartRepository shoppingCartRepository) {
        this.customerRepository = customerRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.customerOrderRepository = customerOrderRepository;
        this.removedUserRepository = removedUserRepository;
        this.removedOrderSummaryRepository = removedOrderSummaryRepository;
        this.customerFlowService = customerFlowService;
        this.shoppingCartRepository = shoppingCartRepository;
    }

    /**
     * Archive and delete a Customer.
     *
     * @param id     The ID of the customer to remove.
     * @param reason The reason for removal.
     */
    /**
     * Archive and delete a Customer.
     *
     * @param id     The ID of the customer to remove.
     * @param reason The reason for removal.
     */
    public void removeCustomer(Long id, String reason) {
        log.info("Request to remove Customer : {}", id);
        try {
            // 0. Pessimistic Lock to prevent concurrent orders/updates
            Customer customer = customerRepository.findByIdForUpdate(id)
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + id));

            // 1. Calculate Stats (Placed / Spent) - Optimized DB Query
            List<Object[]> stats = customerOrderRepository.getCustomerStats(id,
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

            RemovedOrderSummary summary = new RemovedOrderSummary()
                    .userName(customer.getName())
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

            RemovedUser removedUser = new RemovedUser()
                    .name(customer.getName())
                    .role(com.aps.domain.enumeration.UserRole.CUSTOMER)
                    .status(AccountStatus.ACCOUNT_REMOVED);

            removedUser.setOriginalId(customer.getId());
            removedUser.setWhatsappNumber(customer.getWaPhoneNumber());
            removedUser.setPhoneNumber(customer.getPhoneNumber());
            removedUser.setAddress(customer.getAddress());
            removedUser.setLocationLat(customer.getLocationLat());
            removedUser.setLocationLon(customer.getLocationLon());
            removedUser.setJoinedAt(customer.getJoinedAt());
            removedUser.setRemovedAt(Instant.now());
            removedUser.setReasonForRemoval(safeReason);
            removedUser.setOrderHistoryId(summary.getId());

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
     * Archive and delete a Delivery Person (Team Member).
     *
     * @param id     The ID of the team member to remove.
     * @param reason The reason for removal.
     */
    public void removeDeliveryPerson(Long id, String reason) {
        log.info("Request to remove Delivery Person : {}", id);
        TeamMember member = teamMemberRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("TeamMember not found with id: " + id));

        // 1. Calculate Stats & Archive User - optimized DB stats
        // DB Aggregation for performance
        List<Object[]> stats = customerOrderRepository.findStatsByDeliveryPersonIdAndStatus(id,
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
        summary.setUserRole(member.getRole());
        summary.setRemovedAt(Instant.now());
        summary.setFirstInteractionAt(firstInteraction);
        summary.setLastInteractionAt(lastInteraction);

        summary = removedOrderSummaryRepository.save(summary);

        RemovedUser removedUser = new RemovedUser()
                .name(member.getName())
                .role(member.getRole())
                .status(AccountStatus.ACCOUNT_REMOVED);

        removedUser.setOriginalId(member.getId());
        removedUser.setWhatsappNumber(member.getWaPhoneNumber());
        removedUser.setPhoneNumber(member.getPhoneNumber());
        removedUser.setRemovedAt(Instant.now());
        removedUser.setReasonForRemoval(reason);
        removedUser.setOrderHistoryId(summary.getId());

        removedUser = removedUserRepository.save(removedUser);

        // 3. Link Transfer - BULK UPDATE
        customerOrderRepository.unlinkDeliveryPerson(member.getId(), removedUser.getId());

        // 4. Delete Original
        teamMemberRepository.delete(member);
    }

    /**
     * Restore a removed user (Customer or TeamMember) from the archive.
     *
     * @param removedUserId The ID of the removed user record to restore.
     * @return The ID of the newly created active user.
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
            if (customerRepository.existsByWaPhoneNumber(whatsappNumber)
                    || teamMemberRepository.existsByWaPhoneNumber(whatsappNumber)) {
                throw new IllegalStateException("Cannot restore: WhatsApp number " + whatsappNumber
                        + " is actively used by another account.");
            }
        }
        if (phoneNumber != null) {
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
            newCustomer.setIsPincodeValid(true);
            newCustomer.setJoinedAt(removedUser.getJoinedAt()); // Preserving original join date
            newCustomer.setLastInteractionAt(Instant.now());

            newCustomer = customerRepository.save(newCustomer);
            newId = newCustomer.getId();

            // Re-link Orders
            List<CustomerOrder> orders = customerOrderRepository.findAllByRemovedCustomerId(removedUserId);
            for (CustomerOrder order : orders) {
                order.setCustomer(newCustomer);
                order.setRemovedCustomerId(null);
                customerOrderRepository.save(order);
            }

            // Restore session data
            if (removedUser.getLastSessionData() != null) {
                try {
                    customerFlowService.restoreSessionData(newCustomer.getWaPhoneNumber(),
                            removedUser.getLastSessionData());
                } catch (Exception e) {
                    // Non-critical error, log and continue. session data is secondary to account
                    // access.
                    log.warn("Failed to restore session data for user {}: {}", newId, e.getMessage());
                }
            }

        } else {
            // Restore TeamMember
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

            // Re-link Orders
            List<CustomerOrder> orders = customerOrderRepository.findAllByRemovedDeliveryPersonId(removedUserId);
            for (CustomerOrder order : orders) {
                order.setDeliveryPerson(newMember);
                order.setRemovedDeliveryPersonId(null);
                customerOrderRepository.save(order);
            }
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
