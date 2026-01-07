package com.aps.service.util;

import com.aps.service.dto.WhatsAppMessageDto;
import java.util.List;

public class AssistantAdminMenuHelper {

    private AssistantAdminMenuHelper() {
        // Private constructor
    }

    public static List<WhatsAppMessageDto.ButtonDto> getAssistantAdminMenuButtons() {
        return List.of(
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder().id("ADD_ASSISTANT").title("➕ Add").build())
                        .build(),
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder().id("SHOW_ALL_ASSISTANT").title("📋 Show All")
                                .build())
                        .build(),
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder().id("BACK_TO_MAIN").title("⬅️ Back").build())
                        .build());
    }
}
