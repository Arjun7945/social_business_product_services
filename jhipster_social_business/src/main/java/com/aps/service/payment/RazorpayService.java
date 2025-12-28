package com.aps.service.payment;

import com.aps.domain.Customer;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class RazorpayService {

    private final Logger log = LoggerFactory.getLogger(RazorpayService.class);
    private final RestTemplate restTemplate;
    private final com.aps.service.OrderService orderService;

    @org.springframework.beans.factory.annotation.Value("${razorpay.key-id}")
    private String keyId;

    @org.springframework.beans.factory.annotation.Value("${razorpay.key-secret}")
    private String keySecret;

    @org.springframework.beans.factory.annotation.Value("${razorpay.webhook-secret}")
    private String webhookSecret;

    private static final String RAZORPAY_API_BASE = "https://api.razorpay.com/v1";

    public RazorpayService(
        RestTemplateBuilder restTemplateBuilder,
        @org.springframework.context.annotation.Lazy com.aps.service.OrderService orderService,
        @org.springframework.beans.factory.annotation.Value("${razorpay.key-id}") String keyId,
        @org.springframework.beans.factory.annotation.Value("${razorpay.key-secret}") String keySecret
    ) {
        this.keyId = keyId;
        this.keySecret = keySecret;
        this.restTemplate = restTemplateBuilder.basicAuthentication(keyId, keySecret).build();
        this.orderService = orderService;
    }

    /**
     * Create a Standard Static QR Code (or close to it)
     * Note: Razorpay "QR Code" API usually creates a UPI QR.
     * For Payment Links, use createPaymentLink.
     */
    public String createCustomer(String name, String phone) {
        try {
            String url = RAZORPAY_API_BASE + "/customers";

            Map<String, Object> request = new HashMap<>();
            request.put("name", name);
            request.put("contact", phone);
            request.put("fail_existing", "0"); // Don't fail if exists, return existing

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            if (response.getBody() != null && response.getBody().containsKey("id")) {
                return (String) response.getBody().get("id");
            }
        } catch (Exception e) {
            log.error("Error creating Razorpay Customer", e);
        }
        return null;
    }

    /**
     * Create a Native Razorpay QR Code.
     * Endpoint: /v1/payments/qr_codes
     * Docs: https://razorpay.com/docs/api/qr-codes/create/
     */
    public String createQrCode(Long orderId, Double amount, String customerId) {
        try {
            // Correct Endpoint for QR Codes
            String url = RAZORPAY_API_BASE + "/payments/qr_codes";

            Map<String, Object> request = new HashMap<>();
            request.put("type", "upi_qr");
            request.put("name", "Social Business Store");
            request.put("usage", "single_use");
            request.put("fixed_amount", true);
            request.put("payment_amount", (int) (amount * 100)); // paise
            request.put("description", "Order #" + orderId);

            if (customerId != null) {
                request.put("customer_id", customerId);
            }

            log.info("Creating QR with payload: {} at URL: {}", request, url);

            Map<String, String> notes = new HashMap<>();
            notes.put("order_id", orderId.toString());
            request.put("notes", notes);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            // Using Map.class for response to parse arbitrary JSON
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            if (response.getBody() != null && response.getBody().containsKey("image_url")) {
                return (String) response.getBody().get("image_url");
            }
        } catch (HttpClientErrorException e) {
            log.error("Error creating Razorpay QR: {} Response: {}", e.getMessage(), e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Error creating Razorpay QR", e);
        }
        return null;
    }

    public String createPaymentLink(Long orderId, Double amount, Customer customer) {
        try {
            String url = RAZORPAY_API_BASE + "/payment_links";

            Map<String, Object> request = new HashMap<>();
            request.put("amount", (int) (amount * 100)); // paise
            request.put("currency", "INR");
            request.put("accept_partial", false);
            request.put("description", "Payment for Order #" + orderId);

            Map<String, String> notes = new HashMap<>();
            notes.put("order_id", orderId.toString());
            request.put("notes", notes);

            Map<String, String> custDetails = new HashMap<>();
            custDetails.put("name", customer.getName());
            custDetails.put("contact", customer.getPhoneNumber()); // Assuming format is compatible or trimmed
            request.put("customer", custDetails);

            request.put("notify", Map.of("sms", true, "email", false));
            request.put("callback_url", "https://google.com"); // Placeholder or actual callback
            request.put("callback_method", "get");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            if (response.getBody() != null && response.getBody().containsKey("short_url")) {
                return (String) response.getBody().get("short_url");
            }
        } catch (Exception e) {
            log.error("Error creating Razorpay Link", e);
        }
        return null; // Handle error appropriately
    }

    // ==========================================
    // WEBHOOK HANDLING (Using Manual HMAC)
    // ==========================================

    public boolean verifySignature(String payload, String signature) {
        try {
            javax.crypto.Mac sha256_HMAC = javax.crypto.Mac.getInstance("HmacSHA256");
            javax.crypto.spec.SecretKeySpec secret_key = new javax.crypto.spec.SecretKeySpec(webhookSecret.getBytes("UTF-8"), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            byte[] hash = sha256_HMAC.doFinal(payload.getBytes("UTF-8"));

            // Convert to Hex
            StringBuilder result = new StringBuilder();
            for (byte b : hash) {
                result.append(String.format("%02x", b));
            }
            String calculatedSignature = result.toString();
            if (!calculatedSignature.equals(signature)) {
                log.error("Signature Mismatch! Expected: {}, Received: {}", calculatedSignature, signature);
                log.error("Secret used hash: {}", webhookSecret.hashCode());
            }
            return calculatedSignature.equals(signature);
        } catch (Exception e) {
            log.error("Error verifying signature", e);
            return false;
        }
    }

    @org.springframework.scheduling.annotation.Async
    public void processWebhookEvent(String payloadJson) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Map<String, Object> payload = mapper.readValue(payloadJson, Map.class);

            String event = (String) payload.get("event");
            log.info("Processing Razorpay Event: {}", event);

            if ("payment.captured".equals(event)) {
                handlePaymentCaptured(payload);
            } else if ("payment.failed".equals(event)) {
                handlePaymentFailed(payload);
            }
        } catch (Exception e) {
            log.error("Error processing webhook payload", e);
        }
    }

    private void handlePaymentCaptured(Map<String, Object> payload) {
        try {
            Map<String, Object> paymentEntity = getPaymentEntity(payload);
            if (paymentEntity == null) return;

            String paymentId = (String) paymentEntity.get("id");
            Double amount = ((Number) paymentEntity.get("amount")).doubleValue() / 100.0;

            // Extract Order ID from description or notes
            Long orderId = extractOrderId(paymentEntity);

            if (orderId != null) {
                log.info("Payment Captured for Order: {}, Amount: {}, TxnId: {}", orderId, amount, paymentId);
                orderService.processPaymentSuccess(orderId, paymentId, amount);
            }
        } catch (Exception e) {
            log.error("Error handling payment captured", e);
        }
    }

    private void handlePaymentFailed(Map<String, Object> payload) {
        try {
            Map<String, Object> paymentEntity = getPaymentEntity(payload);
            if (paymentEntity == null) return;

            String paymentId = (String) paymentEntity.get("id");
            Long orderId = extractOrderId(paymentEntity);

            if (orderId != null) {
                log.info("Payment Failed for Order: {}, TxnId: {}", orderId, paymentId);
                orderService.processPaymentFailure(orderId, paymentId);
            }
        } catch (Exception e) {
            log.error("Error handling payment failed", e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getPaymentEntity(Map<String, Object> payload) {
        Map<String, Object> contain = (Map<String, Object>) payload.get("payload");
        Map<String, Object> payment = (Map<String, Object>) contain.get("payment");
        return (Map<String, Object>) payment.get("entity");
    }

    @SuppressWarnings("unchecked")
    private Long extractOrderId(Map<String, Object> paymentEntity) {
        try {
            // Try notes first
            Map<String, Object> notes = (Map<String, Object>) paymentEntity.get("notes");
            if (notes != null && notes.containsKey("order_id")) {
                return Long.parseLong(notes.get("order_id").toString());
            }

            // Try description fallback
            String desc = (String) paymentEntity.get("description");
            if (desc != null && desc.contains("Order #")) {
                return Long.parseLong(desc.replace("Payment for Order #", "").replace("Order #", "").trim());
            }
        } catch (Exception e) {
            log.warn("Could not extract Order ID from payment: {}", paymentEntity);
        }
        return null;
    }
}
