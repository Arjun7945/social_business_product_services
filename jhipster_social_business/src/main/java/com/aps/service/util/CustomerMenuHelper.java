package com.aps.service.util;

import com.aps.service.dto.WhatsAppMessageDto;
import java.util.List;

public class CustomerMenuHelper {

    private CustomerMenuHelper() {
        // Private constructor
    }

    public static List<WhatsAppMessageDto.RowDto> getCustomerMenuRows() {
        return List.of(
                WhatsAppMessageDto.RowDto.builder().id("ADD_CUSTOMER").title("➕ Add Customer")
                        .description("Register a new customer").build(),
                WhatsAppMessageDto.RowDto.builder()
                        .id("SHOW_ALL_CUSTOMERS")
                        .title("📋 Show All")
                        .description("List all registered customers")
                        .build(),
                WhatsAppMessageDto.RowDto.builder()
                        .id("UPDATE_CUSTOMER_MENU")
                        .title("📝 Update Customer")
                        .description("Edit Details").build(),
                WhatsAppMessageDto.RowDto.builder()
                        .id("DELETE_CUSTOMER_MENU")
                        .title("🗑️ Delete Customer")
                        .description("Remove a customer")
                        .build(),
                WhatsAppMessageDto.RowDto.builder().id("BACK_TO_MAIN").title("⬅️ Back")
                        .description("Return to main menu").build());
    }
}
