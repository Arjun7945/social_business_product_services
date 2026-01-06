package com.aps.service;

import com.aps.config.FlowConstants;
import com.aps.domain.BotSession;
import com.aps.domain.Customer;
import com.aps.domain.CustomerOrder;
import com.aps.domain.DeliveryPerson;
import com.aps.domain.enumeration.OrderStatus;
import com.aps.repository.CustomerOrderRepository;
import com.aps.repository.CustomerRepository;
import com.aps.repository.DeliveryPersonRepository;
import com.aps.repository.DeliveryZoneRepository;
import com.aps.service.dto.CartItemDetailsDTO;
import com.aps.service.dto.WhatsAppMessageDto;
import com.aps.service.dto.WhatsAppWebhookDto;
import com.aps.service.payment.PaymentStrategy;
import com.aps.service.payment.PaymentStrategyFactory;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@Transactional
public class DeliveryFlowService {

    private final Logger log = LoggerFactory.getLogger(DeliveryFlowService.class);

    private final WhatsAppService whatsAppService;
    private final BotSessionManager sessionManager;
    private final DeliveryPersonMessageService deliveryPersonMessageService;
    private final CustomerMessageService customerMessageService;
    private final CustomerFlowService customerFlowService;
    private final PaymentStrategyFactory paymentStrategyFactory;
    private final DeliveryPersonRepository deliveryPersonRepository;
    private final CustomerOrderRepository customerOrderRepository;
    private final CustomerRepository customerRepository;
    private final OrderStatusHistoryService orderStatusHistoryService;
    private final DeliveryZoneRepository deliveryZoneRepository;
    private final DeliveryPersonService deliveryPersonService;

    public DeliveryFlowService(
            @Lazy WhatsAppService whatsAppService,
            BotSessionManager sessionManager,
            DeliveryPersonMessageService deliveryPersonMessageService,
            CustomerMessageService customerMessageService,
            CustomerFlowService customerFlowService,
            PaymentStrategyFactory paymentStrategyFactory,
            DeliveryPersonRepository deliveryPersonRepository,
            CustomerOrderRepository customerOrderRepository,
            CustomerRepository customerRepository,
            OrderStatusHistoryService orderStatusHistoryService,
            DeliveryZoneRepository deliveryZoneRepository,
            DeliveryPersonService deliveryPersonService) {
        this.whatsAppService = whatsAppService;
        this.sessionManager = sessionManager;
        this.deliveryPersonMessageService = deliveryPersonMessageService;
        this.customerMessageService = customerMessageService;
        this.customerFlowService = customerFlowService;
        this.paymentStrategyFactory = paymentStrategyFactory;
        this.deliveryPersonRepository = deliveryPersonRepository;
        this.customerOrderRepository = customerOrderRepository;
        this.customerRepository = customerRepository;
        this.orderStatusHistoryService = orderStatusHistoryService;
        this.deliveryZoneRepository = deliveryZoneRepository;
        this.deliveryPersonService = deliveryPersonService;
    }

    public void handleDeliveryMessage(DeliveryPerson deliveryPerson, WhatsAppWebhookDto.Message message) {
        log.info("Processing delivery message from: {}", deliveryPerson.getName());
        BotSession session = sessionManager.getSession(deliveryPerson.getWaPhoneNumber());

        if (message.getType().equals("text")) {
            // For any text message, show the Main Menu
            sendMainMenu(deliveryPerson);
        } else if (message.getType().equals("interactive")) {
            if (message.getInteractive().getType().equals("button_reply")) {
                handleButtonReply(deliveryPerson, session, message.getInteractive().getButtonReply());
            } else if (message.getInteractive().getType().equals("list_reply")) {
                handleListReply(deliveryPerson, session, message.getInteractive().getListReply());
            }
        }
        session.setLastActiveAt(Instant.now());
    }

    private void sendMainMenu(DeliveryPerson deliveryPerson) {
        List<WhatsAppMessageDto.ButtonDto> buttons = List.of(
                WhatsAppMessageDto.ButtonDto.builder().type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder().id(FlowConstants.DELIVERY_MENU_ORDER_TAKEN)
                                .title(deliveryPersonMessageService.getMenuOptionOrderTaken()).build())
                        .build(),
                WhatsAppMessageDto.ButtonDto.builder().type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder().id(FlowConstants.DELIVERY_MENU_PROFILE)
                                .title(deliveryPersonMessageService.getMenuOptionProfile()).build())
                        .build());
        whatsAppService.sendInteractiveButtons(deliveryPerson.getWaPhoneNumber(),
                deliveryPersonMessageService.getMenuGreeting(deliveryPerson.getName()), buttons);
    }

    private void handleButtonReply(DeliveryPerson deliveryPerson, BotSession session,
            WhatsAppWebhookDto.ButtonReply buttonReply) {
        String buttonId = buttonReply.getId();
        log.info("Delivery button reply: {}", buttonId);

        if (buttonId.equals(FlowConstants.DELIVERY_MENU_ORDER_TAKEN)) {
            handleOrderTakenList(deliveryPerson);
        } else if (buttonId.equals(FlowConstants.DELIVERY_MENU_PROFILE)) {
            handleProfile(deliveryPerson);
        } else if (buttonId.equals(FlowConstants.DELIVERY_PROFILE_UPDATE_STATUS)) {
            handleUpdateStatusMenu(deliveryPerson);
        } else if (buttonId.equals(FlowConstants.DELIVERY_PROFILE_UPDATE_ZONE)) {
            handleUpdateZoneMenu(deliveryPerson);
        } else if (buttonId.equals(FlowConstants.DELIVERY_PROFILE_MENU)) {
            sendMainMenu(deliveryPerson);
        } else if (buttonId.startsWith(FlowConstants.PREFIX_DELIVERY_TAKE)) {
            // Taking an order (Initial accept)
            Long orderId = Long.parseLong(buttonId.replace(FlowConstants.PREFIX_DELIVERY_TAKE, ""));
            handleDeliveryConfirmation(deliveryPerson.getWaPhoneNumber(), orderId);
        } else if (buttonId.startsWith(FlowConstants.PREFIX_DELIVERY_DETAILS)) {
            // Viewed details (3 options) -> Show options again or just the list?
            // Task says: "when order id clicked... send 3 options".
            // Implementation: List Reply ID -> handleListReply -> Show Details Options
            // But if they are buttons (e.g. from Order Taken list if implemented as
            // buttons), handles here.
            // Task says: "list should be in list view buttons format". If > 3 items, must
            // use List Message. If <= 3, Buttons.
            // I'll assume List Message for robustness.
            // Wait, "button names should be order id". If List Reply, it comes as
            // handleListReply.
            // If Button Reply, here.

            // Re-use logic for showing order details (Payment, Location, etc)
            Long orderId = Long.parseLong(buttonId.replace(FlowConstants.PREFIX_DELIVERY_DETAILS, ""));
            sendOrderInteractionOptions(deliveryPerson, orderId);
        } else if (buttonId.startsWith(FlowConstants.PREFIX_UPDATE_STATUS)) {
            String status = buttonId.replace(FlowConstants.PREFIX_UPDATE_STATUS, "");
            updateDeliveryPersonStatus(deliveryPerson, status);
        }
        // ... Payment and Shipped logic ...
        else if (buttonId.startsWith("PAY_")) {
            handlePaymentModeSelection(deliveryPerson, session, buttonId);
        } else if (buttonId.startsWith(FlowConstants.PREFIX_SHIPPED)) {
            Long orderId = Long.parseLong(buttonId.replace(FlowConstants.PREFIX_SHIPPED, ""));
            updateOrderStatus(deliveryPerson, orderId, OrderStatus.DELIVERY_ONWAY);
            notifyCustomerOrderShipped(orderId);
            whatsAppService.sendSimpleText(deliveryPerson.getWaPhoneNumber(),
                    deliveryPersonMessageService.getOrderShippedSuccess());
        } else if (buttonId.startsWith(FlowConstants.PREFIX_DELIVERED)) {
            Long orderId = Long.parseLong(buttonId.replace(FlowConstants.PREFIX_DELIVERED, ""));
            updateOrderStatus(deliveryPerson, orderId, OrderStatus.ORDER_DELIVERED_SUCESSFULLY);
        }
    }

    private void handleListReply(DeliveryPerson deliveryPerson, BotSession session,
            WhatsAppWebhookDto.ListReply listReply) {
        String id = listReply.getId();
        if (id.startsWith(FlowConstants.PREFIX_DELIVERY_DETAILS)) {
            Long orderId = Long.parseLong(id.replace(FlowConstants.PREFIX_DELIVERY_DETAILS, ""));
            sendOrderInteractionOptions(deliveryPerson, orderId);
        } else if (id.startsWith(FlowConstants.PREFIX_UPDATE_ZONE)) {
            Long zoneId = Long.parseLong(id.replace(FlowConstants.PREFIX_UPDATE_ZONE, ""));
            updateDeliveryPersonZone(deliveryPerson, zoneId);
        } else if (id.equals(FlowConstants.PREFIX_MAIN_MENU)) {
            sendMainMenu(deliveryPerson);
        }
    }

    private void handleOrderTakenList(DeliveryPerson deliveryPerson) {
        String chosenOrders = deliveryPerson.getChosenOrder();
        if (chosenOrders == null || chosenOrders.isEmpty()) {
            whatsAppService.sendSimpleText(deliveryPerson.getWaPhoneNumber(),
                    deliveryPersonMessageService.getNoOrdersTaken());
            sendMainMenu(deliveryPerson);
            return;
        }

        String[] ids = chosenOrders.split(",");
        // Send List Message
        // "list view buttons format" -> WhatsApp List Message if > 3 options or
        // buttons.
        // Task says "button names should be order id".
        // I will use Interactive List Message for scalability.

        List<WhatsAppMessageDto.RowDto> rows = new java.util.ArrayList<>();
        for (String idStr : ids) {
            if (idStr.trim().isEmpty())
                continue;
            rows.add(WhatsAppMessageDto.RowDto.builder()
                    .id(FlowConstants.PREFIX_DELIVERY_DETAILS + idStr.trim())
                    .title(deliveryPersonMessageService.getTitleOfOrderNumber() + idStr.trim())
                    .description(deliveryPersonMessageService.getViewDetailsOfOrders())
                    .build());
            rows.add(WhatsAppMessageDto.RowDto.builder()
                    .id(FlowConstants.PREFIX_MAIN_MENU)
                    .title(deliveryPersonMessageService.getTitleOfOGoBack())
                    .description(deliveryPersonMessageService.getBackToTheMainMenu())
                    .build());
        }

        if (rows.isEmpty()) {
            whatsAppService.sendSimpleText(deliveryPerson.getWaPhoneNumber(),
                    deliveryPersonMessageService.getNoOrdersTaken());
            return;
        }

        whatsAppService.sendInteractiveList(deliveryPerson.getWaPhoneNumber(),
                deliveryPersonMessageService.getOrderTakenListHeader(),
                deliveryPersonMessageService.getOrderViewButtonLabal(), rows);
    }

    private void sendOrderInteractionOptions(DeliveryPerson deliveryPerson, Long orderId) {
        CustomerOrder order = customerOrderRepository.findById(orderId).orElse(null);
        if (order == null)
            return;

        // Send confirmation/details message (Location, Payment options etc)
        // Re-using exiting logic:
        Customer customer = order.getCustomer();

        // 1. Details msg
        whatsAppService.sendSimpleText(deliveryPerson.getWaPhoneNumber(), deliveryPersonMessageService
                .getOrderConfirmationSuccess(orderId, customer.getName(), customer.getPhoneNumber()));

        // 2. Location
        if (customer.getLocationLat() != null && customer.getLocationLon() != null) {
            whatsAppService.sendLocation(deliveryPerson.getWaPhoneNumber(), customer.getLocationLat(),
                    customer.getLocationLon(), customer.getName(), customer.getAddress());
        }

        // 3. Payment Options (Buttons)
        sendPaymentModeSelection(deliveryPerson.getWaPhoneNumber(), orderId, order.getTotalAmount().doubleValue());
    }

    private void handleProfile(DeliveryPerson deliveryPerson) {
        int orderCount = 0;
        if (deliveryPerson.getChosenOrder() != null && !deliveryPerson.getChosenOrder().isEmpty()) {
            orderCount = deliveryPerson.getChosenOrder().split(",").length;
        }

        String details = deliveryPersonMessageService.getProfileDetails(
                deliveryPerson.getName(),
                deliveryPerson.getPhoneNumber(),
                deliveryPerson.getStatus() != null ? deliveryPerson.getStatus().name() : "N/A",
                deliveryPerson.getZone() != null ? deliveryPerson.getZone().getZoneName() : "N/A",
                orderCount);

        List<WhatsAppMessageDto.ButtonDto> buttons = List.of(
                WhatsAppMessageDto.ButtonDto.builder().type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder().id(FlowConstants.DELIVERY_PROFILE_UPDATE_STATUS)
                                .title(deliveryPersonMessageService.getButtonUpdateStatus()).build())
                        .build(),
                WhatsAppMessageDto.ButtonDto.builder().type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder().id(FlowConstants.DELIVERY_PROFILE_UPDATE_ZONE)
                                .title(deliveryPersonMessageService.getButtonUpdateZone()).build())
                        .build(),
                WhatsAppMessageDto.ButtonDto.builder().type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder().id(FlowConstants.DELIVERY_PROFILE_MENU)
                                .title(deliveryPersonMessageService.getButtonGoToMenu()).build())
                        .build());

        whatsAppService.sendInteractiveButtons(deliveryPerson.getWaPhoneNumber(), details, buttons);
    }

    private void handleUpdateStatusMenu(DeliveryPerson deliveryPerson) {
        // Enum values to Buttons. 3 options: FREE, BUSY, OFF_DUTY
        List<WhatsAppMessageDto.ButtonDto> buttons = new java.util.ArrayList<>();
        for (com.aps.domain.enumeration.DeliveryStatus status : com.aps.domain.enumeration.DeliveryStatus.values()) {
            buttons.add(WhatsAppMessageDto.ButtonDto
                    .builder().type("reply").reply(WhatsAppMessageDto.ReplyDto.builder()
                            .id(FlowConstants.PREFIX_UPDATE_STATUS + status.name()).title(status.name()).build())
                    .build());
        }
        whatsAppService.sendInteractiveButtons(deliveryPerson.getWaPhoneNumber(),
                deliveryPersonMessageService.getUpdateStatusHeader(), buttons);
    }

    private void updateDeliveryPersonStatus(DeliveryPerson deliveryPerson, String statusStr) {
        try {
            com.aps.domain.enumeration.DeliveryStatus status = com.aps.domain.enumeration.DeliveryStatus
                    .valueOf(statusStr);

            // Validation: Cannot switch to FREE if orders are pending
            if (status == com.aps.domain.enumeration.DeliveryStatus.FREE) {
                String chosenOrders = deliveryPerson.getChosenOrder();
                if (chosenOrders != null && !chosenOrders.isEmpty()) {
                    // Send Warning
                    whatsAppService.sendSimpleText(deliveryPerson.getWaPhoneNumber(),
                            deliveryPersonMessageService.getStatusUpdateFailedPendingOrders());

                    // Show Available Orders
                    handleOrderTakenList(deliveryPerson);

                    // Send Main Menu (as requested)
                    // sendMainMenu(deliveryPerson);
                    // return;
                }
            }

            deliveryPerson.setStatus(status);
            deliveryPersonRepository.save(deliveryPerson);
            whatsAppService.sendSimpleText(deliveryPerson.getWaPhoneNumber(),
                    deliveryPersonMessageService.getUpdateSuccess());
            handleProfile(deliveryPerson); // Go back to Profile
        } catch (Exception e) {
            log.error("Invalid status update: {}", statusStr);
        }
    }

    private void handleUpdateZoneMenu(DeliveryPerson deliveryPerson) {
        List<com.aps.domain.DeliveryZone> zones = deliveryZoneRepository.findAll();
        List<WhatsAppMessageDto.RowDto> rows = new java.util.ArrayList<>();
        for (com.aps.domain.DeliveryZone zone : zones) {
            rows.add(WhatsAppMessageDto.RowDto.builder().id(FlowConstants.PREFIX_UPDATE_ZONE + zone.getId())
                    .title(zone.getZoneName()).description("Select this zone").build());
        }
        whatsAppService.sendInteractiveList(deliveryPerson.getWaPhoneNumber(),
                deliveryPersonMessageService.getUpdateZoneHeader(), "Select Zone", rows);
    }

    private void updateDeliveryPersonZone(DeliveryPerson deliveryPerson, Long zoneId) {
        deliveryZoneRepository.findById(zoneId).ifPresent(zone -> {
            deliveryPerson.setZone(zone);
            deliveryPersonRepository.save(deliveryPerson);
            whatsAppService.sendSimpleText(deliveryPerson.getWaPhoneNumber(),
                    deliveryPersonMessageService.getUpdateSuccess());
            handleProfile(deliveryPerson);
        });
    }

    // ... Payment Logic (Kept mostly same, just helper method used above) ...
    private void handlePaymentModeSelection(DeliveryPerson deliveryPerson, BotSession session, String buttonId) {
        Long orderId = null;
        String mode = null;

        if (buttonId.startsWith(FlowConstants.PREFIX_PAY_COD)) {
            orderId = Long.parseLong(buttonId.replace(FlowConstants.PREFIX_PAY_COD, ""));
            mode = "COD";
        } else if (buttonId.startsWith(FlowConstants.PREFIX_PAY_QR)) {
            orderId = Long.parseLong(buttonId.replace(FlowConstants.PREFIX_PAY_QR, ""));
            mode = "QR";
        } else if (buttonId.startsWith(FlowConstants.PREFIX_PAY_LINK)) {
            orderId = Long.parseLong(buttonId.replace(FlowConstants.PREFIX_PAY_LINK, ""));
            mode = "LINK";
        }

        if (orderId == null || mode == null)
            return;

        CustomerOrder order = customerOrderRepository.findById(orderId).orElse(null);
        if (order == null)
            return;

        // Strategy Pattern Execution
        PaymentStrategy strategy = paymentStrategyFactory.getStrategy(mode);
        if (strategy != null) {
            order.setPaymentMethod(mode);
            customerOrderRepository.save(order);
            orderStatusHistoryService.addEvent(order);
            strategy.initiatePayment(order, deliveryPerson);
            if ("COD".equals(mode)) {
                updateOrderStatus(deliveryPerson, orderId, OrderStatus.ORDER_DELIVERED_SUCESSFULLY);
            }
        } else {
            whatsAppService.sendSimpleText(deliveryPerson.getWaPhoneNumber(), "⚠️ Invalid payment mode selected.");
        }
    }

    private void updateOrderStatus(DeliveryPerson deliveryPerson, Long orderId, OrderStatus newStatus) {
        // ... Existing logic ...
        CustomerOrder order = customerOrderRepository
                .findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        // Verify ownership (Check if order ID is in chosenOrder list OR mapped by
        // relationship)
        // Task says "order id will be added to the delivery person table... assignments
        // cannot be taken by others"
        // Also original logic checked db relationship.
        // We should stick to DB relationship for security, assuming assignment sets it.

        if (order.getDeliveryPerson() == null || !order.getDeliveryPerson().getId().equals(deliveryPerson.getId())) {
            whatsAppService.sendSimpleText(deliveryPerson.getWaPhoneNumber(),
                    deliveryPersonMessageService.getOrderNotAssignedWarning());
            return;
        }

        if (order.getStatus() != newStatus) {
            order.setStatus(newStatus);
            order.setConfirmedAt(Instant.now());
            customerOrderRepository.save(order);
            orderStatusHistoryService.addEvent(order);
        }

        if (newStatus == OrderStatus.ORDER_DELIVERED_SUCESSFULLY) {
            // Remove from chosen_order list
            deliveryPersonService.removeOrderFromChosenList(deliveryPerson.getId(), orderId);

            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            whatsAppService.sendSimpleText(
                                    deliveryPerson.getWaPhoneNumber(),
                                    deliveryPersonMessageService.getOrderDeliveredSuccess());
                            whatsAppService.sendSimpleText(
                                    order.getCustomer().getWaPhoneNumber(),
                                    customerMessageService.getOrderDeliveredMessage());
                            customerFlowService.sendReOrderFlow(order.getCustomer());
                        }
                    });
        }
    }

    // ... handleDeliveryConfirmation (Modified for Task 2.1) ...
    public void handleDeliveryConfirmation(String deliveryPersonWaId, Long orderId) {
        CustomerOrder order = customerOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        Optional<DeliveryPerson> deliveryPersonOpt = deliveryPersonRepository.findByWaPhoneNumber(deliveryPersonWaId);

        if (deliveryPersonOpt.isEmpty()) {
            whatsAppService.sendUnauthorizedDeliveryMessage(deliveryPersonWaId);
            return;
        }

        DeliveryPerson deliveryPerson = deliveryPersonOpt.get();

        // 1. LIMIT CHECK
        String currentChosen = deliveryPerson.getChosenOrder();
        int currentCount = (currentChosen == null || currentChosen.isEmpty()) ? 0 : currentChosen.split(",").length;
        if (currentCount >= deliveryPerson.getChosenOrderLimit()) {
            whatsAppService.sendSimpleText(deliveryPersonWaId,
                    deliveryPersonMessageService.getOrderLimitReachedError());
            sendMainMenu(deliveryPerson);
            return;
        }

        // 2. STATUS CHECK
        if (order.getStatus() != OrderStatus.ORDER_NOT_TAKEN) {
            whatsAppService.sendSimpleText(deliveryPersonWaId,
                    deliveryPersonMessageService.getOrderAlreadyTaken(orderId, order.getStatus().toString()));
            sendMainMenu(deliveryPerson);
            return;
        }

        // 3. ASSIGN
        order.setStatus(OrderStatus.DELIVERY_ONWAY);
        order.setDeliveryPerson(deliveryPerson);
        order.setConfirmedAt(Instant.now());
        customerOrderRepository.save(order);
        orderStatusHistoryService.addEvent(order);

        // 4. UPDATE CHOSEN_ORDER COLUMN
        String newChosen = (currentChosen == null || currentChosen.isEmpty()) ? String.valueOf(orderId)
                : currentChosen + "," + orderId;
        deliveryPerson.setChosenOrder(newChosen);

        // Auto-set status to BUSY if limit reached
        if (newChosen.split(",").length >= deliveryPerson.getChosenOrderLimit()) {
            deliveryPerson.setStatus(com.aps.domain.enumeration.DeliveryStatus.BUSY);

            // Notify Delivery Person about status change
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    whatsAppService.sendSimpleText(deliveryPersonWaId,
                            deliveryPersonMessageService.getLimitReachedAndBusyMessage());
                }
            });
        }

        deliveryPersonRepository.save(deliveryPerson);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                // 5. NOTIFY CUSTOMER
                Customer customer = customerRepository.findById(order.getCustomer().getId()).orElseThrow();
                whatsAppService.sendDeliveryAssignmentNotification(customer.getWaPhoneNumber(),
                        deliveryPerson.getName(), deliveryPerson.getWaPhoneNumber());

                // 6. NOTIFY DELIVERY PERSON (Simple Success Message as per Task)
                whatsAppService.sendSimpleText(deliveryPersonWaId,
                        deliveryPersonMessageService.getOrderAcceptedSimpleSuccess());

                // Do NOT send details immediately. They must click "Order Taken" -> ID.
                sendMainMenu(deliveryPerson);
            }
        });
    }

    // ... notifyCustomerOrderShipped ...
    private void notifyCustomerOrderShipped(Long orderId) {
        CustomerOrder order = customerOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        whatsAppService.sendSimpleText(order.getCustomer().getWaPhoneNumber(),
                customerMessageService.getOrderShippedMessage());
    }

    // ... sendOrderToAllDeliveryPersons (Keep as is) ...
    public void sendOrderToAllDeliveryPersons(CustomerOrder order, Customer customer, List<CartItemDetailsDTO> items) {
        // Keep existing logic
        List<DeliveryPerson> deliveryPersons = deliveryPersonRepository.findByIsActive(true);
        if (deliveryPersons.isEmpty())
            return;

        StringBuilder orderDetails = new StringBuilder();
        orderDetails.append(deliveryPersonMessageService.getOrderNotificationHeader(order.getId()));
        orderDetails
                .append(deliveryPersonMessageService.getCustomerDetails(customer.getName(), customer.getPhoneNumber()));
        orderDetails.append(deliveryPersonMessageService.getItemsHeader());
        for (CartItemDetailsDTO item : items) {
            orderDetails.append(String.format("• %s - %.2f kg × ₹%.2f = ₹%.2f\n", item.getFishName(),
                    item.getQuantityKg(), item.getPricePerKg(), item.getSubtotal()));
        }
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter
                .ofPattern("dd-MM-yyyy, hh:mm a").withZone(java.time.ZoneId.systemDefault());
        String formattedTime = formatter.format(order.getOrderTime());
        orderDetails.append(
                deliveryPersonMessageService.getOrderFooter(order.getTotalAmount().doubleValue(), formattedTime));

        for (DeliveryPerson deliveryPerson : deliveryPersons) {
            whatsAppService.sendInteractiveOrderAlert(deliveryPerson.getWaPhoneNumber(), orderDetails.toString(),
                    order.getId());
        }
    }

    // ... sendPaymentModeSelection (Helper) ...
    private void sendPaymentModeSelection(String waId, Long orderId, double amount) {
        List<WhatsAppMessageDto.ButtonDto> buttons = List.of(
                WhatsAppMessageDto.ButtonDto.builder().type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder().id(FlowConstants.PREFIX_PAY_COD + orderId)
                                .title(deliveryPersonMessageService.getButtonCod()).build())
                        .build(),
                WhatsAppMessageDto.ButtonDto.builder().type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder().id(FlowConstants.PREFIX_PAY_QR + orderId)
                                .title(deliveryPersonMessageService.getButtonQr()).build())
                        .build(),
                WhatsAppMessageDto.ButtonDto.builder().type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder().id(FlowConstants.PREFIX_PAY_LINK + orderId)
                                .title(deliveryPersonMessageService.getButtonLink()).build())
                        .build());

        String message = deliveryPersonMessageService.getPaymentModeSelectionHeader(orderId, amount);
        whatsAppService.sendCartActionButtons(waId, message, buttons);
    }
}
