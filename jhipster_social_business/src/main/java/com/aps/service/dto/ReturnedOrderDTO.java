package com.aps.service.dto;

import com.aps.domain.enumeration.ReturnProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.aps.domain.ReturnedOrder} entity.
 */
@Schema(
    description = "NEW ENTITY: ReturnedOrder\nStores information about returned orders.\nLinked to Order, and Customer for full context.\nNOTE: Products are now Master-Detail via ReturnedOrderItem."
)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReturnedOrderDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant returnDate;

    private String paymentReceivedMode;

    private String paymentReturnedMode;

    @NotNull
    private ReturnProductStatus productClaimStatus;

    @DecimalMin(value = "0")
    private BigDecimal refundAmount;

    @NotNull
    @Schema(description = "Returned Order links to Original Order")
    private CustomerOrderDTO order;

    @NotNull
    private CustomerDTO customer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Instant returnDate) {
        this.returnDate = returnDate;
    }

    public String getPaymentReceivedMode() {
        return paymentReceivedMode;
    }

    public void setPaymentReceivedMode(String paymentReceivedMode) {
        this.paymentReceivedMode = paymentReceivedMode;
    }

    public String getPaymentReturnedMode() {
        return paymentReturnedMode;
    }

    public void setPaymentReturnedMode(String paymentReturnedMode) {
        this.paymentReturnedMode = paymentReturnedMode;
    }

    public ReturnProductStatus getProductClaimStatus() {
        return productClaimStatus;
    }

    public void setProductClaimStatus(ReturnProductStatus productClaimStatus) {
        this.productClaimStatus = productClaimStatus;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public CustomerOrderDTO getOrder() {
        return order;
    }

    public void setOrder(CustomerOrderDTO order) {
        this.order = order;
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
        if (!(o instanceof ReturnedOrderDTO)) {
            return false;
        }

        ReturnedOrderDTO returnedOrderDTO = (ReturnedOrderDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, returnedOrderDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReturnedOrderDTO{" +
            "id=" + getId() +
            ", returnDate='" + getReturnDate() + "'" +
            ", paymentReceivedMode='" + getPaymentReceivedMode() + "'" +
            ", paymentReturnedMode='" + getPaymentReturnedMode() + "'" +
            ", productClaimStatus='" + getProductClaimStatus() + "'" +
            ", refundAmount=" + getRefundAmount() +
            ", order=" + getOrder() +
            ", customer=" + getCustomer() +
            "}";
    }
}
