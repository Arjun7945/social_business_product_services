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
    private final CreditCustomerFlowService creditCustomerFlowService; // Injected

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
            DeliveryPersonService deliveryPersonService,
            CreditCustomerFlowService creditCustomerFlowService) { // Injected
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
        this.creditCustomerFlowService = creditCustomerFlowService;
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
            // Viewed details (3 options)
            Long detailOrderId = Long.parseLong(buttonId.replace(FlowConstants.PREFIX_DELIVERY_DETAILS, ""));
            sendOrderInteractionOptions(deliveryPerson, detailOrderId);
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
        } else if (id.startsWith("PAY_")) {
            handlePaymentModeSelection(deliveryPerson, session, id);
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
        List<WhatsAppMessageDto.RowDto> rows = new java.util.ArrayList<>();
        for (String idStr : ids) {
            if (idStr.trim().isEmpty())
                continue;
            rows.add(WhatsAppMessageDto.RowDto.builder()
                    .id(FlowConstants.PREFIX_DELIVERY_DETAILS + idStr.trim())
                    .title(deliveryPersonMessageService.getTitleOfOrderNumber() + idStr.trim())
                    .description(deliveryPersonMessageService.getViewDetailsOfOrders())
                    .build());
        }

        // Add Back button once at the end
        rows.add(WhatsAppMessageDto.RowDto.builder()
                .id(FlowConstants.PREFIX_MAIN_MENU)
                .title(deliveryPersonMessageService.getTitleOfOGoBack())
                .description(deliveryPersonMessageService.getBackToTheMainMenu())
                .build());

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
        String customerName = (customer != null) ? customer.getName() : "Unknown (Deleted)";
        String customerPhone = (customer != null) ? customer.getPhoneNumber() : "N/A";

        // 1. Details msg
        whatsAppService.sendSimpleText(deliveryPerson.getWaPhoneNumber(), deliveryPersonMessageService
                .getOrderConfirmationSuccess(orderId, customerName, customerPhone));

        // 2. Location
        if (customer != null && customer.getLocationLat() != null && customer.getLocationLon() != null) {
            whatsAppService.sendLocation(deliveryPerson.getWaPhoneNumber(), customer.getLocationLat(),
                    customer.getLocationLon(), customerName, customer.getAddress());
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
                    return;
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
    // ... Payment Logic ...

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
        } else if (buttonId.startsWith(CreditCustomerFlowService.PREFIX_PAY_RESISTED)) {
            orderId = Long.parseLong(buttonId.replace(CreditCustomerFlowService.PREFIX_PAY_RESISTED, ""));
            // Delegate to CreditCustomerFlowService
            creditCustomerFlowService.handlePaymentResisted(deliveryPerson, orderId);
            return;
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
            whatsAppService.sendSimpleText(deliveryPerson.getWaPhoneNumber(),
                    deliveryPersonMessageService.getInvalidModeSelectedWarningMessage());
        }
    }

    // ... sendPaymentModeSelection (Modified to List View) ...
    private void sendPaymentModeSelection(String waId, Long orderId, double amount) {
        List<WhatsAppMessageDto.RowDto> rows = List.of(
                WhatsAppMessageDto.RowDto.builder()
                        .id(FlowConstants.PREFIX_PAY_COD + orderId)
                        .title(deliveryPersonMessageService.getButtonCod())
                        .description(deliveryPersonMessageService.getButtonCodDescription()).build(),
                WhatsAppMessageDto.RowDto.builder()
                        .id(FlowConstants.PREFIX_PAY_QR + orderId)
                        .title(deliveryPersonMessageService.getButtonQr())
                        .description(deliveryPersonMessageService.getButtonQrDescription()).build(),
                WhatsAppMessageDto.RowDto.builder()
                        .id(FlowConstants.PREFIX_PAY_LINK + orderId)
                        .title(deliveryPersonMessageService.getButtonLink())
                        .description(deliveryPersonMessageService.getButtonLinkDescription()).build(),
                WhatsAppMessageDto.RowDto.builder()
                        .id(CreditCustomerFlowService.PREFIX_PAY_RESISTED + orderId)
                        .title(deliveryPersonMessageService.getButtonPaymentResisted())
                        .description(deliveryPersonMessageService.getDescriptionPaymentResisted()).build());

        String message = deliveryPersonMessageService.getPaymentModeSelectionHeader(orderId, amount);
        whatsAppService.sendInteractiveList(waId, message, deliveryPersonMessageService.getButtonName(), rows);
    }

    private void updateOrderStatus(DeliveryPerson deliveryPerson, Long orderId, OrderStatus newStatus) {
        CustomerOrder order = customerOrderRepository
                .findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

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

    public void handleDeliveryConfirmation(String deliveryPersonWaId, Long orderId) {
        CustomerOrder order = customerOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        Optional<DeliveryPerson> deliveryPersonOpt = deliveryPersonRepository.findByWaPhoneNumber(deliveryPersonWaId);

        if (deliveryPersonOpt.isEmpty()) {
            whatsAppService.sendUnauthorizedDeliveryMessage(deliveryPersonWaId);
            return;
        }

        DeliveryPerson deliveryPerson = deliveryPersonOpt.get();

        String currentChosen = deliveryPerson.getChosenOrder();
        int currentCount = (currentChosen == null || currentChosen.isEmpty()) ? 0 : currentChosen.split(",").length;
        if (currentCount >= deliveryPerson.getChosenOrderLimit()) {
            whatsAppService.sendSimpleText(deliveryPersonWaId,
                    deliveryPersonMessageService.getOrderLimitReachedError());
            sendMainMenu(deliveryPerson);
            return;
        }

        if (order.getStatus() != OrderStatus.ORDER_NOT_TAKEN) {
            whatsAppService.sendSimpleText(deliveryPersonWaId,
                    deliveryPersonMessageService.getOrderAlreadyTaken(orderId, order.getStatus().toString()));
            sendMainMenu(deliveryPerson);
            return;
        }

        order.setStatus(OrderStatus.DELIVERY_ONWAY);
        order.setDeliveryPerson(deliveryPerson);
        order.setConfirmedAt(Instant.now());
        customerOrderRepository.save(order);
        orderStatusHistoryService.addEvent(order);

        String newChosen = (currentChosen == null || currentChosen.isEmpty()) ? String.valueOf(orderId)
                : currentChosen + "," + orderId;
        deliveryPerson.setChosenOrder(newChosen);

        if (newChosen.split(",").length >= deliveryPerson.getChosenOrderLimit()) {
            deliveryPerson.setStatus(com.aps.domain.enumeration.DeliveryStatus.BUSY);

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
                Customer customer = customerRepository.findById(order.getCustomer().getId()).orElseThrow();
                whatsAppService.sendDeliveryAssignmentNotification(customer.getWaPhoneNumber(),
                        deliveryPerson.getName(), deliveryPerson.getWaPhoneNumber());

                whatsAppService.sendSimpleText(deliveryPersonWaId,
                        deliveryPersonMessageService.getOrderAcceptedSimpleSuccess());

                sendMainMenu(deliveryPerson);
            }
        });
    }

    private void notifyCustomerOrderShipped(Long orderId) {
        CustomerOrder order = customerOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        whatsAppService.sendSimpleText(order.getCustomer().getWaPhoneNumber(),
                customerMessageService.getOrderShippedMessage());
    }

    public void sendOrderToAllDeliveryPersons(CustomerOrder order, Customer customer, List<CartItemDetailsDTO> items) {
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
}
