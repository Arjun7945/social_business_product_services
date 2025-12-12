package com.seller.whatsappservice.controller;

import com.seller.whatsappservice.model.FishProduct;
import com.seller.whatsappservice.model.ProductImage;
import com.seller.whatsappservice.repository.FishProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/public/images")
@RequiredArgsConstructor
@Slf4j
public class ProductImageController {

    private final FishProductRepository fishProductRepository;

    @GetMapping("/products/{productId}")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public ResponseEntity<byte[]> getProductImage(@PathVariable Long productId) {
        log.info("Request for image of product ID: {}", productId);

        Optional<FishProduct> productOpt = fishProductRepository.findById(productId);

        if (productOpt.isEmpty()) {
            log.warn("Product not found: {}", productId);
            return ResponseEntity.notFound().build();
        }

        FishProduct product = productOpt.get();
        List<ProductImage> images = product.getOrderedImages();

        if (images.isEmpty()) {
            log.warn("No images found for product: {}", productId);
            return ResponseEntity.notFound().build();
        }

        // Get the first image (primary)
        ProductImage primaryImage = images.get(0);
        byte[] imageData = primaryImage.getImageData();

        if (imageData == null || imageData.length == 0) {
            log.warn("Image data is empty for product: {}", productId);
            return ResponseEntity.notFound().build();
        }

        String mimeType = primaryImage.getMimeType();
        if (mimeType == null || mimeType.isEmpty()) {
            mimeType = "image/jpeg"; // Default
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, mimeType)
                .body(imageData);
    }
}
