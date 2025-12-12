package com.seller.whatsappservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class WhatsAppWebhookDto {
    private String object;
    private List<Entry> entry;

    @Data
    public static class Entry {
        private String id;
        private List<Change> changes;
    }

    @Data
    public static class Change {
        private Value value;
        private String field;
    }

    @Data
    public static class Value {
        @JsonProperty("messaging_product")
        private String messagingProduct;
        private Metadata metadata;
        private List<Contact> contacts;
        private List<Message> messages;
    }

    @Data
    public static class Metadata {
        @JsonProperty("display_phone_number")
        private String displayPhoneNumber;
        @JsonProperty("phone_number_id")
        private String phoneNumberId;
    }

    @Data
    public static class Contact {
        private Profile profile;
        @JsonProperty("wa_id")
        private String waId;
    }

    @Data
    public static class Profile {
        private String name;
    }

    @Data
    public static class Message {
        private String from;
        private String id;
        private String timestamp;
        private String type;
        private Text text;
        private Interactive interactive;
        private Button button;
        private Location location;
        private Image image;
    }

    @Data
    public static class Text {
        private String body;
    }

    @Data
    public static class Interactive {
        private String type;
        @JsonProperty("list_reply")
        private ListReply listReply;
        @JsonProperty("button_reply")
        private ButtonReply buttonReply;
    }

    @Data
    public static class ListReply {
        private String id;
        private String title;
        private String description;
    }

    @Data
    public static class ButtonReply {
        private String id;
        private String title;
    }

    @Data
    public static class Button {
        private String payload;
        private String text;
    }

    @Data
    public static class Location {
        private Double latitude;
        private Double longitude;
        private String name;
        private String address;
    }

    @Data
    public static class Image {
        private String id;
        @JsonProperty("mime_type")
        private String mimeType;
        private String sha256;
    }
}
