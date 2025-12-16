package com.aps.service;

import com.aps.config.WhatsAppConfig;
import com.aps.service.dto.WhatsAppMessageDto;

import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service for sending messages to WhatsApp Cloud API.
 * Acts as a wrapper around Meta's API.
 */
@Service
public class WhatsAppService {

        private final Logger log = LoggerFactory.getLogger(WhatsAppService.class);

        private final WhatsAppConfig whatsAppConfig;
        private final RestClient.Builder restClientBuilder;
        private final CustomerMessageService messageService;
        private final DeliveryPersonMessageService deliveryMessageService;

        public WhatsAppService(WhatsAppConfig whatsAppConfig,
                        RestClient.Builder restClientBuilder,
                        CustomerMessageService messageService,
                        DeliveryPersonMessageService deliveryMessageService) {
                this.whatsAppConfig = whatsAppConfig;
                this.restClientBuilder = restClientBuilder;
                this.messageService = messageService;
                this.deliveryMessageService = deliveryMessageService;
        }

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

        public void sendDocument(String toWaId, String mediaId, String filename, String caption) {
                WhatsAppMessageDto message = WhatsAppMessageDto.builder()
                                .to(toWaId)
                                .type("document")
                                .document(WhatsAppMessageDto.DocumentDto.builder()
                                                .id(mediaId)
                                                .filename(filename)
                                                .caption(caption)
                                                .build())
                                .build();

                sendToMeta(message);
        }

        public void sendInteractiveOrderAlert(String toWaId, String bodyText, Long orderId) {
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

        public void sendOrderConfirmation(String toWaId, Long orderId, BigDecimal total) {
                // Updated to accept BigDecimal
                String message = messageService.getOrderConfirmation(String.valueOf(orderId), total.doubleValue());
                sendSimpleText(toWaId, message);
        }

        // Overload for Double if needed, but BigDecimal is preferred in JHipster
        public void sendOrderConfirmation(String toWaId, Long orderId, Double total) {
                String message = messageService.getOrderConfirmation(String.valueOf(orderId), total);
                sendSimpleText(toWaId, message);
        }

        public void sendDeliveryAssignmentNotification(String customerWaId, String deliveryPersonName,
                        String deliveryPersonWaPhone) {
                String message = messageService.getDeliveryAssignmentNotification(deliveryPersonName,
                                deliveryPersonWaPhone);
                sendSimpleText(customerWaId, message);
        }

        public void sendCustomerWelcomeMessage(String customerWaId, String customerName, String customerPhone,
                        String executiveName, String executiveWaPhone) {
                String message = messageService.getCustomerWelcomeByExecutive(customerName, customerPhone,
                                executiveName, executiveWaPhone);
                sendSimpleText(customerWaId, message);
        }

        public void sendTeamMemberWelcomeMessage(String teamMemberWaId, String teamMemberName, String teamMemberPhone,
                        String roleName, String addedByName, String addedByWaPhone) {
                String message = deliveryMessageService.getTeamMemberWelcomeMessage(
                                teamMemberName, teamMemberPhone, roleName, addedByName, addedByWaPhone);

                sendSimpleText(teamMemberWaId, message);
        }

        public void sendUnauthorizedDeliveryMessage(String toWaId) {
                String message = deliveryMessageService.getUnauthorizedDeliveryMessage();
                sendSimpleText(toWaId, message);
        }

        public void sendDeliveryConfirmationToGroup(String groupId, Long orderId,
                        String deliveryPersonName, String deliveryPersonPhone, LocalDateTime confirmedAt) {

                String formattedTime = confirmedAt.format(DateTimeFormatter.ofPattern("hh:mm a"));
                String message = deliveryMessageService.getDeliveryConfirmationToGroup(
                                orderId, deliveryPersonName, deliveryPersonPhone, formattedTime);

                sendSimpleText(groupId, message);
        }

        private void sendToMeta(WhatsAppMessageDto message) {
                try {
                        // Using ObjectMapper only for logging/debug if needed, RestClient handles
                        // serialization
                        // String jsonPreview = objectMapper.writeValueAsString(message);
                        // log.debug("Sending message payload: {}", jsonPreview);

                        getRestClient().post()
                                        .uri("/" + whatsAppConfig.getPhoneNumberId() + "/messages")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(message)
                                        // .retrieve() // JHipster/Spring 6.1 RestClient syntax
                                        // .toBodilessEntity();
                                        .retrieve()
                                        .toBodilessEntity();

                        log.info("Message sent to {}", message.getTo());
                } catch (Exception e) {
                        log.error("Failed to send message to {}: {}", message.getTo(), e.getMessage());
                }
        }
}
