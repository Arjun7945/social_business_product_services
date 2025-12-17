package com.aps.web.rest;

import com.aps.service.payment.RazorpayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks")
public class RazorpayWebhookController {

    private final Logger log = LoggerFactory.getLogger(RazorpayWebhookController.class);
    private final RazorpayService razorpayService;

    public RazorpayWebhookController(RazorpayService razorpayService) {
        this.razorpayService = razorpayService;
    }

    @PostMapping("/razorpay")
    public ResponseEntity<String> handleRazorpayWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String signature) {

        log.info("Received Razorpay Webhook");

        // 1. Verify Signature
        boolean isValid = razorpayService.verifySignature(payload, signature);
        if (!isValid) {
            log.warn("Invalid Razorpay Signature");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Signature");
        }

        // 2. Process Event
        razorpayService.processWebhookEvent(payload);

        return ResponseEntity.ok("Received");
    }
}
