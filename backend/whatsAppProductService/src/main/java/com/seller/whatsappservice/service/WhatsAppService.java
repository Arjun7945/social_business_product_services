package com.seller.whatsappservice.service;

import com.seller.whatsappservice.config.WhatsAppConfig;
import com.seller.whatsappservice.dto.WhatsAppMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WhatsAppService {

        private final WhatsAppConfig whatsAppConfig;
        private final RestClient.Builder restClientBuilder;
        private final CustomerMessageService messageService;
        private final DeliveryPersonMessageService deliveryMessageService;

        private RestClient getRestClient() {
                return restClientBuilder
                                .baseUrl(whatsAppConfig.getApiBaseUrl())
                                .defaultHeader("Authorization", "Bearer " + whatsAppConfig.getApiToken())
                                .defaultHeader("Content-Type", "application/json")
                                .build();
        }

        public void sendSimpleText(String toWaId, String text) {
                WhatsAppMessageDto message = WhatsAppMessageDto.builder()
                                .to(toWaId)
                                .type("text")
                                .text(WhatsAppMessageDto.TextDto.builder()
                                                .body(text)
                                                .previewUrl(false)
                                                .build())
                                .build();

                sendToMeta(message);
        }

        public void sendInteractiveOrderAlert(String toWaId, String bodyText, Long orderId) {
                // Constructing a button message for the delivery group
                WhatsAppMessageDto message = WhatsAppMessageDto.builder()
                                .to(toWaId)
                                .type("interactive")
                                .interactive(WhatsAppMessageDto.InteractiveDto.builder()
                                                .type("button")
                                                .body(WhatsAppMessageDto.BodyDto.builder()
                                                                .text(bodyText)
                                                                .build())
                                                .action(WhatsAppMessageDto.ActionDto.builder()
                                                                .buttons(List.of(
                                                                                WhatsAppMessageDto.ButtonDto.builder()
                                                                                                .type("reply")
                                                                                                .reply(WhatsAppMessageDto.ReplyDto
                                                                                                                .builder()
                                                                                                                .id("DELIVERY_TAKE_"
                                                                                                                                + orderId)
                                                                                                                .title(messageService
                                                                                                                                .getButtonAcceptOrder())
                                                                                                                .build())
                                                                                                .build()))
                                                                .build())
                                                .build())
                                .build();

                sendToMeta(message);
        }

        public void sendInteractiveList(String toWaId, String bodyText, List<WhatsAppMessageDto.RowDto> rows) {
                WhatsAppMessageDto message = WhatsAppMessageDto.builder()
                                .to(toWaId)
                                .type("interactive")
                                .interactive(WhatsAppMessageDto.InteractiveDto.builder()
                                                .type("list")
                                                .body(WhatsAppMessageDto.BodyDto.builder()
                                                                .text(bodyText)
                                                                .build())
                                                .action(WhatsAppMessageDto.ActionDto.builder()
                                                                .button(messageService.getButtonViewFish())
                                                                .sections(List.of(
                                                                                WhatsAppMessageDto.SectionDto.builder()
                                                                                                .title(messageService
                                                                                                                .getSectionTitleAvailableFish())
                                                                                                .rows(rows)
                                                                                                .build()))
                                                                .build())
                                                .build())
                                .build();

                sendToMeta(message);
        }

        /**
         * Send interactive buttons for cart actions (Continue Shopping / Checkout)
         */
        public void sendCartActionButtons(String toWaId, String bodyText, List<WhatsAppMessageDto.ButtonDto> buttons) {
                WhatsAppMessageDto message = WhatsAppMessageDto.builder()
                                .to(toWaId)
                                .type("interactive")
                                .interactive(WhatsAppMessageDto.InteractiveDto.builder()
                                                .type("button")
                                                .body(WhatsAppMessageDto.BodyDto.builder()
                                                                .text(bodyText)
                                                                .build())
                                                .action(WhatsAppMessageDto.ActionDto.builder()
                                                                .buttons(buttons)
                                                                .build())
                                                .build())
                                .build();

                sendToMeta(message);
        }

        public void sendCarouselMessage(String to, String bodyText, List<WhatsAppMessageDto.CarouselCardDto> cards) {
                WhatsAppMessageDto message = WhatsAppMessageDto.builder()
                                .to(to)
                                .type("interactive")
                                .interactive(WhatsAppMessageDto.InteractiveDto.builder()
                                                .type("carousel")
                                                .body(WhatsAppMessageDto.BodyDto.builder()
                                                                .text(bodyText)
                                                                .build())
                                                .action(WhatsAppMessageDto.ActionDto.builder()
                                                                .cards(cards)
                                                                .build())
                                                .build())
                                .build();

                sendToMeta(message);
        }

        /**
         * Send order confirmation to customer
         */
        public void sendOrderConfirmation(String toWaId, Long orderId, Double total) {
                String message = messageService.getOrderConfirmation(String.valueOf(orderId), total);
                sendSimpleText(toWaId, message);
        }

        /**
         * Send delivery assignment notification to customer with delivery person
         * details
         */
        public void sendDeliveryAssignmentNotification(String customerWaId, String deliveryPersonName,
                        String deliveryPersonWaPhone) {
                String message = messageService.getDeliveryAssignmentNotification(deliveryPersonName,
                                deliveryPersonWaPhone);
                sendSimpleText(customerWaId, message);
        }

        /**
         * Send welcome message to newly added customer
         */
        public void sendCustomerWelcomeMessage(String customerWaId, String customerName, String customerPhone,
                        String executiveName, String executiveWaPhone) {
                String message = messageService.getCustomerWelcomeByExecutive(customerName, customerPhone,
                                executiveName, executiveWaPhone);
                sendSimpleText(customerWaId, message);
        }

        /**
         * Send welcome message to newly added team member (Admin, Executive, etc.)
         */
        public void sendTeamMemberWelcomeMessage(String teamMemberWaId, String teamMemberName, String teamMemberPhone,
                        String roleName, String addedByName, String addedByWaPhone) {
                String message = deliveryMessageService.getTeamMemberWelcomeMessage(
                                teamMemberName, teamMemberPhone, roleName, addedByName, addedByWaPhone);

                sendSimpleText(teamMemberWaId, message);
        }

        /**
         * Send unauthorized delivery person message
         */
        public void sendUnauthorizedDeliveryMessage(String toWaId) {
                String message = deliveryMessageService.getUnauthorizedDeliveryMessage();
                sendSimpleText(toWaId, message);
        }

        /**
         * Send enhanced delivery confirmation to group
         */
        public void sendDeliveryConfirmationToGroup(String groupId, Long orderId,
                        String deliveryPersonName, String deliveryPersonPhone, LocalDateTime confirmedAt) {

                String formattedTime = confirmedAt.format(DateTimeFormatter.ofPattern("hh:mm a"));
                String message = deliveryMessageService.getDeliveryConfirmationToGroup(
                                orderId, deliveryPersonName, deliveryPersonPhone, formattedTime);

                sendSimpleText(groupId, message);
        }

        private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

        private void sendToMeta(WhatsAppMessageDto message) {
                try {
                        String json = objectMapper.writeValueAsString(message);
                        log.info("Sending message payload: {}", json);

                        getRestClient().post()
                                        .uri("/" + whatsAppConfig.getPhoneNumberId() + "/messages")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(message)
                                        .retrieve()
                                        .toBodilessEntity();
                        log.info("Message sent to {}", message.getTo());
                } catch (Exception e) {
                        log.error("Failed to send message to {}: {}", message.getTo(), e.getMessage());
                }
        }
}
