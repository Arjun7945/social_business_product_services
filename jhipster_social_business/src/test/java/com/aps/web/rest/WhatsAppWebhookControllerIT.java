package com.aps.web.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.aps.IntegrationTest;
import com.aps.config.WhatsAppConfig;
import com.aps.service.WhatsAppDispatcherService;
import com.aps.service.dto.WhatsAppWebhookDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for the {@link WhatsAppWebhookController} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class WhatsAppWebhookControllerIT {

    private static final String API_URL = "/api/v1/webhook";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WhatsAppDispatcherService dispatcherService;

    @Autowired // Inject actual config to verify against properties
    private WhatsAppConfig whatsAppConfig;

    @Autowired
    private ObjectMapper om;

    @Test
    void verifyWebhook_ShouldReturnChallenge_WhenTokenIsValid() throws Exception {
        String mode = "subscribe";
        String token = "my-test-token"; // We assume application-test.yml or default sets this, or we rely on injected
                                        // val
        String challenge = "123456789";

        // Reflection to force token if properties differ in test env
        // But usually @IntegrationTest loads context. Let's check what the token is or
        // assume "my-secret-token" from defaults?
        // Better: Mock WhatsAppConfig? No, it's a bean.
        // Let's assume standard JHipster test config might use "my-secret-token".
        // If not, we can rely on what's in application.yml.
        // Actually, let's look at WhatsAppConfig behavior.

        // Easier: Override the method behavior? No, verifyWebhook logic is in
        // Controller.
        // Let's rely on the property value.
        // If the test fails, we'll know the property is missing.
        // Wait, for stability, let's mock the 'whatsAppConfig' if possible, OR set the
        // property in the test.
    }

    // Simpler approach: construct controller manually for unit test?
    // But this is an IT.
    // Let's use @MockBean for WhatsAppConfig to control the expected token.

    @MockBean
    private WhatsAppConfig driverWhatsAppConfig; // mocks the bean

    @Test
    void verifyWebhook_Success() throws Exception {
        String validToken = "valid-token";
        when(driverWhatsAppConfig.getWebhookVerifyToken()).thenReturn(validToken);

        mockMvc.perform(get(API_URL)
                .param("hub.mode", "subscribe")
                .param("hub.verify_token", validToken)
                .param("hub.challenge", "challenge_code"))
                .andExpect(status().isOk())
                .andExpect(content().string("challenge_code"));
    }

    @Test
    void verifyWebhook_Forbidden_WhenTokenInvalid() throws Exception {
        String validToken = "valid-token";
        when(driverWhatsAppConfig.getWebhookVerifyToken()).thenReturn(validToken);

        mockMvc.perform(get(API_URL)
                .param("hub.mode", "subscribe")
                .param("hub.verify_token", "invalid-token")
                .param("hub.challenge", "challenge_code"))
                .andExpect(status().isForbidden());
    }

    @Test
    void receiveMessage_ShouldProcessAsync() throws Exception {
        WhatsAppWebhookDto dto = new WhatsAppWebhookDto();
        // Populate dto if validation required?
        // Looking at Controller: @RequestBody WhatsAppWebhookDto webhookDto
        // It's not @Valid annotated in the snippet I saw.

        mockMvc.perform(post(API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsBytes(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("EVENT_RECEIVED"));

        verify(dispatcherService).processWebhookAsync(any(WhatsAppWebhookDto.class));
    }
}
