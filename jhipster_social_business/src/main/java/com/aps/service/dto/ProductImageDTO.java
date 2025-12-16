package com.aps.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.aps.domain.ProductImage} entity.
 */
@Schema(description = "Images associated with a product.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductImageDTO implements Serializable {

    private Long id;

    @NotNull
    private String imageUrl;

    @NotNull
    private Integer displayOrder;

    @NotNull
    private FishProductDTO product;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public FishProductDTO getProduct() {
        return product;
    }

    public void setProduct(FishProductDTO product) {
        this.product = product;
    }

    @jakarta.persistence.Lob
    private byte[] imageData;

    private String mimeType;

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductImageDTO)) {
            return false;
        }

        ProductImageDTO productImageDTO = (ProductImageDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, productImageDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductImageDTO{" +
                "id=" + getId() +
                ", imageUrl='" + getImageUrl() + "'" +
                ", displayOrder=" + getDisplayOrder() +
                ", product=" + getProduct() +
                "}";
    }
}
