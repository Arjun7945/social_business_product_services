package com.aps.config;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@ConfigurationProperties(prefix = "whatsapp")
public class WhatsAppConfig {

    private Api api;
    private String phoneNumberId;
    private Webhook webhook;
    private String deliveryGroupId;

    @Bean
    public RestClient whatsAppRestClient(RestClient.Builder builder) {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(200);
        connectionManager.setDefaultMaxPerRoute(20);

        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connectionManager).build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

        return builder
            .requestFactory(factory)
            .baseUrl(getApiBaseUrl())
            .defaultHeader("Authorization", "Bearer " + getApiToken())
            .defaultHeader("Content-Type", "application/json")
            .build();
    }

    public String getWebhookVerifyToken() {
        return webhook != null ? webhook.getVerifyToken() : null;
    }

    public String getApiBaseUrl() {
        return api != null ? api.getBaseUrl() : null;
    }

    public String getApiToken() {
        return api != null ? api.getToken() : null;
    }

    public Api getApi() {
        return api;
    }

    public void setApi(Api api) {
        this.api = api;
    }

    public String getPhoneNumberId() {
        return phoneNumberId;
    }

    public void setPhoneNumberId(String phoneNumberId) {
        this.phoneNumberId = phoneNumberId;
    }

    public Webhook getWebhook() {
        return webhook;
    }

    public void setWebhook(Webhook webhook) {
        this.webhook = webhook;
    }

    public String getDeliveryGroupId() {
        return deliveryGroupId;
    }

    public void setDeliveryGroupId(String deliveryGroupId) {
        this.deliveryGroupId = deliveryGroupId;
    }

    public static class Api {

        private String token;
        private String baseUrl;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }
    }

    public static class Webhook {

        private String verifyToken;

        public String getVerifyToken() {
            return verifyToken;
        }

        public void setVerifyToken(String verifyToken) {
            this.verifyToken = verifyToken;
        }
    }
}
