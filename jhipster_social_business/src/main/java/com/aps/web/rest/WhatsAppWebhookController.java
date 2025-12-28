package com.aps.web.rest;

import com.aps.config.WhatsAppConfig;
import com.aps.service.WhatsAppDispatcherService;
import com.aps.service.dto.WhatsAppWebhookDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class WhatsAppWebhookController {

    private final Logger log = LoggerFactory.getLogger(WhatsAppWebhookController.class);

    private final WhatsAppConfig whatsAppConfig;
    private final WhatsAppDispatcherService dispatcherService;

    public WhatsAppWebhookController(WhatsAppConfig whatsAppConfig, WhatsAppDispatcherService dispatcherService) {
        this.whatsAppConfig = whatsAppConfig;
        this.dispatcherService = dispatcherService;
    }

    /**
     * Webhook Verification (GET)
     */
    @GetMapping("/webhook")
    public ResponseEntity<String> verifyWebhook(
        @RequestParam("hub.mode") String mode,
        @RequestParam("hub.verify_token") String token,
        @RequestParam("hub.challenge") String challenge
    ) {
        log.info("Received Webhook Verification Request: mode={}, token={}, challenge={}", mode, token, challenge);

        if ("subscribe".equals(mode) && whatsAppConfig.getWebhookVerifyToken().equals(token)) {
            log.info("Webhook verification successful!");
            return ResponseEntity.ok(challenge);
        } else {
            log.error("Webhook verification failed. Token mismatch.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Verification failed");
        }
    }

    /**
     * Webhook Message Handling (POST)
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> receiveMessage(@RequestBody WhatsAppWebhookDto webhookDto) {
        log.debug("Received Webhook Payload: {}", webhookDto);

        if (webhookDto.getEntry() != null) {
            webhookDto
                .getEntry()
                .forEach(entry -> {
                    if (entry.getChanges() != null) {
                        entry
                            .getChanges()
                            .forEach(change -> {
                                if (change.getValue() != null && change.getValue().getMessages() != null) {
                                    change
                                        .getValue()
                                        .getMessages()
                                        .forEach(message -> {
                                            dispatcherService.handleIncomingMessage(change.getValue(), message);
                                        });
                                }
                            });
                    }
                });
        }

        return ResponseEntity.ok("EVENT_RECEIVED");
    }
}
