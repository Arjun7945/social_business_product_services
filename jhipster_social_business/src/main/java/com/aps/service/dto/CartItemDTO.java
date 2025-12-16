package com.aps.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.aps.domain.CartItem} entity.
 */
@Schema(description = "Items currently in the shopping cart.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CartItemDTO implements Serializable {

    private Long id;

    @NotNull
    private Double quantityKg;

    private Instant addedAt;

    @NotNull
    @Schema(description = "CartItem links to a Product")
    private FishProductDTO product;

    @NotNull
    private ShoppingCartDTO cart;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getQuantityKg() {
        return quantityKg;
    }

    public void setQuantityKg(Double quantityKg) {
        this.quantityKg = quantityKg;
    }

    public Instant getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(Instant addedAt) {
        this.addedAt = addedAt;
    }

    public FishProductDTO getProduct() {
        return product;
    }

    public void setProduct(FishProductDTO product) {
        this.product = product;
    }

    public ShoppingCartDTO getCart() {
        return cart;
    }

    public void setCart(ShoppingCartDTO cart) {
        this.cart = cart;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CartItemDTO)) {
            return false;
        }

        CartItemDTO cartItemDTO = (CartItemDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, cartItemDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CartItemDTO{" +
            "id=" + getId() +
            ", quantityKg=" + getQuantityKg() +
            ", addedAt='" + getAddedAt() + "'" +
            ", product=" + getProduct() +
            ", cart=" + getCart() +
            "}";
    }
}
