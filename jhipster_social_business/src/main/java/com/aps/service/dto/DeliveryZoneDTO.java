package com.aps.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.aps.domain.DeliveryZone} entity.
 */
@Schema(
    description = "DeliveryZone\nRepresents the \"Category\" (A, B, C, etc.) based on pincodes.\nUsed to group Customers and DeliveryPersons."
)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DeliveryZoneDTO implements Serializable {

    private Long id;

    @NotNull
    @Schema(description = "e.g., \"A\", \"B\", \"North-1\"", requiredMode = Schema.RequiredMode.REQUIRED)
    private String zoneName;

    @Schema(description = "specific pincode or pattern linked to this zone")
    private String pincode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DeliveryZoneDTO)) {
            return false;
        }

        DeliveryZoneDTO deliveryZoneDTO = (DeliveryZoneDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, deliveryZoneDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DeliveryZoneDTO{" +
            "id=" + getId() +
            ", zoneName='" + getZoneName() + "'" +
            ", pincode='" + getPincode() + "'" +
            "}";
    }
}
