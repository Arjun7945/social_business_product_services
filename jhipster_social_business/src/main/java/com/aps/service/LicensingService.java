package com.aps.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@Service
public class LicensingService {

    private final Logger log = LoggerFactory.getLogger(LicensingService.class);
    private final RestTemplate restTemplate;

    public LicensingService() {
        org.springframework.http.client.SimpleClientHttpRequestFactory factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(3000);
        this.restTemplate = new RestTemplate(factory);
    }

    @Value("${application.licensing.url}")
    private String licensingUrl;

    @Value("${LICENSE_KEY:MOCK_KEY}")
    private String licenseKey;

    // Default to true during startup so we don't block before first check
    private boolean isLicenseValid = true;

    // Accessor for other services to check status
    public boolean isLicenseValid() {
        return this.isLicenseValid;
    }

    // NEW: Check immediately when the app is ready (Startup Check)
    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        checkLicense();
    }

    @Scheduled(cron = "${application.licensing.cron:0 0 12 * * ?}")
    public void checkLicense() {
        log.info("Initiating License Heartbeat check against: {}", licensingUrl);

        try {
            String verifyUrl = licensingUrl + "?key=" + licenseKey;
            ResponseEntity<String> response = restTemplate.getForEntity(verifyUrl, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("✅ License Validated Successfully.");
                this.isLicenseValid = true;
            } else {
                handleInvalidLicense("Server returned " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("⚠️ License Check Failed: {}", e.getMessage());
            // Optional: Implement Grace Period logic here.
            // For now, we will be strict: if check fails, we assume invalid after retries.
            // But to avoid stopping prod on transient network errors, we might want to keep
            // previous state
            // or default to false only on explicit 403.
            // For this implementation, we will only disable on explicit 403 Forbidden.
            if (e.getMessage().contains("403")) {
                handleInvalidLicense("Server denied access (403)");
            }
        }
    }

    private void handleInvalidLicense(String reason) {
        log.error("❌ CRITICAL: License Key is INVALID/EXPIRED/SUSPENDED. Reason: {}", reason);
        log.error("🛑 STOPPING ALL SERVICE PROCESSING IMMEDIATELY.");
        this.isLicenseValid = false;
    }
}
