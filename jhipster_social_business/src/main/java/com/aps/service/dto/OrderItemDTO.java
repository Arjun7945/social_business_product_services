package com.aps.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.aps.domain.OrderItem} entity.
 */
@Schema(description = "Individual items within an order.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrderItemDTO implements Serializable {

    private Long id;

    @NotNull
    @DecimalMin(value = "0")
    @Schema(description = "Quantity remains Double as weight can be fractional", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double quantityKg;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal priceAtOrder;

    @NotNull
    @Schema(description = "OrderItem links to a Product (Snapshot needed? usually yes but simple link here)")
    private FishProductDTO product;

    @NotNull
    private CustomerOrderDTO order;

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

    public BigDecimal getPriceAtOrder() {
        return priceAtOrder;
    }

    public void setPriceAtOrder(BigDecimal priceAtOrder) {
        this.priceAtOrder = priceAtOrder;
    }

    public FishProductDTO getProduct() {
        return product;
    }

    public void setProduct(FishProductDTO product) {
        this.product = product;
    }

    public CustomerOrderDTO getOrder() {
        return order;
    }

    public void setOrder(CustomerOrderDTO order) {
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderItemDTO)) {
            return false;
        }

        OrderItemDTO orderItemDTO = (OrderItemDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, orderItemDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderItemDTO{" +
            "id=" + getId() +
            ", quantityKg=" + getQuantityKg() +
            ", priceAtOrder=" + getPriceAtOrder() +
            ", product=" + getProduct() +
            ", order=" + getOrder() +
            "}";
    }
}
