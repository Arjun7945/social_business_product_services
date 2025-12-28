package com.aps.service;

import com.aps.config.WhatsAppConfig;
import com.aps.service.dto.WhatsAppMessageDto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * Service for sending messages to WhatsApp Cloud API.
 * Acts as a wrapper around Meta's API.
 */
@Service
public class WhatsAppService {

    private final Logger log = LoggerFactory.getLogger(WhatsAppService.class);

    private final WhatsAppConfig whatsAppConfig;
    private final RestClient restClient;
    private final CustomerMessageService messageService;
    private final DeliveryPersonMessageService deliveryMessageService;
    private final CommonMessageService commonMessageService;

    public WhatsAppService(
        WhatsAppConfig whatsAppConfig,
        RestClient whatsAppRestClient,
        CustomerMessageService messageService,
        DeliveryPersonMessageService deliveryMessageService,
        CommonMessageService commonMessageService
    ) {
        this.whatsAppConfig = whatsAppConfig;
        this.restClient = whatsAppRestClient;
        this.messageService = messageService;
        this.deliveryMessageService = deliveryMessageService;
        this.commonMessageService = commonMessageService;
    }

    public boolean sendSimpleText(String toWaId, String text) {
        WhatsAppMessageDto message = WhatsAppMessageDto.builder()
            .to(toWaId)
            .type("text")
            .text(WhatsAppMessageDto.TextDto.builder().body(text).previewUrl(false).build())
            .build();

        return sendToMeta(message);
    }

    public boolean sendDocument(String toWaId, String mediaId, String filename, String caption) {
        WhatsAppMessageDto message = WhatsAppMessageDto.builder()
            .to(toWaId)
            .type("document")
            .document(WhatsAppMessageDto.DocumentDto.builder().id(mediaId).filename(filename).caption(caption).build())
            .build();

        return sendToMeta(message);
    }

    public boolean sendLocation(String toWaId, double lat, double lon, String name, String address) {
        WhatsAppMessageDto message = WhatsAppMessageDto.builder()
            .to(toWaId)
            .type("location")
            .location(WhatsAppMessageDto.LocationDto.builder().latitude(lat).longitude(lon).name(name).address(address).build())
            .build();

        return sendToMeta(message);
    }

    public boolean sendImageMessage(String toWaId, String imageUrl, String caption) {
        WhatsAppMessageDto message = WhatsAppMessageDto.builder()
            .to(toWaId)
            .type("image")
            .image(WhatsAppMessageDto.ImageDto.builder().link(imageUrl).caption(caption).build())
            .build();

        return sendToMeta(message);
    }

    public boolean sendInteractiveOrderAlert(String toWaId, String bodyText, Long orderId) {
        WhatsAppMessageDto message = WhatsAppMessageDto.builder()
            .to(toWaId)
            .type("interactive")
            .interactive(
                WhatsAppMessageDto.InteractiveDto.builder()
                    .type("button")
                    .body(WhatsAppMessageDto.BodyDto.builder().text(bodyText).build())
                    .action(
                        WhatsAppMessageDto.ActionDto.builder()
                            .buttons(
                                List.of(
                                    WhatsAppMessageDto.ButtonDto.builder()
                                        .type("reply")
                                        .reply(
                                            WhatsAppMessageDto.ReplyDto.builder()
                                                .id("DELIVERY_TAKE_" + orderId)
                                                .title(messageService.getButtonAcceptOrder())
                                                .build()
                                        )
                                        .build()
                                )
                            )
                            .build()
                    )
                    .build()
            )
            .build();

        return sendToMeta(message);
    }

    public boolean sendInteractiveList(String toWaId, String bodyText, List<WhatsAppMessageDto.RowDto> rows) {
        WhatsAppMessageDto message = WhatsAppMessageDto.builder()
            .to(toWaId)
            .type("interactive")
            .interactive(
                WhatsAppMessageDto.InteractiveDto.builder()
                    .type("list")
                    .body(WhatsAppMessageDto.BodyDto.builder().text(bodyText).build())
                    .action(
                        WhatsAppMessageDto.ActionDto.builder()
                            .button(commonMessageService.getButtonViewOptions())
                            .sections(
                                List.of(
                                    WhatsAppMessageDto.SectionDto.builder()
                                        .title(messageService.getSectionTitleAvailableFish())
                                        .rows(rows)
                                        .build()
                                )
                            )
                            .build()
                    )
                    .build()
            )
            .build();

        return sendToMeta(message);
    }

    public boolean sendCartActionButtons(String toWaId, String bodyText, List<WhatsAppMessageDto.ButtonDto> buttons) {
        WhatsAppMessageDto message = WhatsAppMessageDto.builder()
            .to(toWaId)
            .type("interactive")
            .interactive(
                WhatsAppMessageDto.InteractiveDto.builder()
                    .type("button")
                    .body(WhatsAppMessageDto.BodyDto.builder().text(bodyText).build())
                    .action(WhatsAppMessageDto.ActionDto.builder().buttons(buttons).build())
                    .build()
            )
            .build();

        return sendToMeta(message);
    }

    public boolean sendCarouselMessage(String to, String bodyText, List<WhatsAppMessageDto.CarouselCardDto> cards) {
        WhatsAppMessageDto message = WhatsAppMessageDto.builder()
            .to(to)
            .type("interactive")
            .interactive(
                WhatsAppMessageDto.InteractiveDto.builder()
                    .type("carousel")
                    .body(WhatsAppMessageDto.BodyDto.builder().text(bodyText).build())
                    .action(WhatsAppMessageDto.ActionDto.builder().cards(cards).build())
                    .build()
            )
            .build();

        return sendToMeta(message);
    }

    public boolean sendOrderConfirmation(String toWaId, Long orderId, BigDecimal total) {
        // Updated to accept BigDecimal
        String message = messageService.getOrderConfirmation(String.valueOf(orderId), total.doubleValue());
        return sendSimpleText(toWaId, message);
    }

    // Overload for Double if needed, but BigDecimal is preferred in JHipster
    public boolean sendOrderConfirmation(String toWaId, Long orderId, Double total) {
        String message = messageService.getOrderConfirmation(String.valueOf(orderId), total);
        return sendSimpleText(toWaId, message);
    }

    public boolean sendDeliveryAssignmentNotification(String customerWaId, String deliveryPersonName, String deliveryPersonWaPhone) {
        String message = messageService.getDeliveryAssignmentNotification(deliveryPersonName, deliveryPersonWaPhone);
        return sendSimpleText(customerWaId, message);
    }

    public boolean sendCustomerWelcomeMessage(
        String customerWaId,
        String customerName,
        String customerPhone,
        String executiveName,
        String executiveWaPhone
    ) {
        String message = messageService.getCustomerWelcomeByExecutive(customerName, customerPhone, executiveName, executiveWaPhone);
        return sendSimpleText(customerWaId, message);
    }

    public boolean sendTeamMemberWelcomeMessage(
        String teamMemberWaId,
        String teamMemberName,
        String teamMemberPhone,
        String roleName,
        String addedByName,
        String addedByWaPhone
    ) {
        String message = deliveryMessageService.getTeamMemberWelcomeMessage(
            teamMemberName,
            teamMemberPhone,
            roleName,
            addedByName,
            addedByWaPhone
        );

        return sendSimpleText(teamMemberWaId, message);
    }

    public boolean sendUnauthorizedDeliveryMessage(String toWaId) {
        String message = deliveryMessageService.getUnauthorizedDeliveryMessage();
        return sendSimpleText(toWaId, message);
    }

    public boolean sendDeliveryConfirmationToGroup(
        String groupId,
        Long orderId,
        String deliveryPersonName,
        String deliveryPersonPhone,
        LocalDateTime confirmedAt
    ) {
        String formattedTime = confirmedAt.format(DateTimeFormatter.ofPattern("hh:mm a"));
        String message = deliveryMessageService.getDeliveryConfirmationToGroup(
            orderId,
            deliveryPersonName,
            deliveryPersonPhone,
            formattedTime
        );

        return sendSimpleText(groupId, message);
    }

    private boolean sendToMeta(WhatsAppMessageDto message) {
        try {
            // Using ObjectMapper only for logging/debug if needed, RestClient handles
            // serialization
            // String jsonPreview = objectMapper.writeValueAsString(message);
            // log.debug("Sending message payload: {}", jsonPreview);

            restClient
                .post()
                .uri("/" + whatsAppConfig.getPhoneNumberId() + "/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .body(message)
                // .retrieve() // JHipster/Spring 6.1 RestClient syntax
                // .toBodilessEntity();
                .retrieve()
                .toBodilessEntity();

            log.info("Message sent to {}", message.getTo());
            return true;
        } catch (Exception e) {
            log.error("Failed to send message to {}: {}", message.getTo(), e.getMessage());
            return false;
        }
    }
}
