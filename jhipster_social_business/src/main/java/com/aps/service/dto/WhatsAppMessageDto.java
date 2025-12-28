package com.aps.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class WhatsAppMessageDto {

    @JsonProperty("messaging_product")
    private String messagingProduct = "whatsapp";

    @JsonProperty("recipient_type")
    private String recipientType = "individual";

    private String to;
    private String type;
    private TextDto text;
    private InteractiveDto interactive;
    private DocumentDto document;
    private ImageDto image;
    private LocationDto location;

    // Default constructor
    public WhatsAppMessageDto() {}

    public static WhatsAppMessageDto builder() {
        return new WhatsAppMessageDto();
    }

    public WhatsAppMessageDto build() {
        return this;
    }

    // Getters and Setters
    public String getMessagingProduct() {
        return messagingProduct;
    }

    public void setMessagingProduct(String messagingProduct) {
        this.messagingProduct = messagingProduct;
    }

    public String getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(String recipientType) {
        this.recipientType = recipientType;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public TextDto getText() {
        return text;
    }

    public void setText(TextDto text) {
        this.text = text;
    }

    public InteractiveDto getInteractive() {
        return interactive;
    }

    public void setInteractive(InteractiveDto interactive) {
        this.interactive = interactive;
    }

    // Builder pattern manually implemented (simplified)
    public WhatsAppMessageDto messagingProduct(String messagingProduct) {
        this.messagingProduct = messagingProduct;
        return this;
    }

    public WhatsAppMessageDto recipientType(String recipientType) {
        this.recipientType = recipientType;
        return this;
    }

    public WhatsAppMessageDto to(String to) {
        this.to = to;
        return this;
    }

    public WhatsAppMessageDto type(String type) {
        this.type = type;
        return this;
    }

    public WhatsAppMessageDto text(TextDto text) {
        this.text = text;
        return this;
    }

    public WhatsAppMessageDto interactive(InteractiveDto interactive) {
        this.interactive = interactive;
        return this;
    }

    public DocumentDto getDocument() {
        return document;
    }

    public void setDocument(DocumentDto document) {
        this.document = document;
    }

    public WhatsAppMessageDto document(DocumentDto document) {
        this.document = document;
        return this;
    }

    public ImageDto getImage() {
        return image;
    }

    public void setImage(ImageDto image) {
        this.image = image;
    }

    public WhatsAppMessageDto image(ImageDto image) {
        this.image = image;
        return this;
    }

    public LocationDto getLocation() {
        return location;
    }

    public void setLocation(LocationDto location) {
        this.location = location;
    }

    public WhatsAppMessageDto location(LocationDto location) {
        this.location = location;
        return this;
    }

    // Nested Classes
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class TextDto {

        @JsonProperty("preview_url")
        private boolean previewUrl;

        private String body;

        public static TextDto builder() {
            return new TextDto();
        }

        public TextDto build() {
            return this;
        }

        public boolean isPreviewUrl() {
            return previewUrl;
        }

        public void setPreviewUrl(boolean previewUrl) {
            this.previewUrl = previewUrl;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public TextDto body(String body) {
            this.body = body;
            return this;
        }

        public TextDto previewUrl(boolean previewUrl) {
            this.previewUrl = previewUrl;
            return this;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class InteractiveDto {

        private String type;
        private ActionDto action;
        private BodyDto body;

        public static InteractiveDto builder() {
            return new InteractiveDto();
        }

        public InteractiveDto build() {
            return this;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public ActionDto getAction() {
            return action;
        }

        public void setAction(ActionDto action) {
            this.action = action;
        }

        public BodyDto getBody() {
            return body;
        }

        public void setBody(BodyDto body) {
            this.body = body;
        }

        public InteractiveDto type(String type) {
            this.type = type;
            return this;
        }

        public InteractiveDto action(ActionDto action) {
            this.action = action;
            return this;
        }

        public InteractiveDto body(BodyDto body) {
            this.body = body;
            return this;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class BodyDto {

        private String text;

        public static BodyDto builder() {
            return new BodyDto();
        }

        public BodyDto build() {
            return this;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public BodyDto text(String text) {
            this.text = text;
            return this;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class ActionDto {

        private String button;
        private List<ButtonDto> buttons;
        private List<SectionDto> sections;
        private List<CarouselCardDto> cards;

        public static ActionDto builder() {
            return new ActionDto();
        }

        public ActionDto build() {
            return this;
        }

        public String getButton() {
            return button;
        }

        public void setButton(String button) {
            this.button = button;
        }

        public List<ButtonDto> getButtons() {
            return buttons;
        }

        public void setButtons(List<ButtonDto> buttons) {
            this.buttons = buttons;
        }

        public List<SectionDto> getSections() {
            return sections;
        }

        public void setSections(List<SectionDto> sections) {
            this.sections = sections;
        }

        public List<CarouselCardDto> getCards() {
            return cards;
        }

        public void setCards(List<CarouselCardDto> cards) {
            this.cards = cards;
        }

        public ActionDto button(String button) {
            this.button = button;
            return this;
        }

        public ActionDto buttons(List<ButtonDto> buttons) {
            this.buttons = buttons;
            return this;
        }

        public ActionDto sections(List<SectionDto> sections) {
            this.sections = sections;
            return this;
        }

        public ActionDto cards(List<CarouselCardDto> cards) {
            this.cards = cards;
            return this;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class ButtonDto {

        private String type;
        private ReplyDto reply;

        @JsonProperty("quick_reply")
        private ReplyDto quickReply;

        public static ButtonDto builder() {
            return new ButtonDto();
        }

        public ButtonDto build() {
            return this;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public ReplyDto getReply() {
            return reply;
        }

        public void setReply(ReplyDto reply) {
            this.reply = reply;
        }

        public ReplyDto getQuickReply() {
            return quickReply;
        }

        public void setQuickReply(ReplyDto quickReply) {
            this.quickReply = quickReply;
        }

        public ButtonDto type(String type) {
            this.type = type;
            return this;
        }

        public ButtonDto reply(ReplyDto reply) {
            this.reply = reply;
            return this;
        }

        public ButtonDto quickReply(ReplyDto quickReply) {
            this.quickReply = quickReply;
            return this;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class ReplyDto {

        private String id;
        private String title;

        public static ReplyDto builder() {
            return new ReplyDto();
        }

        public ReplyDto build() {
            return this;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public ReplyDto id(String id) {
            this.id = id;
            return this;
        }

        public ReplyDto title(String title) {
            this.title = title;
            return this;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class SectionDto {

        private String title;
        private List<RowDto> rows;

        public static SectionDto builder() {
            return new SectionDto();
        }

        public SectionDto build() {
            return this;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<RowDto> getRows() {
            return rows;
        }

        public void setRows(List<RowDto> rows) {
            this.rows = rows;
        }

        public SectionDto title(String title) {
            this.title = title;
            return this;
        }

        public SectionDto rows(List<RowDto> rows) {
            this.rows = rows;
            return this;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class RowDto {

        private String id;
        private String title;
        private String description;

        public static RowDto builder() {
            return new RowDto();
        }

        public RowDto build() {
            return this;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public RowDto id(String id) {
            this.id = id;
            return this;
        }

        public RowDto title(String title) {
            this.title = title;
            return this;
        }

        public RowDto description(String description) {
            this.description = description;
            return this;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class CarouselCardDto {

        @JsonProperty("card_index")
        private Integer cardIndex;

        private String type;
        private HeaderDto header;
        private BodyDto body;
        private ActionDto action;

        public static CarouselCardDto builder() {
            return new CarouselCardDto();
        }

        public CarouselCardDto build() {
            return this;
        }

        public Integer getCardIndex() {
            return cardIndex;
        }

        public void setCardIndex(Integer cardIndex) {
            this.cardIndex = cardIndex;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public HeaderDto getHeader() {
            return header;
        }

        public void setHeader(HeaderDto header) {
            this.header = header;
        }

        public BodyDto getBody() {
            return body;
        }

        public void setBody(BodyDto body) {
            this.body = body;
        }

        public ActionDto getAction() {
            return action;
        }

        public void setAction(ActionDto action) {
            this.action = action;
        }

        public CarouselCardDto cardIndex(Integer index) {
            this.cardIndex = index;
            return this;
        }

        public CarouselCardDto type(String type) {
            this.type = type;
            return this;
        }

        public CarouselCardDto header(HeaderDto header) {
            this.header = header;
            return this;
        }

        public CarouselCardDto body(BodyDto body) {
            this.body = body;
            return this;
        }

        public CarouselCardDto action(ActionDto action) {
            this.action = action;
            return this;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class HeaderDto {

        private String type;
        private ImageDto image;
        private VideoDto video;

        public static HeaderDto builder() {
            return new HeaderDto();
        }

        public HeaderDto build() {
            return this;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public ImageDto getImage() {
            return image;
        }

        public void setImage(ImageDto image) {
            this.image = image;
        }

        public VideoDto getVideo() {
            return video;
        }

        public void setVideo(VideoDto video) {
            this.video = video;
        }

        public HeaderDto type(String type) {
            this.type = type;
            return this;
        }

        public HeaderDto image(ImageDto image) {
            this.image = image;
            return this;
        }

        public HeaderDto video(VideoDto video) {
            this.video = video;
            return this;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class ImageDto {

        private String id;
        private String link;
        private String caption;

        public static ImageDto builder() {
            return new ImageDto();
        }

        public ImageDto build() {
            return this;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getCaption() {
            return caption;
        }

        public void setCaption(String caption) {
            this.caption = caption;
        }

        public ImageDto id(String id) {
            this.id = id;
            return this;
        }

        public ImageDto link(String link) {
            this.link = link;
            return this;
        }

        public ImageDto caption(String caption) {
            this.caption = caption;
            return this;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class DocumentDto {

        private String id;
        private String link;
        private String caption;
        private String filename;

        public static DocumentDto builder() {
            return new DocumentDto();
        }

        public DocumentDto build() {
            return this;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getCaption() {
            return caption;
        }

        public void setCaption(String caption) {
            this.caption = caption;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public DocumentDto id(String id) {
            this.id = id;
            return this;
        }

        public DocumentDto link(String link) {
            this.link = link;
            return this;
        }

        public DocumentDto caption(String caption) {
            this.caption = caption;
            return this;
        }

        public DocumentDto filename(String filename) {
            this.filename = filename;
            return this;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class VideoDto {

        private String id;

        public static VideoDto builder() {
            return new VideoDto();
        }

        public VideoDto build() {
            return this;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public VideoDto id(String id) {
            this.id = id;
            return this;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class LocationDto {

        private Double longitude;
        private Double latitude;
        private String name;
        private String address;

        public static LocationDto builder() {
            return new LocationDto();
        }

        public LocationDto build() {
            return this;
        }

        public Double getLongitude() {
            return longitude;
        }

        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }

        public Double getLatitude() {
            return latitude;
        }

        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public LocationDto longitude(Double longitude) {
            this.longitude = longitude;
            return this;
        }

        public LocationDto latitude(Double latitude) {
            this.latitude = latitude;
            return this;
        }

        public LocationDto name(String name) {
            this.name = name;
            return this;
        }

        public LocationDto address(String address) {
            this.address = address;
            return this;
        }
    }
}
