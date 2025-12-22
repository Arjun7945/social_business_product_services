package com.aps.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.aps.domain.ShoppingCart} entity.
 */
@Schema(description = "Persistent Shopping Cart for a customer.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ShoppingCartDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant createdAt;

    private Instant updatedAt;

    private Long version;

    @NotNull
    private CustomerDTO customer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public CustomerDTO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerDTO customer) {
        this.customer = customer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ShoppingCartDTO)) {
            return false;
        }

        ShoppingCartDTO shoppingCartDTO = (ShoppingCartDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, shoppingCartDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ShoppingCartDTO{" +
                "id=" + getId() +
                ", createdAt='" + getCreatedAt() + "'" +
                ", updatedAt='" + getUpdatedAt() + "'" +
                ", customer=" + getCustomer() +
                "}";
    }
}
