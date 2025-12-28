package com.aps.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * Custom DTO for cart item with product details.
 * Used by CartService for business logic (WhatsApp display).
 */
public class CartItemDetailsDTO implements Serializable {

    private Long fishProductId;
    private String fishName;
    private Double quantityKg;
    private Double pricePerKg;
    private Double subtotal;
    private String imageUrl;

    public CartItemDetailsDTO() {}

    public CartItemDetailsDTO(Long fishProductId, String fishName, Double quantityKg, Double pricePerKg, Double subtotal, String imageUrl) {
        this.fishProductId = fishProductId;
        this.fishName = fishName;
        this.quantityKg = quantityKg;
        this.pricePerKg = pricePerKg;
        this.subtotal = subtotal;
        this.imageUrl = imageUrl;
    }

    public Long getFishProductId() {
        return fishProductId;
    }

    public void setFishProductId(Long fishProductId) {
        this.fishProductId = fishProductId;
    }

    public String getFishName() {
        return fishName;
    }

    public void setFishName(String fishName) {
        this.fishName = fishName;
    }

    public Double getQuantityKg() {
        return quantityKg;
    }

    public void setQuantityKg(Double quantityKg) {
        this.quantityKg = quantityKg;
    }

    public Double getPricePerKg() {
        return pricePerKg;
    }

    public void setPricePerKg(Double pricePerKg) {
        this.pricePerKg = pricePerKg;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Long fishProductId;
        private String fishName;
        private Double quantityKg;
        private Double pricePerKg;
        private Double subtotal;
        private String imageUrl;

        public Builder fishProductId(Long fishProductId) {
            this.fishProductId = fishProductId;
            return this;
        }

        public Builder fishName(String fishName) {
            this.fishName = fishName;
            return this;
        }

        public Builder quantityKg(Double quantityKg) {
            this.quantityKg = quantityKg;
            return this;
        }

        public Builder pricePerKg(Double pricePerKg) {
            this.pricePerKg = pricePerKg;
            return this;
        }

        public Builder subtotal(Double subtotal) {
            this.subtotal = subtotal;
            return this;
        }

        public Builder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public CartItemDetailsDTO build() {
            return new CartItemDetailsDTO(fishProductId, fishName, quantityKg, pricePerKg, subtotal, imageUrl);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItemDetailsDTO that = (CartItemDetailsDTO) o;
        return (
            Objects.equals(fishProductId, that.fishProductId) &&
            Objects.equals(fishName, that.fishName) &&
            Objects.equals(quantityKg, that.quantityKg)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(fishProductId, fishName, quantityKg);
    }

    @Override
    public String toString() {
        return (
            "CartItemDetailsDTO{" +
            "fishProductId=" +
            fishProductId +
            ", fishName='" +
            fishName +
            '\'' +
            ", quantityKg=" +
            quantityKg +
            ", pricePerKg=" +
            pricePerKg +
            ", subtotal=" +
            subtotal +
            '}'
        );
    }
}
