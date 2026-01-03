package com.aps.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class LicensingService {

    private final Logger log = LoggerFactory.getLogger(LicensingService.class);

    @Value("${application.licensing.url}")
    private String licensingUrl;

    @Value("${LICENSE_KEY:MOCK_KEY}")
    private String licenseKey;

    @Scheduled(cron = "${application.licensing.cron:0 0 12 * * ?}")
    public void checkLicense() {
        log.info("Initiating License Heartbeat check against: {}", licensingUrl);

        // MOCK LOGIC - To be replaced with actual HTTP call to licensing server
        if ("MOCK_INVALID".equals(licenseKey)) {
            log.error("CRITICAL: License Key is INVALID. Application functions may be restricted.");
        } else {
            log.info("License Validated Successfully. Key: [HIDDEN]");
        }
    }
}
