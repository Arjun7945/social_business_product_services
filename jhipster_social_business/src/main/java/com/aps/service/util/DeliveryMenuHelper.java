package com.aps.service.util;

import com.aps.service.dto.WhatsAppMessageDto;
import java.util.List;

public class DeliveryMenuHelper {

    private DeliveryMenuHelper() {
        // Private constructor
    }

    public static List<WhatsAppMessageDto.RowDto> getDeliveryPersonMenuRows() {
        return List.of(
                WhatsAppMessageDto.RowDto.builder()
                        .id("ADD_DELIVERY")
                        .title("➕ Add Delivery Person")
                        .description("Add a new staff member")
                        .build(),
                WhatsAppMessageDto.RowDto.builder().id("SHOW_ALL_DELIVERY").title("📋 Show All")
                        .description("List all delivery staff").build(),
                WhatsAppMessageDto.RowDto.builder()
                        .id("DELETE_DELIVERY_MENU")
                        .title("🗑️ Delete")
                        .description("Remove a delivery person")
                        .build(),
                WhatsAppMessageDto.RowDto.builder().id("BACK_TO_MAIN").title("⬅️ Back")
                        .description("Return to main menu").build());
    }
}
