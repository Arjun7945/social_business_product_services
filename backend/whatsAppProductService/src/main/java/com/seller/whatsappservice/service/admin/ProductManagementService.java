package com.seller.whatsappservice.service.admin;

import com.seller.whatsappservice.dto.WhatsAppMessageDto;
import com.seller.whatsappservice.dto.WhatsAppWebhookDto;
import com.seller.whatsappservice.model.FishProduct;
import com.seller.whatsappservice.model.TeamMember;
import com.seller.whatsappservice.model.enums.AdminFlowStage;
import com.seller.whatsappservice.repository.FishProductRepository;
import com.seller.whatsappservice.repository.ProductImageRepository;
import com.seller.whatsappservice.service.ProductImageService;
import com.seller.whatsappservice.service.WhatsAppService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductManagementService {

    private final FishProductRepository fishProductRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductImageService productImageService;
    private final WhatsAppService whatsAppService;

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
        admin.setCurrentAdminFlowStage(AdminFlowStage.PRODUCT_MENU);
    }

    public void startAddProduct(TeamMember admin) {
        admin.setTempEntityType("PRODUCT");
        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "➕ *Add New Product*\n\n📝 Please provide the product name:");
        admin.setCurrentAdminFlowStage(AdminFlowStage.AWAITING_PRODUCT_NAME);
    }

    public void handleProductNameInput(TeamMember admin, String name) {
        admin.setTempFieldName(name.trim());
        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "✅ Name: " + name.trim() + "\n\n💰 Please provide the price per kg (in ₹):");
        admin.setCurrentAdminFlowStage(AdminFlowStage.AWAITING_PRODUCT_PRICE);
    }

    public void handleProductPriceInput(TeamMember admin, String price) {
        try {
            Double priceValue = Double.parseDouble(price.trim());
            admin.setTempFieldValue(price.trim());
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                    "✅ Price: ₹" + priceValue + "/kg\n\n📄 Please provide a description:");
            admin.setCurrentAdminFlowStage(AdminFlowStage.AWAITING_PRODUCT_DESCRIPTION);
        } catch (NumberFormatException e) {
            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                    "❌ Invalid price. Please enter a valid number:");
        }
    }

    public void handleProductDescriptionInput(TeamMember admin, String description) {
        admin.setTempCustomerName(description.trim());
        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "✅ Description saved\n\n🔄 Is this product available? (yes/no):");
        admin.setCurrentAdminFlowStage(AdminFlowStage.AWAITING_PRODUCT_AVAILABILITY);
    }

    public void handleProductAvailabilityInput(TeamMember admin, String availability) {
        boolean isAvailable = availability.trim().equalsIgnoreCase("yes") ||
                availability.trim().equalsIgnoreCase("y");
        admin.setTempTeamMemberIsActive(isAvailable);

        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "✅ Availability: " + (isAvailable ? "Available" : "Not Available") +
                        "\n\n📸 *Send product images* (1-10 images)\n\n" +
                        "• Send images one by one\n" +
                        "• When done, type 'DONE'\n" +
                        "• To skip images, type 'SKIP'");
        admin.setCurrentAdminFlowStage(AdminFlowStage.AWAITING_PRODUCT_IMAGES);
    }

    public void handleProductImageMessage(TeamMember admin, WhatsAppWebhookDto.Message message) {
        if (message.getType().equals("image") && message.getImage() != null) {
            String mediaId = message.getImage().getId();

            String currentMediaIds = admin.getTempCustomerPhone() != null ? admin.getTempCustomerPhone() : "";
            String updatedMediaIds = currentMediaIds.isEmpty() ? mediaId : currentMediaIds + "," + mediaId;
            admin.setTempCustomerPhone(updatedMediaIds);

            int imageCount = updatedMediaIds.split(",").length;

            whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                    "✅ Image " + imageCount + " received!\n\n" +
                            (imageCount < 10 ? "Send more images or type 'DONE' to finish."
                                    : "Maximum 10 images reached. Type 'DONE' to finish."));

            if (imageCount >= 10) {
                finalizeProductAdd(admin);
            }
        }
    }

    public void finalizeProductAdd(TeamMember admin) {
        FishProduct newProduct = FishProduct.builder()
                .name(admin.getTempFieldName())
                .pricePerKg(Double.parseDouble(admin.getTempFieldValue()))
                .description(admin.getTempCustomerName())
                .isAvailable(admin.getTempTeamMemberIsActive())
                .build();

        fishProductRepository.save(newProduct);

        String mediaIds = admin.getTempCustomerPhone();
        if (mediaIds != null && !mediaIds.isEmpty()) {
            String[] mediaIdArray = mediaIds.split(",");
            for (int i = 0; i < mediaIdArray.length; i++) {
                productImageService.downloadAndSaveImage(mediaIdArray[i], newProduct, i);
            }
        }

        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(),
                "✅ *Product Added Successfully!*\n\n" +
                        "🐟 " + newProduct.getName() + " has been added with " +
                        (mediaIds != null ? mediaIds.split(",").length : 0) + " images.");

        showProductMenu(admin);
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
                    p.isAvailable() ? "✅ Available" : "❌ Not Available"));
        }

        whatsAppService.sendSimpleText(admin.getWaPhoneNumber(), message.toString());
        showProductMenu(admin);
    }
}
