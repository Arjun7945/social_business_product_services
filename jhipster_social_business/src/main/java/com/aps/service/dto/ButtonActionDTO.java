package com.aps.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.aps.domain.ButtonAction} entity.
 */
@Schema(description = "Context ID tracking for buttons.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ButtonActionDTO implements Serializable {

    private Long id;

    @NotNull
    private String waMessageId;

    private String buttonId;

    private Instant clickedAt;

    private String clickedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWaMessageId() {
        return waMessageId;
    }

    public void setWaMessageId(String waMessageId) {
        this.waMessageId = waMessageId;
    }

    public String getButtonId() {
        return buttonId;
    }

    public void setButtonId(String buttonId) {
        this.buttonId = buttonId;
    }

    public Instant getClickedAt() {
        return clickedAt;
    }

    public void setClickedAt(Instant clickedAt) {
        this.clickedAt = clickedAt;
    }

    public String getClickedBy() {
        return clickedBy;
    }

    public void setClickedBy(String clickedBy) {
        this.clickedBy = clickedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ButtonActionDTO)) {
            return false;
        }

        ButtonActionDTO buttonActionDTO = (ButtonActionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, buttonActionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ButtonActionDTO{" +
            "id=" + getId() +
            ", waMessageId='" + getWaMessageId() + "'" +
            ", buttonId='" + getButtonId() + "'" +
            ", clickedAt='" + getClickedAt() + "'" +
            ", clickedBy='" + getClickedBy() + "'" +
            "}";
    }
}
