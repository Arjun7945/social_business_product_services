package com.aps.service.util;

import com.aps.service.dto.WhatsAppMessageDto;
import com.aps.service.CreditCustomerFlowService;
import java.util.List;

public class CreditCustomerMenuHelper {

    private CreditCustomerMenuHelper() {
        // Private constructor
    }

    public static List<WhatsAppMessageDto.RowDto> getCreditCustomerMenuRows() {
        return List.of(
                WhatsAppMessageDto.RowDto.builder()
                        .id(CreditCustomerFlowService.MENU_CREDIT_OC)
                        .title("📦 Credit Orders")
                        .description("Pay Later Flow").build(),
                WhatsAppMessageDto.RowDto.builder()
                        .id("ADD_CREDIT_CUSTOMER")
                        .title("➕ Create Credit Cust")
                        .description("Register new Credit Customer").build(),
                WhatsAppMessageDto.RowDto.builder()
                        .id("SHOW_ALL_CREDIT_CUSTOMERS")
                        .title("📋 Show All")
                        .description("List Credit Customers").build(),
                WhatsAppMessageDto.RowDto.builder()
                        .id("UPDATE_CREDIT_CUSTOMER")
                        .title("📝 Update")
                        .description("Update Credit Customer").build(),
                WhatsAppMessageDto.RowDto.builder()
                        .id("DELETE_CUSTOMER_MENU")
                        .title("🗑️ Delete Credit Cust")
                        .description("Remove Credit Customer").build(),
                WhatsAppMessageDto.RowDto.builder()
                        .id("BACK_TO_MAIN")
                        .title("⬅️ Back")
                        .description("Main Menu").build());
    }
}
