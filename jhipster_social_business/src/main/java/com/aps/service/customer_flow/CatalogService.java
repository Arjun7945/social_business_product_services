package com.aps.service.customer_flow;

import com.aps.config.FlowConstants;
import com.aps.domain.BotSession;
import com.aps.domain.Customer;
import com.aps.domain.FishProduct;
import com.aps.domain.ProductImage;
import com.aps.domain.enumeration.CustomerFlowStage;
import com.aps.repository.FishProductRepository;
import com.aps.service.CustomerMessageService;
import com.aps.service.WhatsAppMediaService;
import com.aps.service.WhatsAppService;
import com.aps.service.dto.WhatsAppMessageDto;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Handles product catalog browsing logic.
 * Extracted from CustomerFlowService.
 */
@Service
public class CatalogService {

    private final Logger log = LoggerFactory.getLogger(CatalogService.class);

    private final FishProductRepository fishProductRepository;
    private final WhatsAppService whatsAppService;
    private final CustomerMessageService messageService;
    private final WhatsAppMediaService whatsAppMediaService;
    private final FlowStateService flowStateService;

    @Value("${app.server.url}")
    private String appServerUrl;

    public CatalogService(
            FishProductRepository fishProductRepository,
            WhatsAppService whatsAppService,
            CustomerMessageService messageService,
            WhatsAppMediaService whatsAppMediaService,
            FlowStateService flowStateService) {
        this.fishProductRepository = fishProductRepository;
        this.whatsAppService = whatsAppService;
        this.messageService = messageService;
        this.whatsAppMediaService = whatsAppMediaService;
        this.flowStateService = flowStateService;
    }

    public void showProductCatalog(Customer customer, BotSession session) {
        List<FishProduct> products = fishProductRepository.findByIsAvailableTrue();

        if (products.isEmpty()) {
            whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                    messageService.getNoProductsAvailable(customer.getName()));
            return;
        }

        String dummyMediaId = whatsAppMediaService.getDummyImageMediaId();

        List<FishProduct> carouselProducts = new java.util.ArrayList<>();
        List<FishProduct> listProducts = new java.util.ArrayList<>();

        for (FishProduct product : products) {
            boolean hasImage = product.getImage() != null;
            if (hasImage || dummyMediaId != null) {
                carouselProducts.add(product);
            } else {
                listProducts.add(product);
            }
        }

        // Send the Main Header as a separate Text Message FIRST
        whatsAppService.sendSimpleText(customer.getWaPhoneNumber(),
                messageService.getProductCatalogHeader(customer.getName()));

        // --- Logic Update: Carousel Chunking & Rebalancing ---
        if (carouselProducts.size() < 3) {
            listProducts.addAll(0, carouselProducts);
            carouselProducts.clear();
        } else {
            // 1. Create Chunks of Max 10
            List<List<FishProduct>> chunks = new java.util.ArrayList<>();
            int chunkSize = 10;
            for (int i = 0; i < carouselProducts.size(); i += chunkSize) {
                int end = Math.min(carouselProducts.size(), i + chunkSize);
                chunks.add(new java.util.ArrayList<>(carouselProducts.subList(i, end)));
            }

            // 2. Rebalance Logic
            // If the last chunk has fewer than 3 items, borrow from the previous chunk.
            if (chunks.size() > 1) {
                List<FishProduct> lastChunk = chunks.get(chunks.size() - 1);

                if (lastChunk.size() < 3) {
                    List<FishProduct> prevChunk = chunks.get(chunks.size() - 2);
                    int needed = 3 - lastChunk.size();

                    for (int k = 0; k < needed; k++) {
                        FishProduct movedItem = prevChunk.remove(prevChunk.size() - 1);
                        lastChunk.add(0, movedItem);
                    }
                }
            }

            // 3. Send Carousels
            int part = 1;
            int totalParts = chunks.size();
            for (List<FishProduct> chunk : chunks) {
                String bodyText = totalParts > 1
                        ? messageService.getCarouselTitle(part, totalParts)
                        : messageService.getCarouselTitleSingle();
                sendProductCarousel(customer, chunk, dummyMediaId, bodyText);
                part++;
            }
        }

        if (!listProducts.isEmpty()) {
            String title = !carouselProducts.isEmpty()
                    ? messageService.getProductsWithoutImagesTitle()
                    : messageService.getAvailableFishTitle();
            sendProductList(customer, listProducts, title);
        }

        flowStateService.updateStage(session, CustomerFlowStage.BROWSING);
    }

    private void sendProductCarousel(Customer customer, List<FishProduct> products, String dummyMediaId,
            String bodyText) {
        List<WhatsAppMessageDto.CarouselCardDto> cards = java.util.stream.IntStream.range(0, products.size())
                .mapToObj(i -> {
                    FishProduct product = products.get(i);
                    String imageUrl;

                    ProductImage firstImage = product.getImage();
                    log.debug("Product: {}, Image: {}", product.getName(),
                            firstImage != null ? firstImage.getId() : "NULL");

                    if (firstImage != null) {
                        imageUrl = appServerUrl + "/api/product-images/public/" + firstImage.getId() + "/content";
                        log.debug("Generated image URL: {}", imageUrl);
                    } else {
                        // Fallback
                        imageUrl = FlowConstants.WHATSAPP_LOGO_URL;
                        log.debug("Using fallback URL: {}", imageUrl);
                    }

                    return WhatsAppMessageDto.CarouselCardDto.builder()
                            .cardIndex(i)
                            .type("button")
                            .header(
                                    WhatsAppMessageDto.HeaderDto.builder()
                                            .type("image")
                                            .image(WhatsAppMessageDto.ImageDto.builder().link(imageUrl).build())
                                            .build())
                            .body(
                                    WhatsAppMessageDto.BodyDto.builder()
                                            .text(
                                                    String.format(
                                                            "%s\n₹%.2f/kg\n%s",
                                                            product.getName(),
                                                            product.getPricePerKg(),
                                                            product.getDescription() != null ? product.getDescription()
                                                                    : ""))
                                            .build())
                            .action(
                                    WhatsAppMessageDto.ActionDto.builder()
                                            .buttons(
                                                    List.of(
                                                            WhatsAppMessageDto.ButtonDto.builder()
                                                                    .type("quick_reply")
                                                                    .quickReply(
                                                                            WhatsAppMessageDto.ReplyDto.builder()
                                                                                    .id("SELECT_" + product.getId())
                                                                                    .title(messageService
                                                                                            .getButtonAddToCart())
                                                                                    .build())
                                                                    .build()))
                                            .build())
                            .build();
                })
                .collect(Collectors.toList());

        whatsAppService.sendCarouselMessage(
                customer.getWaPhoneNumber(),
                bodyText != null ? bodyText : messageService.getCarouselBodyDefault(),
                cards);
    }

    private void sendProductList(Customer customer, List<FishProduct> products, String title) {
        List<WhatsAppMessageDto.RowDto> rows = products
                .stream()
                .map(p -> WhatsAppMessageDto.RowDto.builder()
                        .id("FISH_" + p.getId())
                        .title(p.getName())
                        .description(String.format("₹%.2f/kg", p.getPricePerKg()))
                        .build())
                .collect(Collectors.toList());

        whatsAppService.sendInteractiveList(customer.getWaPhoneNumber(), title, rows);
    }
}
