package com.seller.whatsappservice.controller;

import com.seller.whatsappservice.config.WhatsAppConfig;
import com.seller.whatsappservice.dto.WhatsAppWebhookDto;
import com.seller.whatsappservice.service.CustomerFlowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/webhook")
@RequiredArgsConstructor
@Slf4j
public class WebhookController {

    private final WhatsAppConfig whatsAppConfig;
    private final CustomerFlowService customerFlowService;

    @GetMapping
    public ResponseEntity<String> verifyWebhook(
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.verify_token") String token,
            @RequestParam("hub.challenge") String challenge) {

        if ("subscribe".equals(mode) && whatsAppConfig.getWebhookVerifyToken().equals(token)) {
            log.info("Webhook verified successfully.");
            return ResponseEntity.ok(challenge);
        } else {
            log.warn("Webhook verification failed. Token mismatch.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping
    public ResponseEntity<Void> receiveMessage(@RequestBody WhatsAppWebhookDto webhookDto) {
        log.info("Received webhook: {}", webhookDto);

        if (webhookDto.getEntry() != null) {
            webhookDto.getEntry().forEach(entry -> {
                if (entry.getChanges() != null) {
                    entry.getChanges().forEach(change -> {
                        if (change.getValue() != null) {
                            // Log group messages to capture group ID
                            if (change.getValue().getMessages() != null) {
                                change.getValue().getMessages().forEach(msg -> {
                                    if (msg.getFrom() != null && msg.getFrom().contains("@g.us")) {
                                        log.info("📱 GROUP MESSAGE DETECTED - Group ID: {}", msg.getFrom());
                                        log.info("⚠️ UPDATE application.properties with: whatsapp.delivery-group-id={}",
                                                msg.getFrom());
                                    }
                                });
                            }

                            // Process customer messages
                            if (change.getValue().getMessages() != null) {
                                customerFlowService.processIncomingMessage(change.getValue());
                            }
                        }
                    });
                }
            });
        }

        return ResponseEntity.ok().build();
    }
}
