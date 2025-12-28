package com.aps.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.aps.domain.BotSession} entity.
 */
@Schema(description = "BotSession for conversation state.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BotSessionDTO implements Serializable {

    private Long id;

    @NotNull
    private String waPhoneNumber;

    @NotNull
    private String currentState;

    @Lob
    private String sessionData;

    private Instant lastActiveAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWaPhoneNumber() {
        return waPhoneNumber;
    }

    public void setWaPhoneNumber(String waPhoneNumber) {
        this.waPhoneNumber = waPhoneNumber;
    }

    public String getCurrentState() {
        return currentState;
    }

    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }

    public String getSessionData() {
        return sessionData;
    }

    public void setSessionData(String sessionData) {
        this.sessionData = sessionData;
    }

    public Instant getLastActiveAt() {
        return lastActiveAt;
    }

    public void setLastActiveAt(Instant lastActiveAt) {
        this.lastActiveAt = lastActiveAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BotSessionDTO)) {
            return false;
        }

        BotSessionDTO botSessionDTO = (BotSessionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, botSessionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BotSessionDTO{" +
            "id=" + getId() +
            ", waPhoneNumber='" + getWaPhoneNumber() + "'" +
            ", currentState='" + getCurrentState() + "'" +
            ", sessionData='" + getSessionData() + "'" +
            ", lastActiveAt='" + getLastActiveAt() + "'" +
            "}";
    }
}
