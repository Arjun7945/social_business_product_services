package com.aps.service.util;

import com.aps.service.dto.WhatsAppMessageDto;
import java.util.List;

public class AdminMenuHelper {

    private AdminMenuHelper() {
        // Private constructor to hide the implicit public one
    }

    public static List<WhatsAppMessageDto.RowDto> getMainMenuRows() {
        return List.of(
                WhatsAppMessageDto.RowDto.builder().id("CUSTOMER_SECTION").title("👥 Customers")
                        .description("Manage Customers").build(),
                WhatsAppMessageDto.RowDto.builder().id("DELIVERY_SECTION").title("🚚 Delivery")
                        .description("Manage Delivery Staff").build(),
                WhatsAppMessageDto.RowDto.builder().id("PRODUCT_SECTION").title("🐟 Products")
                        .description("Manage Inventory").build(),
                WhatsAppMessageDto.RowDto.builder().id("EXECUTIVE_SECTION").title("💼 Executives")
                        .description("Manage Executives").build(),
                WhatsAppMessageDto.RowDto.builder().id("ASSISTANT_SECTION").title("️ Assistants")
                        .description("Manage Assistants").build(),
                WhatsAppMessageDto.RowDto.builder().id("ACCOUNTS_SECTION").title(" Accounts")
                        .description("Manage Accounts Team").build(),
                WhatsAppMessageDto.RowDto.builder().id("CREDIT_CUSTOMER_SECTION").title("💳 Credit Customers")
                        .description("Manage Credit Flow").build(),
                WhatsAppMessageDto.RowDto.builder()
                        .id("CONTACT_DEVELOPER")
                        .title("👨‍ Contact Dev")
                        .description("Get Technical Support")
                        .build());
    }
}
