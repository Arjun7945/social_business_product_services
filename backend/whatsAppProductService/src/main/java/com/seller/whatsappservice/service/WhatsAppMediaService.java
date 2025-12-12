package com.seller.whatsappservice.service;

import com.seller.whatsappservice.model.ProductImage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@Slf4j
public class WhatsAppMediaService {

    @Value("${whatsapp.api.base-url}")
    private String whatsappApiUrl;

    @Value("${whatsapp.api.token}")
    private String whatsappApiToken;

    @Value("${whatsapp.phone-number-id}")
    private String phoneNumberId;

    private final RestTemplate restTemplate;

    public WhatsAppMediaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @SuppressWarnings("unchecked")
    public byte[] downloadImage(String mediaId) {
        try {
            // 1. Get Media URL
            String url = whatsappApiUrl + "/" + mediaId;
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(whatsappApiToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(url, HttpMethod.GET, entity,
                    new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {
                    });
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String mediaUrl = (String) response.getBody().get("url");

                // 2. Download Media
                ResponseEntity<byte[]> mediaResponse = restTemplate.exchange(mediaUrl, HttpMethod.GET, entity,
                        byte[].class);
                if (mediaResponse.getStatusCode() == HttpStatus.OK) {
                    return mediaResponse.getBody();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String uploadImage(byte[] imageData, String mimeType) {
        try {
            log.info("Uploading image to WhatsApp: size={}, type={}", imageData.length, mimeType);
            String url = whatsappApiUrl + "/" + phoneNumberId + "/media";

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(whatsappApiToken);
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // Create temp file for upload
            java.nio.file.Path tempFile = java.nio.file.Files.createTempFile("upload", ".tmp");
            java.nio.file.Files.write(tempFile, imageData);

            org.springframework.util.LinkedMultiValueMap<String, Object> body = new org.springframework.util.LinkedMultiValueMap<>();

            // Create a file part with headers
            HttpHeaders fileHeaders = new HttpHeaders();
            fileHeaders.setContentType(MediaType.parseMediaType(mimeType));
            HttpEntity<org.springframework.core.io.FileSystemResource> fileEntity = new HttpEntity<>(
                    new org.springframework.core.io.FileSystemResource(tempFile.toFile()), fileHeaders);

            body.add("file", fileEntity);
            body.add("messaging_product", "whatsapp");
            body.add("type", mimeType);

            HttpEntity<org.springframework.util.LinkedMultiValueMap<String, Object>> entity = new HttpEntity<>(body,
                    headers);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(url, HttpMethod.POST, entity,
                    new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {
                    });

            // Clean up temp file
            java.nio.file.Files.deleteIfExists(tempFile);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String mediaId = (String) response.getBody().get("id");
                log.info("Upload successful. Media ID: {}", mediaId);
                return mediaId;
            } else {
                log.error("Upload failed. Status: {}, Body: {}", response.getStatusCode(), response.getBody());
            }
        } catch (Exception e) {
            log.error("Failed to upload image to WhatsApp", e);
        }
        return null;
    }

    private String dummyMediaId = null;

    public String getDummyImageMediaId() {
        if (dummyMediaId != null) {
            return dummyMediaId;
        }

        try {
            // Try to load from resources
            org.springframework.core.io.Resource resource = new org.springframework.core.io.ClassPathResource(
                    "images/placeholder.webp");
            if (!resource.exists()) {
                // Fallback to png if webp not found (handling user's potential file type
                // mismatch)
                resource = new org.springframework.core.io.ClassPathResource("images/placeholder.png");
            }

            if (resource.exists()) {
                byte[] imageData = resource.getInputStream().readAllBytes();
                // Determine mime type based on filename
                String filename = resource.getFilename();
                String mimeType = filename != null && filename.endsWith(".png") ? "image/png" : "image/webp";

                dummyMediaId = uploadImage(imageData, mimeType);
                return dummyMediaId;
            } else {
                log.warn("Placeholder image not found in resources");
            }
        } catch (Exception e) {
            log.error("Failed to load or upload dummy image", e);
        }
        return null;
    }

    public String getOrUploadMediaId(ProductImage image) {
        // if (image.getWhatsappMediaId() != null &&
        // image.getMediaExpiresAt() != null &&
        // image.getMediaExpiresAt().isAfter(LocalDateTime.now())) {
        // return image.getWhatsappMediaId();
        // }

        // Re-upload if expired or missing
        String mediaId = uploadImage(image.getImageData(), image.getMimeType());
        if (mediaId != null) {
            // Update the image entity with new media ID
            // Note: This service doesn't have repository access to save,
            // so we rely on the caller or a separate update mechanism if needed.
            // Ideally, this method should probably be in ProductImageService or return the
            // ID to be saved.
            // For now, we return the new ID.
            return mediaId;
        }

        return image.getWhatsappMediaId(); // Fallback to old ID if upload fails
    }
}
