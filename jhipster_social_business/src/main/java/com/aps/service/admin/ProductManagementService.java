package com.aps.service.admin;

import com.aps.domain.BotSession;
import com.aps.config.FlowConstants;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductManagementService {

    private final Logger log = LoggerFactory.getLogger(ProductManagementService.class);

    private final FishProductRepository fishProductRepository;
    private final ProductImageRepository productImageRepository;
    private final WhatsAppService whatsAppService;
    private final WhatsAppMediaService whatsAppMediaService;
    private final BotSessionManager sessionManager;

    public ProductManagementService(FishProductRepository fishProductRepository,
            ProductImageRepository productImageRepository,
            WhatsAppService whatsAppService,
            WhatsAppMediaService whatsAppMediaService,
            BotSessionManager sessionManager) {
        this.fishProductRepository = fishProductRepository;
        this.productImageRepository = productImageRepository;
        this.whatsAppService = whatsAppService;
        this.whatsAppMediaService = whatsAppMediaService;
        this.sessionManager = sessionManager;
    }

    public void showProductMenu(TeamMember admin) {
        List<WhatsAppMessageDto.ButtonDto> buttons = List.of(
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder()
                                .id("ADD_PRODUCT")
                                .title("➕ Add Product")
                                .build())
                        .build(),
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder()
                                .id("SHOW_ALL_PRODUCTS")
                                .title("📋 Show All")
                                .build())
                        .build(),
                WhatsAppMessageDto.ButtonDto.builder()
                        .type("reply")
                        .reply(WhatsAppMessageDto.ReplyDto.builder()
                                .id("BACK_TO_MAIN")
                                .title("⬅️ Back")
                                .build())
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
        sessionManager.setSessionData(session, "tempProductName", name.trim());
        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "✅ Name: " + name.trim() + "\n\n💰 Please provide the price per kg (in ₹):");
        sessionManager.updateState(session, AdminFlowStage.AWAITING_PRODUCT_PRICE.name());
    }

    public void handleProductPriceInput(TeamMember admin, BotSession session, String price) {
        try {
            Double priceValue = Double.parseDouble(price.trim());
            sessionManager.setSessionData(session, "tempProductPrice", priceValue);
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                    "✅ Price: ₹" + priceValue + "/kg\n\n📄 Please provide a description:");
            sessionManager.updateState(session, AdminFlowStage.AWAITING_PRODUCT_DESCRIPTION.name());
        } catch (NumberFormatException e) {
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                    "❌ Invalid price. Please enter a valid number:");
        }
    }

    public void handleProductDescriptionInput(TeamMember admin, BotSession session, String description) {
        sessionManager.setSessionData(session, "tempProductDesc", description.trim());
        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "✅ Description saved\n\n🔄 Is this product available? (yes/no):");
        sessionManager.updateState(session, AdminFlowStage.AWAITING_PRODUCT_AVAILABILITY.name());
    }

    public void handleProductAvailabilityInput(TeamMember admin, BotSession session, String availability) {
        boolean isAvailable = availability.trim().equalsIgnoreCase(FlowConstants.CMD_YES) ||
                availability.trim().equalsIgnoreCase(FlowConstants.CMD_Y);
        sessionManager.setSessionData(session, "tempProductAvail", isAvailable);

        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "✅ Availability: " + (isAvailable ? "Available" : "Not Available") +
                        "\n\n📸 *Send product images* (1-10 images)\n\n" +
                        "\n\n📸 *Send product images* (1-10 images)\n\n" +
                        "• Send images one by one\n" +
                        "• When done, type '" + FlowConstants.CMD_DONE + "'\n" +
                        "• To skip images, type '" + FlowConstants.CMD_SKIP + "'");
        sessionManager.updateState(session, AdminFlowStage.AWAITING_PRODUCT_IMAGES.name());
        // Initialize image list in session? Or just string of IDs.
        sessionManager.setSessionData(session, "tempMediaIds", "");
    }

    public void handleProductImageMessage(TeamMember admin, BotSession session, WhatsAppWebhookDto.Message message) {
        if (message.getType().equals("image") && message.getImage() != null) {
            String mediaId = message.getImage().getId();

            String currentMediaIds = sessionManager.getSessionDataString(session, "tempMediaIds");
            if (currentMediaIds == null)
                currentMediaIds = "";
            String updatedMediaIds = currentMediaIds.isEmpty() ? mediaId : currentMediaIds + "," + mediaId;
            sessionManager.setSessionData(session, "tempMediaIds", updatedMediaIds);

            int imageCount = updatedMediaIds.split(",").length;

            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                    "✅ Image " + imageCount + " received!\n\n" +
                            (imageCount < 10 ? "Send more images or type 'DONE' to finish."
                                    : "Maximum 10 images reached. Type 'DONE' to finish."));

            if (imageCount >= 10) {
                finalizeProductAdd(admin, session);
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

            fishProductRepository.save(newProduct);

            String mediaIds = sessionManager.getSessionDataString(session, "tempMediaIds");
            if (mediaIds != null && !mediaIds.isEmpty()) {
                String[] mediaIdArray = mediaIds.split(",");
                for (int i = 0; i < mediaIdArray.length; i++) {
                    downloadAndSaveImage(mediaIdArray[i], newProduct, i);
                }
            }

            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                    "✅ *Product Added Successfully!*\n\n" +
                            "🐟 " + newProduct.getName() + " has been added with " +
                            (mediaIds != null && !mediaIds.isEmpty() ? mediaIds.split(",").length : 0) + " images.");

            showProductMenu(admin);
        } catch (Exception e) {
            log.error("Error finalizing product add", e);
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), "❌ Error adding product: " + e.getMessage());
            showProductMenu(admin);
        }
    }

    private void downloadAndSaveImage(String mediaId, FishProduct product, int index) {
        try {
            WhatsAppMediaService.MediaContent content = whatsAppMediaService.downloadImage(mediaId);
            if (content != null && content.getData() != null) {
                ProductImage image = new ProductImage();
                image.setProduct(product);
                image.setImageData(content.getData());
                image.setMimeType(content.getMimeType() != null ? content.getMimeType() : "image/jpeg");
                image.setDisplayOrder(index);

                // Save first to get ID
                image = productImageRepository.save(image);

                // Update URL to point to controller
                image.setImageUrl("/api/public/images/" + image.getId());
                productImageRepository.save(image);
            }
        } catch (Exception e) {
            log.error("Failed to download image {}", mediaId, e);
        }
    }

    public void showAllProducts(TeamMember admin) {
        List<FishProduct> products = fishProductRepository.findAll();

        if (products.isEmpty()) {
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                    "📋 *No products found.*");
            showProductMenu(admin);
            return;
        }

        StringBuilder message = new StringBuilder(
                String.format("📋 *All Products* (Total: %d)\n\n", products.size()));
        int count = 1;
        for (FishProduct p : products) {
            long imageCount = productImageRepository.countByProductId(p.getId());
            message.append(String.format(
                    "%d. *%s*\n   💰 ₹%.2f/kg\n   📸 %d images\n   %s\n\n",
                    count++, p.getName(), p.getPricePerKg(), imageCount,
                    p.getIsAvailable() != null && p.getIsAvailable() ? "✅ Available" : "❌ Not Available"));
        }

        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), message.toString());
        showProductMenu(admin);
    }
}
