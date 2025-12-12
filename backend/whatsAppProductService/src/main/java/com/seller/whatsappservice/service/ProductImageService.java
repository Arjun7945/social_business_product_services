package com.seller.whatsappservice.service;

import com.seller.whatsappservice.model.FishProduct;
import com.seller.whatsappservice.model.ProductImage;
import com.seller.whatsappservice.repository.ProductImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final WhatsAppMediaService whatsAppMediaService;

    public ProductImageService(ProductImageRepository productImageRepository,
            WhatsAppMediaService whatsAppMediaService) {
        this.productImageRepository = productImageRepository;
        this.whatsAppMediaService = whatsAppMediaService;
    }

    @Transactional
    public ProductImage downloadAndSaveImage(String mediaId, FishProduct product, int displayOrder) {
        byte[] imageData = whatsAppMediaService.downloadImage(mediaId);

        if (imageData != null) {
            ProductImage image = ProductImage.builder()
                    .product(product)
                    .imageData(imageData)
                    .imageName("product_" + product.getId() + "_" + displayOrder)
                    .mimeType("image/jpeg") // Defaulting to jpeg, ideally detect from bytes or headers
                    .fileSize((long) imageData.length)
                    .displayOrder(displayOrder)
                    .whatsappMediaId(mediaId)
                    .mediaExpiresAt(LocalDateTime.now().plusDays(30)) // WhatsApp media lasts 30 days
                    .uploadedAt(LocalDateTime.now())
                    .build();

            return productImageRepository.save(image);
        }
        return null;
    }

    public List<ProductImage> getProductImages(Long productId) {
        return productImageRepository.findByProductIdOrderByDisplayOrderAsc(productId);
    }

    @Transactional
    public void deleteProductImage(Long imageId) {
        productImageRepository.deleteById(imageId);
    }

    public boolean validateImageSize(byte[] imageData) {
        return imageData.length <= 1024 * 1024; // 1MB limit
    }
}
