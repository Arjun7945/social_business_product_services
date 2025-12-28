package com.aps.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.aps.domain.ReturnedOrderItem} entity.
 */
@Schema(description = "NEW ENTITY: ReturnedOrderItem\nHandles multiple products in a single return request.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReturnedOrderItemDTO implements Serializable {

    private Long id;

    @NotNull
    private Double quantity;

    private String productComment;

    @NotNull
    @Schema(description = "Returned Order Item links to Specific Product")
    private FishProductDTO product;

    @NotNull
    private ReturnedOrderDTO returnedOrder;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getProductComment() {
        return productComment;
    }

    public void setProductComment(String productComment) {
        this.productComment = productComment;
    }

    public FishProductDTO getProduct() {
        return product;
    }

    public void setProduct(FishProductDTO product) {
        this.product = product;
    }

    public ReturnedOrderDTO getReturnedOrder() {
        return returnedOrder;
    }

    public void setReturnedOrder(ReturnedOrderDTO returnedOrder) {
        this.returnedOrder = returnedOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReturnedOrderItemDTO)) {
            return false;
        }

        ReturnedOrderItemDTO returnedOrderItemDTO = (ReturnedOrderItemDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, returnedOrderItemDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReturnedOrderItemDTO{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            ", productComment='" + getProductComment() + "'" +
            ", product=" + getProduct() +
            ", returnedOrder=" + getReturnedOrder() +
            "}";
    }
}
