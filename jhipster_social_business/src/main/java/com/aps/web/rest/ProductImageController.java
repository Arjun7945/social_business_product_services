package com.aps.web.rest;

import com.aps.domain.ProductImage;
import com.aps.repository.ProductImageRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to serve product images stored in the database.
 */
@RestController
@RequestMapping("/api/public/images")
@Transactional
public class ProductImageController {

    private final Logger log = LoggerFactory.getLogger(ProductImageController.class);

    private final ProductImageRepository productImageRepository;

    public ProductImageController(ProductImageRepository productImageRepository) {
        this.productImageRepository = productImageRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getProductImage(@PathVariable Long id) {
        log.debug("REST request to get ProductImage : {}", id);
        Optional<ProductImage> imageOpt = productImageRepository.findById(id);

        if (imageOpt.isPresent()) {
            ProductImage image = imageOpt.get();
            byte[] imageData = image.getImageData();
            String mimeType = image.getMimeType();

            if (imageData != null && imageData.length > 0) {
                MediaType mediaType = MediaType.IMAGE_JPEG; // Default
                if (mimeType != null && mimeType.equals("image/png")) {
                    mediaType = MediaType.IMAGE_PNG;
                } else if (mimeType != null && mimeType.equals("image/webp")) {
                    mediaType = MediaType.parseMediaType("image/webp");
                } else if (mimeType != null && !mimeType.isEmpty()) {
                    try {
                        mediaType = MediaType.parseMediaType(mimeType);
                    } catch (Exception e) {
                        log.warn("Invalid mime type {}, falling back to JPEG", mimeType);
                    }
                }

                return ResponseEntity.ok().contentType(mediaType).body(imageData);
            }
        }

        return ResponseEntity.notFound().build();
    }
}
