package com.aps.service;

import com.aps.config.WhatsAppConfig;
import com.aps.domain.ProductImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Service to handle media downloads and uploads for WhatsApp API.
 * Uses RestTemplate for multipart handling.
 */
@Service
public class WhatsAppMediaService {

    private final Logger log = LoggerFactory.getLogger(WhatsAppMediaService.class);

    private final WhatsAppConfig whatsAppConfig;
    private final RestTemplate restTemplate;

    public WhatsAppMediaService(WhatsAppConfig whatsAppConfig, RestTemplateBuilder restTemplateBuilder) {
        this.whatsAppConfig = whatsAppConfig;
        this.restTemplate = restTemplateBuilder.build();
    }

    public MediaContent downloadImage(String mediaId) {
        try {
            // 1. Get Media URL
            String url = whatsAppConfig.getApiBaseUrl() + "/" + mediaId;
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(whatsAppConfig.getApiToken());
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {}
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String mediaUrl = (String) response.getBody().get("url");
                String mimeTypeFromMeta = (String) response.getBody().get("mime_type"); // Try to get from metadata
                // first

                // 2. Download Media
                ResponseEntity<byte[]> mediaResponse = restTemplate.exchange(mediaUrl, HttpMethod.GET, entity, byte[].class);
                if (mediaResponse.getStatusCode() == HttpStatus.OK) {
                    byte[] data = mediaResponse.getBody();
                    String finalMimeType = mimeTypeFromMeta;

                    // Fallback to header if metadata missing
                    if (finalMimeType == null && mediaResponse.getHeaders().getContentType() != null) {
                        finalMimeType = mediaResponse.getHeaders().getContentType().toString();
                    }

                    return new MediaContent(data, finalMimeType);
                }
            }
        } catch (Exception e) {
            log.error("Failed to download image: {}", mediaId, e);
        }
        return null; // Or throw custom exception
    }

    public static class MediaContent {

        private final byte[] data;
        private final String mimeType;

        public MediaContent(byte[] data, String mimeType) {
            this.data = data;
            this.mimeType = mimeType;
        }

        public byte[] getData() {
            return data;
        }

        public String getMimeType() {
            return mimeType;
        }
    }

    public String uploadImage(byte[] imageData, String mimeType) {
        try {
            log.info("Uploading image to WhatsApp: size={}, type={}", imageData.length, mimeType);
            String url = whatsAppConfig.getApiBaseUrl() + "/" + whatsAppConfig.getPhoneNumberId() + "/media";

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(whatsAppConfig.getApiToken());
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // Create temp file for upload
            Path tempFile = Files.createTempFile("upload", ".tmp");
            Files.write(tempFile, imageData);

            LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            // Create a file part with headers
            HttpHeaders fileHeaders = new HttpHeaders();
            fileHeaders.setContentType(MediaType.parseMediaType(mimeType));
            HttpEntity<FileSystemResource> fileEntity = new HttpEntity<>(new FileSystemResource(tempFile.toFile()), fileHeaders);

            body.add("file", fileEntity);
            body.add("messaging_product", "whatsapp");
            body.add("type", mimeType);

            HttpEntity<LinkedMultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {}
            );

            // Clean up temp file
            Files.deleteIfExists(tempFile);

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
            Resource resource = new ClassPathResource("images/placeholder.webp");
            if (!resource.exists()) {
                resource = new ClassPathResource("images/placeholder.png");
            }

            if (resource.exists()) {
                byte[] imageData = resource.getInputStream().readAllBytes();
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
        // Since we don't store expiresAt in simplified ProductImage yet (assuming),
        // we might just re-upload if logic demands or trust existing ID.
        // Assuming ProductImage has whatsappMediaId field.
        // For migration purpose, simple logic:
        return uploadImage(image.getImageData(), image.getMimeType());
    }
}
