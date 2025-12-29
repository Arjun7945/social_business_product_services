package com.aps.service.admin;

import com.aps.config.FlowConstants;
import com.aps.domain.BotSession;
import com.aps.domain.FishProduct;
import com.aps.domain.ProductImage;
import com.aps.domain.TeamMember;
import com.aps.domain.enumeration.AdminFlowStage;
import com.aps.repository.FishProductRepository;
import com.aps.repository.ProductImageRepository;
import com.aps.service.BotSessionManager;
import com.aps.service.WhatsAppMediaService;
import com.aps.service.WhatsAppService;
import com.aps.service.dto.WhatsAppMessageDto;
import com.aps.service.dto.WhatsAppWebhookDto;
import com.aps.service.util.InputValidator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class ProductManagementService {

    private final Logger log = LoggerFactory.getLogger(ProductManagementService.class);

    private final FishProductRepository fishProductRepository;
    private final ProductImageRepository productImageRepository;
    private final WhatsAppService whatsAppService;
    private final WhatsAppMediaService whatsAppMediaService;
    private final BotSessionManager sessionManager;
    private final InputValidator inputValidator;

    // In-memory lock to prevent race conditions on double image sends
    private final java.util.Set<String> processingImages = java.util.concurrent.ConcurrentHashMap.newKeySet();

    public ProductManagementService(
            FishProductRepository fishProductRepository,
            ProductImageRepository productImageRepository,
            WhatsAppService whatsAppService,
            WhatsAppMediaService whatsAppMediaService,
            BotSessionManager sessionManager,
            InputValidator inputValidator) {
        this.fishProductRepository = fishProductRepository;
        this.productImageRepository = productImageRepository;
        this.whatsAppService = whatsAppService;
        this.whatsAppMediaService = whatsAppMediaService;
        this.sessionManager = sessionManager;
        this.inputValidator = inputValidator;
    }

    public void showProductMenu(TeamMember admin) {
        List<WhatsAppMessageDto.ButtonDto> buttons = List.of(
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder().id("ADD_PRODUCT").title("➕ Add Product").build())
                        .build(),
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder().id("SHOW_ALL_PRODUCTS").title("📋 Show All")
                                .build())
                        .build(),
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder().id("BACK_TO_MAIN").title("⬅️ Back").build())
                        .build());

        whatsAppService.sendCartActionButtons(admin.getWaPhoneNumber(),
                "🐟 *Product Management*\n\nWhat would you like to do?", buttons);

        BotSession session = sessionManager.getSession(admin.getWaPhoneNumber());
        sessionManager.updateState(session, AdminFlowStage.PRODUCT_MENU.name());
    }

    public void startAddProduct(TeamMember admin, BotSession session) {
        sessionManager.setSessionData(session, "tempEntityType", "PRODUCT");
        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "➕ *Add New Product*\n\n📝 Please provide the product name:");
        sessionManager.updateState(session, AdminFlowStage.AWAITING_PRODUCT_NAME.name());
    }

    public void handleProductNameInput(TeamMember admin, BotSession session, String name) {
        if (!inputValidator.isValidName(name)) {
            whatsAppService.sendSimpleText(
                    admin.getWaPhoneNumber(),
                    "❌ Invalid name. Use letters, spaces, numbers, or hyphens only. Try again:");
            return;
        }
        sessionManager.setSessionData(session, "tempProductName", name.trim());
        whatsAppService.sendSimpleText(
                admin.getWaPhoneNumber(),
                "✅ Name: " + name.trim() + "\n\n💰 Please provide the price per kg (in ₹):");
        sessionManager.updateState(session, AdminFlowStage.AWAITING_PRODUCT_PRICE.name());
    }

    public void handleProductPriceInput(TeamMember admin, BotSession session, String price) {
        try {
            Double priceValue = Double.parseDouble(price.trim());
            if (priceValue <= 0) {
                whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                        "❌ Price must be greater than zero. Try again:");
                return;
            }
            sessionManager.setSessionData(session, "tempProductPrice", priceValue);
            whatsAppService.sendSimpleText(
                    admin.getWaPhoneNumber(),
                    "✅ Price: ₹" + priceValue + "/kg\n\n📄 Please provide a description:");
            sessionManager.updateState(session, AdminFlowStage.AWAITING_PRODUCT_DESCRIPTION.name());
        } catch (NumberFormatException e) {
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "❌ Invalid price. Please enter a valid number:");
        }
    }

    public void handleProductDescriptionInput(TeamMember admin, BotSession session, String description) {
        // WhatsApp carousel card body limit: 160 characters
        final int WHATSAPP_CAROUSEL_LIMIT = 160;

        // Get product name and price from session
        String productName = sessionManager.getSessionDataString(session, "tempProductName");
        Double productPrice = sessionManager.getSessionDataDouble(session, "tempProductPrice");

        // Calculate used characters (name + price + 2 newlines)
        String priceText = String.format("₹%.2f/kg", productPrice);
        int usedChars = productName.length() + priceText.length() + 2; // +2 for newlines
        int availableChars = WHATSAPP_CAROUSEL_LIMIT - usedChars;

        // Validate description length
        String trimmedDescription = description.trim();
        if (trimmedDescription.length() > availableChars) {
            // Send detailed error message
            String errorMsg = String.format(
                    "❌ Description too long!\n" +
                            "Product: \"%s\" (%d chars)\n" +
                            "Price: \"%s\" (%d chars)\n" +
                            "Available for description: %d characters\n\n" +
                            "Please add description which is less than or equal to %d characters.",
                    productName, productName.length(),
                    priceText, priceText.length(),
                    availableChars, availableChars);
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), errorMsg);
            // Stay in AWAITING_PRODUCT_DESCRIPTION state - don't proceed
            return;
        }

        // Description is valid, save and proceed
        sessionManager.setSessionData(session, "tempProductDesc", trimmedDescription);
        List<WhatsAppMessageDto.ButtonDto> buttons = List.of(
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder().id("AVAIL_YES").title("Yes").build())
                        .build(),
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder().id("AVAIL_NO").title("No").build())
                        .build());

        whatsAppService.sendCartActionButtons(admin.getWaPhoneNumber(),
                "✅ Description saved\n\n🔄 Is this product available?", buttons);
        sessionManager.updateState(session, AdminFlowStage.AWAITING_PRODUCT_AVAILABILITY.name());
    }

    public void handleProductAvailabilityInput(TeamMember admin, BotSession session, String availability) {
        boolean isAvailable = availability.trim().equalsIgnoreCase(FlowConstants.CMD_YES)
                || availability.trim().equalsIgnoreCase(FlowConstants.CMD_Y);
        sessionManager.setSessionData(session, "tempProductAvail", isAvailable);

        whatsAppService.sendSimpleText(
                admin.getWaPhoneNumber(),
                "✅ Availability: " +
                        (isAvailable ? "Available" : "Not Available") +
                        "\n\n📸 *Send product image* (Add only 1 image)\n\n" +
                        "• Send the image\n" +
                        "• Processing will start immediately after receipt");
        sessionManager.updateState(session, AdminFlowStage.AWAITING_PRODUCT_IMAGES.name());
        // Initialize image list in session
        sessionManager.setSessionData(session, "tempMediaIds", "");
    }

    public void handleProductImageMessage(TeamMember admin, BotSession session, WhatsAppWebhookDto.Message message) {
        if (message.getType().equals("image") && message.getImage() != null) {
            String waPhone = admin.getWaPhoneNumber();

            // Acquire lock for this user
            if (!processingImages.add(waPhone)) {
                // If we couldn't add, it means we are already processing an image for this
                // user.
                // This handles the concurrent 2nd image.
                whatsAppService.sendSimpleText(
                        waPhone,
                        "⚠️ *Only 1 image allowed per product.*\n\nFirst image is being processed. This one is ignored.");
                return;
            }

            try {
                String mediaId = message.getImage().getId();

                String currentMediaIds = sessionManager.getSessionDataString(session, "tempMediaIds");
                if (currentMediaIds == null)
                    currentMediaIds = "";

                if (!currentMediaIds.isEmpty()) {
                    // Image already exists (legacy check + safety)
                    whatsAppService.sendSimpleText(
                            admin.getWaPhoneNumber(),
                            "⚠️ *Product creation in progress.*\n\nNew image ignored as processing a previous image is underway.");
                } else {
                    // First image, save it and FINALIZE IMMEDIATELY
                    sessionManager.setSessionData(session, "tempMediaIds", mediaId);
                    whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                            "✅ Image received! Finalizing product... please wait.");

                    // Trigger finalization
                    sessionManager.updateState(session, AdminFlowStage.PROCESSING.name());
                    finalizeProductAdd(admin, session);
                }
            } finally {
                // Release lock
                processingImages.remove(waPhone);
            }
        }
    }

    @Async
    @Transactional
    public void finalizeProductAdd(TeamMember admin, BotSession session) {
        try {
            String name = sessionManager.getSessionDataString(session, "tempProductName");
            Double price = sessionManager.getSessionDataDouble(session, "tempProductPrice"); // Need helper for Double
            String desc = sessionManager.getSessionDataString(session, "tempProductDesc");
            Boolean isAvailable = (Boolean) sessionManager.getSessionData(session, "tempProductAvail");

            // Safety check
            if (name == null || price == null) {
                whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "❌ Error: Missing product data.");
                return;
            }

            FishProduct newProduct = new FishProduct();
            newProduct.setName(name);
            newProduct.setPricePerKg(java.math.BigDecimal.valueOf(price));
            newProduct.setDescription(desc);
            newProduct.setIsAvailable(isAvailable);

            fishProductRepository.saveAndFlush(newProduct);

            int successCount = 0;
            int failCount = 0;

            String mediaIds = sessionManager.getSessionDataString(session, "tempMediaIds");
            if (mediaIds != null && !mediaIds.isEmpty()) {
                String[] mediaIdArray = mediaIds.split(",");
                for (int i = 0; i < mediaIdArray.length; i++) {
                    boolean success = downloadAndSaveImage(mediaIdArray[i], newProduct, i);
                    if (success) {
                        successCount++;
                    } else {
                        failCount++;
                    }
                }
            }

            String msgBase = "✅ *Product Added Successfully!*\n\n" + "🐟 " + newProduct.getName() + " has been added.";

            if (successCount > 0) {
                msgBase += "\n📸 Image saved.";
            }
            if (failCount > 0) {
                msgBase += "\n⚠️ Image download failed.";
            }

            final String finalMsg = msgBase;

            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), finalMsg);
                            showProductMenu(admin);
                        }
                    });
        } catch (Exception e) {
            log.error("Error finalizing product add", e);
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "❌ Error adding product: " + e.getMessage());
            showProductMenu(admin);
        }
    }

    private boolean downloadAndSaveImage(String mediaId, FishProduct product, int index) {
        try {
            WhatsAppMediaService.MediaContent content = whatsAppMediaService.downloadImage(mediaId);
            if (content != null && content.getData() != null) {
                ProductImage image = new ProductImage();
                image.setProduct(product);
                image.setImageData(content.getData());
                image.setMimeType(content.getMimeType() != null ? content.getMimeType() : "image/jpeg");
                image.setDisplayOrder(index);

                // Generate UUID and set URL immediately
                String uuid = java.util.UUID.randomUUID().toString();
                String finalUrl = "/api/product-images/public/uuid/" + uuid + "/content";
                image.setImageUrl(finalUrl);

                // Save once - no null violation, no circular dependency
                productImageRepository.save(image);
                return true;
            }
        } catch (Exception e) {
            log.error("Failed to download image {}", mediaId, e);
        }
        return false;
    }

    public void showAllProducts(TeamMember admin) {
        List<FishProduct> products = fishProductRepository.findAll();

        if (products.isEmpty()) {
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "📋 *No products found.*");
            showProductMenu(admin);
            return;
        }

        StringBuilder message = new StringBuilder(String.format("📋 *All Products* (Total: %d)\n\n", products.size()));
        int count = 1;
        for (FishProduct p : products) {
            long imageCount = productImageRepository.countByProductId(p.getId());
            message.append(
                    String.format(
                            "%d. *%s*\n   💰 ₹%.2f/kg\n   📸 %d images\n   %s\n\n",
                            count++,
                            p.getName(),
                            p.getPricePerKg(),
                            imageCount,
                            p.getIsAvailable() != null && p.getIsAvailable() ? "✅ Available" : "❌ Not Available"));
        }

        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), message.toString());
        showProductMenu(admin);
    }
}
