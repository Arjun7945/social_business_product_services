package com.aps.service;

import com.aps.config.FlowConstants;
import com.aps.domain.Customer;
import com.aps.domain.CustomerOrder;
import com.aps.domain.DeliveryPerson;
import com.aps.domain.TeamMember;
import com.aps.domain.enumeration.OrderStatus;
import com.aps.domain.enumeration.UserRole;
import com.aps.repository.CustomerOrderRepository;
import com.aps.repository.CustomerRepository;
import com.aps.repository.DeliveryPersonRepository;
import com.aps.repository.TeamMemberRepository;
import com.aps.service.dto.WhatsAppMessageDto;
// import com.aps.service.payment.RazorpayPaymentStrategy; // Removed
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreditCustomerFlowService {

        private final Logger log = LoggerFactory.getLogger(CreditCustomerFlowService.class);

        private final WhatsAppService whatsAppService;
        private final CustomerOrderRepository customerOrderRepository;
        private final CustomerRepository customerRepository;
        private final TeamMemberRepository teamMemberRepository;
        private final DeliveryPersonRepository deliveryPersonRepository;
        private final DeliveryPersonMessageService deliveryPersonMessageService;
        private final OrderStatusHistoryService orderStatusHistoryService;
        private final CustomerMessageService customerMessageService;
        private final com.aps.service.payment.RazorpayService razorpayService;

        // Constants for Credit Flow
        public static final String PREFIX_PAY_RESISTED = "PAY_RESISTED_";
        public static final String PREFIX_ADMIN_CREDIT_ALLOW = "CREDIT_ALLOW_";
        public static final String PREFIX_ADMIN_CREDIT_GRANT = "CREDIT_GRANT_";
        public static final String PREFIX_ADMIN_CREDIT_DENY = "CREDIT_DENY_";

        public static final String MENU_CREDIT_OC = "CREDIT_CUST_ORDERS";
        public static final String MENU_CREDIT_CRUD = "CREDIT_CUST_CRUD";

        public static final String PREFIX_CREDIT_ORDER_DTL = "CREDIT_ORD_DTL_";
        public static final String PREFIX_CREDIT_PAY_LINK = "CREDIT_PAY_LINK_";
        public static final String PREFIX_CREDIT_COD = "CREDIT_COD_";

        public CreditCustomerFlowService(
                        @Lazy WhatsAppService whatsAppService,
                        CustomerOrderRepository customerOrderRepository,
                        CustomerRepository customerRepository,
                        TeamMemberRepository teamMemberRepository,
                        DeliveryPersonRepository deliveryPersonRepository,
                        DeliveryPersonMessageService deliveryPersonMessageService,
                        OrderStatusHistoryService orderStatusHistoryService,
                        CustomerMessageService customerMessageService,
                        com.aps.service.payment.RazorpayService razorpayService) {
                this.whatsAppService = whatsAppService;
                this.customerOrderRepository = customerOrderRepository;
                this.customerRepository = customerRepository;
                this.teamMemberRepository = teamMemberRepository;
                this.deliveryPersonRepository = deliveryPersonRepository;
                this.deliveryPersonMessageService = deliveryPersonMessageService;
                this.orderStatusHistoryService = orderStatusHistoryService;
                this.customerMessageService = customerMessageService;
                this.razorpayService = razorpayService;
        }

        // ==========================================
        // DELIVERY PERSON FLOW: PAYMENT RESISTED
        // ==========================================

        public void handlePaymentResisted(DeliveryPerson deliveryPerson, Long orderId) {
                log.info("handlePaymentResisted: Enter for Order ID: {}", orderId);
                CustomerOrder order = customerOrderRepository.findById(orderId).orElse(null);
                if (order == null) {
                        log.error("handlePaymentResisted: Order not found: {}", orderId);
                        return;
                }

                Customer customer = order.getCustomer();

                // Notify All Admins
                List<TeamMember> admins = teamMemberRepository.findAllByRole(UserRole.ADMIN);
                log.info("handlePaymentResisted: Found {} admins to notify", admins.size());

                String adminMsg = deliveryPersonMessageService.getPaymentResistedAdminInfo(
                                deliveryPerson.getName(),
                                customer.getName(),
                                customer.getRole().name(),
                                order.getTotalAmount().doubleValue(),
                                orderId);

                List<WhatsAppMessageDto.ButtonDto> buttons = List.of(
                                WhatsAppMessageDto.ButtonDto.builder().type("reply")
                                                .reply(WhatsAppMessageDto.ReplyDto.builder()
                                                                .id(PREFIX_ADMIN_CREDIT_ALLOW + orderId + "_"
                                                                                + deliveryPerson.getId())
                                                                .title(deliveryPersonMessageService
                                                                                .getButtonAllowCreditOnce())
                                                                .build())
                                                .build(),
                                WhatsAppMessageDto.ButtonDto.builder().type("reply")
                                                .reply(WhatsAppMessageDto.ReplyDto.builder()
                                                                .id(PREFIX_ADMIN_CREDIT_GRANT + orderId + "_"
                                                                                + deliveryPerson.getId())
                                                                .title(deliveryPersonMessageService
                                                                                .getButtonGrantAlways())
                                                                .build())
                                                .build(),
                                WhatsAppMessageDto.ButtonDto.builder().type("reply")
                                                .reply(WhatsAppMessageDto.ReplyDto.builder()
                                                                .id(PREFIX_ADMIN_CREDIT_DENY + orderId + "_"
                                                                                + deliveryPerson.getId())
                                                                .title(deliveryPersonMessageService
                                                                                .getButtonDenyCredit())
                                                                .build())
                                                .build());

                for (TeamMember admin : admins) {
                        log.info("handlePaymentResisted: Notifying Admin: {}", admin.getWaPhoneNumber());
                        whatsAppService.sendInteractiveButtons(admin.getWaPhoneNumber(), adminMsg, buttons);
                }

                // Notify DP to wait
                log.info("handlePaymentResisted: Notifying DP to wait");
                whatsAppService.sendSimpleText(deliveryPerson.getWaPhoneNumber(),
                                deliveryPersonMessageService.getAdminNotification());
        }

        // ==========================================
        // ADMIN FLOW: HANDLE DECISION
        // ==========================================

        public void handleAdminDecision(TeamMember admin, String buttonId) {
                String prefix;
                if (buttonId.startsWith(PREFIX_ADMIN_CREDIT_ALLOW))
                        prefix = PREFIX_ADMIN_CREDIT_ALLOW;
                else if (buttonId.startsWith(PREFIX_ADMIN_CREDIT_GRANT))
                        prefix = PREFIX_ADMIN_CREDIT_GRANT;
                else if (buttonId.startsWith(PREFIX_ADMIN_CREDIT_DENY))
                        prefix = PREFIX_ADMIN_CREDIT_DENY;
                else
                        return;

                String payload = buttonId.substring(prefix.length());
                String[] parts = payload.split("_");
                if (parts.length < 2)
                        return;

                Long orderId = Long.parseLong(parts[0]);
                Long dpId = Long.parseLong(parts[1]);

                CustomerOrder order = customerOrderRepository.findById(orderId).orElse(null);
                DeliveryPerson dp = deliveryPersonRepository.findById(dpId).orElse(null);

                if (order == null || dp == null) {
                        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                                        "❌ Error: Order or Delivery Person not found.");
                        return;
                }

                Customer customer = order.getCustomer();

                if (prefix.equals(PREFIX_ADMIN_CREDIT_GRANT)) {
                        // GRANT ALWAYS
                        customer.setRole(UserRole.CREDIT_CUSTOMER);
                        customerRepository.save(customer);

                        updateOrderToCredit(order);

                        // Notify DP
                        whatsAppService.sendSimpleText(dp.getWaPhoneNumber(),
                                        deliveryPersonMessageService.getCreditGrantedAlwaysMessage(customer.getName(),
                                                        orderId));

                        // Notify Admin
                        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                                        "✅ Approved: Always Grant for " + customer.getName());

                        // Notify Customer
                        try {
                                whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                                                customerMessageService
                                                                .getMessageCreditPrivilegesGranted(customer.getName()));
                        } catch (Exception e) {
                                log.error("Failed to notify customer about credit grant", e);
                        }

                } else if (prefix.equals(PREFIX_ADMIN_CREDIT_ALLOW)) {
                        // ALLOW ONCE
                        updateOrderToCredit(order);

                        // Notify DP
                        whatsAppService.sendSimpleText(dp.getWaPhoneNumber(),
                                        deliveryPersonMessageService.getCreditApprovedOnceMessage(customer.getName(),
                                                        orderId));

                        // Notify Admin
                        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                                        "✅ Approved: Only for this Order" + orderId);

                } else {
                        // DENY
                        // Notify DP
                        whatsAppService.sendSimpleText(dp.getWaPhoneNumber(),
                                        deliveryPersonMessageService.getCreditDeniedMessage(customer.getName(),
                                                        orderId));

                        // Notify Admin
                        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                                        "❌ Denied Credit for Order " + orderId);
                }
        }

        private void updateOrderToCredit(CustomerOrder order) {
                order.setStatus(OrderStatus.ON_CREDIT_PURCHASE);
                customerOrderRepository.save(order);
                orderStatusHistoryService.addEvent(order);
        }

        // ==========================================
        // ADMIN DASHBOARD: CREDIT CUSTOMER MENU
        // ==========================================

        public void showCreditCustomerMenu(TeamMember admin) {
                // Flattened Menu: No more nested CRUD menu
                List<WhatsAppMessageDto.RowDto> rows = com.aps.service.util.CreditCustomerMenuHelper
                                .getCreditCustomerMenuRows();
                whatsAppService.sendInteractiveList(admin.getWaPhoneNumber(), "💳 *Credit Customer Management*", rows);
        }

        public void showCreditCustomerOrders(TeamMember admin) {
                // FIXED: Use findAllByStatus
                List<CustomerOrder> orders = customerOrderRepository.findAllByStatus(OrderStatus.ON_CREDIT_PURCHASE);

                if (orders.isEmpty()) {
                        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                                        "No credit customer orders found till now.");
                        return;
                }

                List<WhatsAppMessageDto.RowDto> rows = new java.util.ArrayList<>();
                for (CustomerOrder order : orders) {
                        rows.add(WhatsAppMessageDto.RowDto.builder()
                                        .id(PREFIX_CREDIT_ORDER_DTL + order.getId())
                                        .title(order.getCustomer().getName())
                                        .description(order.getCustomer().getWaPhoneNumber())
                                        .build());
                }

                rows.add(WhatsAppMessageDto.RowDto.builder().id("CREDIT_MENU").title("⬅️ Back")
                                .description("Credit Menu")
                                .build());

                whatsAppService.sendInteractiveList(admin.getWaPhoneNumber(), "📦 *Credit Customer Orders*", rows);
        }

        public void handleCreditOrderSelection(TeamMember admin, Long orderId) {
                CustomerOrder order = customerOrderRepository.findById(orderId).orElse(null);
                if (order == null)
                        return;

                String details = String.format(
                                "📦 *Order Details*\n" +
                                                "ID: %d\n" +
                                                "Name: %s\n" +
                                                "Amount: ₹%.2f\n" +
                                                "Status: %s\n",
                                order.getId(), order.getCustomer().getName(), order.getTotalAmount(),
                                order.getStatus());

                List<WhatsAppMessageDto.ButtonDto> buttons = List.of(
                                WhatsAppMessageDto.ButtonDto.builder().type("reply")
                                                .reply(WhatsAppMessageDto.ReplyDto.builder()
                                                                .id(PREFIX_CREDIT_PAY_LINK + orderId)
                                                                .title("🔗 Send Link").build())
                                                .build(),
                                WhatsAppMessageDto.ButtonDto.builder().type("reply")
                                                .reply(WhatsAppMessageDto.ReplyDto.builder()
                                                                .id(PREFIX_CREDIT_COD + orderId)
                                                                .title("💵 Marked COD").build())
                                                .build(),
                                WhatsAppMessageDto.ButtonDto.builder().type("reply")
                                                .reply(WhatsAppMessageDto.ReplyDto.builder().id(MENU_CREDIT_OC)
                                                                .title("⬅️ Back").build())
                                                .build());

                whatsAppService.sendInteractiveButtons(admin.getWaPhoneNumber(), details, buttons);
        }

        public void handleCreditOrderAction(TeamMember admin, String buttonId) {
                if (buttonId.startsWith(PREFIX_CREDIT_PAY_LINK)) {
                        Long orderId = Long.parseLong(buttonId.replace(PREFIX_CREDIT_PAY_LINK, ""));
                        CustomerOrder order = customerOrderRepository.findById(orderId).orElse(null);
                        if (order != null) {

                                String link = razorpayService.createPaymentLink(order.getId(),
                                                order.getTotalAmount().doubleValue(),
                                                order.getCustomer());
                                if (link == null) {
                                        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                                                        "❌ Error: Failed to generate Razorpay link.");
                                        return;
                                }

                                whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                                                "✅ Payment link generated and sent.");

                                whatsAppService.sendSimpleText(order.getCustomer().getWaPhoneNumber(),
                                                String.format("Hi %s, payment link for order %d from %s: %s",
                                                                order.getCustomer().getName(), orderId, admin.getName(),
                                                                link));
                        }
                } else if (buttonId.startsWith(PREFIX_CREDIT_COD)) {
                        Long orderId = Long.parseLong(buttonId.replace(PREFIX_CREDIT_COD, ""));
                        CustomerOrder order = customerOrderRepository.findById(orderId).orElse(null);
                        if (order != null) {
                                order.setStatus(OrderStatus.ORDER_DELIVERED_SUCESSFULLY);
                                order.setPaymentMethod("COD");
                                customerOrderRepository.save(order);
                                orderStatusHistoryService.addEvent(order);
                                whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "✅ Marked as COD Delivered.");
                        }
                }
        }
}
