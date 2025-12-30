package com.aps.service.customer_flow;

import com.aps.domain.BotSession;
import com.aps.domain.Customer;
import com.aps.domain.CustomerOrder;
import com.aps.domain.enumeration.CustomerFlowStage;
import com.aps.service.CartService;
import com.aps.service.CustomerMessageService;
import com.aps.service.OrderService;
import com.aps.service.WhatsAppService;
import com.aps.service.dto.WhatsAppMessageDto;
import com.aps.service.errors.ProductUnavailableException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Handles Order placement and management flow.
 * Extracted from CustomerFlowService.
 */
@Service
public class OrderFlowHandler {

    private final Logger log = LoggerFactory.getLogger(OrderFlowHandler.class);

    private final OrderService orderService;
    private final CartService cartService;
    private final WhatsAppService whatsAppService;
    private final CustomerMessageService messageService;
    private final FlowStateService flowStateService;
    private final CartHandler cartHandler;

    public OrderFlowHandler(
            OrderService orderService,
            CartService cartService,
            WhatsAppService whatsAppService,
            CustomerMessageService messageService,
            FlowStateService flowStateService,
            CartHandler cartHandler) {
        this.orderService = orderService;
        this.cartService = cartService;
        this.whatsAppService = whatsAppService;
        this.messageService = messageService;
        this.flowStateService = flowStateService;
        this.cartHandler = cartHandler;
    }

    public void placeOrder(Customer customer, BotSession session) {
        try {
            CustomerOrder order = orderService.createOrder(customer, "NOT_SELECTED");

            Double total = order.getTotalAmount() != null ? order.getTotalAmount().doubleValue() : 0.0;

            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            whatsAppService.sendOrderConfirmation(customer.getWaPhoneNumber(), order.getId(), total);
                        }
                    });

            flowStateService.updateStage(session, CustomerFlowStage.REGISTERED);
        } catch (ProductUnavailableException e) {
            whatsAppService.sendSimpleText(
                    customer.getWaPhoneNumber(),
                    "🚫 " + e.getMessage() + "\nPlease remove the unavailable item from your cart.");
            cartHandler.showCartSummary(customer, session);
        } catch (IllegalStateException e) {
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), messageService.getEmptyCartDuringOrder());
        } catch (Exception e) {
            log.error("Order placement failed for customer {}", customer.getId(), e);
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(), messageService.getOrderPlacementFailure());
        }
    }

    public void handleCancelOrder(Customer customer, BotSession session) {
        cartService.clearCart(customer.getId());
        flowStateService.updateStage(session, CustomerFlowStage.REGISTERED);
        whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                messageService.getOrderCancelled(customer.getName()));
    }

    public void showEditOrderOptions(Customer customer, BotSession session) {
        List<WhatsAppMessageDto.RowDto> rows = List.of(
                WhatsAppMessageDto.RowDto.builder()
                        .id("EDIT_PRODUCT")
                        .title(messageService.getButtonEditProductShort())
                        .description(messageService.getButtonEditProductDesc())
                        .build(),
                WhatsAppMessageDto.RowDto.builder()
                        .id("EDIT_QUANTITY")
                        .title(messageService.getButtonEditQuantityShort())
                        .description(messageService.getButtonEditQuantityDesc())
                        .build(),
                WhatsAppMessageDto.RowDto.builder()
                        .id("CONTINUE_SHOPPING")
                        .title(messageService.getButtonAddMoreFishShort())
                        .description(messageService.getButtonAddMoreFishDesc())
                        .build(),
                WhatsAppMessageDto.RowDto.builder()
                        .id("BACK_TO_CHECKOUT")
                        .title(messageService.getButtonBackToCheckoutShort())
                        .description(messageService.getButtonBackToCheckoutDesc())
                        .build());

        whatsAppService.sendInteractiveList(customer.getWaPhoneNumber(),
                messageService.getEditOrderMenu(customer.getName()), rows);

        flowStateService.updateStage(session, CustomerFlowStage.EDITING_ORDER);
    }
}
