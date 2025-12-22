package com.aps.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.aps.domain.FishProduct} entity.
 */
@Schema(description = "Product catalog entity.\nIndexed in Elasticsearch for fast search.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FishProductDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal pricePerKg;

    private Long version;

    private String imageUrl;

    @Size(max = 2000)
    @Schema(description = "Increased length for SEO descriptions")
    private String description;

    @NotNull
    private Boolean isAvailable;

    private Instant createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPricePerKg() {
        return pricePerKg;
    }

    public void setPricePerKg(BigDecimal pricePerKg) {
        this.pricePerKg = pricePerKg;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FishProductDTO)) {
            return false;
        }

        FishProductDTO fishProductDTO = (FishProductDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, fishProductDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FishProductDTO{" +
                "id=" + getId() +
                ", name='" + getName() + "'" +
                ", pricePerKg=" + getPricePerKg() +
                ", imageUrl='" + getImageUrl() + "'" +
                ", description='" + getDescription() + "'" +
                ", isAvailable='" + getIsAvailable() + "'" +
                ", createdAt='" + getCreatedAt() + "'" +
                "}";
    }
}
