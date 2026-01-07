package com.aps.service.util;

import com.aps.service.dto.WhatsAppMessageDto;
import java.util.List;

public class ExecutiveMenuHelper {

        private ExecutiveMenuHelper() {
                // Private constructor
        }

        public static List<WhatsAppMessageDto.ButtonDto> getExecutiveMenuButtons() {
                return List.of(
                                WhatsAppMessageDto.ButtonDto.builder()
                                                .type("reply")
                                                .reply(WhatsAppMessageDto.ReplyDto.builder().id("ADD_EXECUTIVE")
                                                                .title("➕ Add Executive")
                                                                .build())
                                                .build(),
                                WhatsAppMessageDto.ButtonDto.builder()
                                                .type("reply")
                                                .reply(WhatsAppMessageDto.ReplyDto.builder().id("SHOW_ALL_EXECUTIVE")
                                                                .title("📋 Show All")
                                                                .build())
                                                .build(),
                                WhatsAppMessageDto.ButtonDto.builder()
                                                .type("reply")
                                                .reply(WhatsAppMessageDto.ReplyDto.builder().id("BACK_TO_MAIN")
                                                                .title("⬅️ Back").build())
                                                .build());
        }
}
