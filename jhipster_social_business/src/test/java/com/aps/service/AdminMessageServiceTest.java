package com.aps.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AdminMessageServiceTest {

    private AdminMessageService adminMessageService;

    @BeforeEach
    public void setup() {
        adminMessageService = new AdminMessageService();
    }

    @Test
    void testGetNoCreditOrdersFound() {
        assertThat(adminMessageService.getNoCreditOrdersFound())
                .isEqualTo("No credit customer orders found till now.");
    }

    @Test
    void testGetCreditGrantedAlwaysMessage() {
        String msg = adminMessageService.getCreditGrantedAlwaysMessage("John Doe", 123L);
        assertThat(msg).isEqualTo("✅ Approved: Always Grant for John Doe");
    }

    @Test
    void testGetCreditOneTimeApprovedMessage() {
        String msg = adminMessageService.getCreditOneTimeApprovedMessage(123L);
        assertThat(msg).isEqualTo("✅ Approved: Only for this Order123");
    }

    @Test
    void testGetCreditDeniedMessage() {
        String msg = adminMessageService.getCreditDeniedMessage(123L);
        assertThat(msg).isEqualTo("❌ Denied Credit for Order 123");
    }

    @Test
    void testGetOrderDetails() {
        String details = adminMessageService.getOrderDetails(100L, "Jane Doe", 500.0, "PENDING");
        assertThat(details).contains("ID: 100")
                .contains("Name: Jane Doe")
                .contains("Amount: ₹500.00")
                .contains("Status: PENDING");
    }

    @Test
    void testGetPaymentLinkGeneratedSuccess() {
        assertThat(adminMessageService.getPaymentLinkGeneratedSuccess())
                .isEqualTo("✅ Payment link generated and sent.");
    }

    @Test
    void testGetMenuTitle() {
        assertThat(adminMessageService.getMenuTitle()).isEqualTo("💳 *Credit Customer Management*");
    }
}
