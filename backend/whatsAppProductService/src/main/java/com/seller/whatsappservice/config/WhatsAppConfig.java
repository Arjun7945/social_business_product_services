package com.seller.whatsappservice.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@Getter
public class WhatsAppConfig {

    @Value("${whatsapp.api.token}")
    private String apiToken;

    @Value("${whatsapp.api.base-url}")
    private String apiBaseUrl;

    @Value("${whatsapp.phone-number-id}")
    private String phoneNumberId;

    @Value("${whatsapp.webhook.verify-token}")
    private String webhookVerifyToken;

    @Value("${whatsapp.delivery-group-id}")
    private String deliveryGroupId;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
