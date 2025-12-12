package com.seller.whatsappservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class WhatsAppMessageDto {
    @JsonProperty("messaging_product")
    @Builder.Default
    private String messagingProduct = "whatsapp";

    @JsonProperty("recipient_type")
    @Builder.Default
    private String recipientType = "individual";

    private String to;

    private String type;

    private TextDto text;

    private InteractiveDto interactive;

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class TextDto {
        @JsonProperty("preview_url")
        private boolean previewUrl;
        private String body;
    }

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class InteractiveDto {
        private String type;
        private ActionDto action;
        private BodyDto body;
    }

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class BodyDto {
        private String text;
    }

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class ActionDto {
        private String button;
        private List<ButtonDto> buttons;
        private List<SectionDto> sections;
        private List<CarouselCardDto> cards;
    }

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class ButtonDto {
        private String type;
        private ReplyDto reply;
        @JsonProperty("quick_reply")
        private ReplyDto quickReply;
    }

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class ReplyDto {
        private String id;
        private String title;
    }

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class SectionDto {
        private String title;
        private List<RowDto> rows;
    }

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class RowDto {
        private String id;
        private String title;
        private String description;
    }

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class CarouselCardDto {
        @JsonProperty("card_index")
        private Integer cardIndex;
        private String type; // Re-added as API requires it
        private HeaderDto header;
        private BodyDto body;
        private ActionDto action; // Buttons must be wrapped in action
    }

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class HeaderDto {
        private String type; // "image" or "video"
        private ImageDto image;
        private VideoDto video;
    }

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class ImageDto {
        private String id; // Media ID
        private String link; // Image URL
    }

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class VideoDto {
        private String id; // Media ID
    }
}
