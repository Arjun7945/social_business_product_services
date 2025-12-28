package com.aps.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * BotSession for conversation state.
 */
@Entity
@Table(name = "bot_session")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BotSession implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "wa_phone_number", nullable = false, unique = true)
    private String waPhoneNumber;

    @NotNull
    @Column(name = "current_state", nullable = false)
    private String currentState;

    @Lob
    @Column(name = "session_data")
    private String sessionData;

    @Column(name = "last_active_at")
    private Instant lastActiveAt;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public BotSession id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWaPhoneNumber() {
        return this.waPhoneNumber;
    }

    public BotSession waPhoneNumber(String waPhoneNumber) {
        this.setWaPhoneNumber(waPhoneNumber);
        return this;
    }

    public void setWaPhoneNumber(String waPhoneNumber) {
        this.waPhoneNumber = waPhoneNumber;
    }

    public String getCurrentState() {
        return this.currentState;
    }

    public BotSession currentState(String currentState) {
        this.setCurrentState(currentState);
        return this;
    }

    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }

    public String getSessionData() {
        return this.sessionData;
    }

    public BotSession sessionData(String sessionData) {
        this.setSessionData(sessionData);
        return this;
    }

    public void setSessionData(String sessionData) {
        this.sessionData = sessionData;
    }

    public Instant getLastActiveAt() {
        return this.lastActiveAt;
    }

    public BotSession lastActiveAt(Instant lastActiveAt) {
        this.setLastActiveAt(lastActiveAt);
        return this;
    }

    public void setLastActiveAt(Instant lastActiveAt) {
        this.lastActiveAt = lastActiveAt;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BotSession)) {
            return false;
        }
        return getId() != null && getId().equals(((BotSession) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BotSession{" +
            "id=" + getId() +
            ", waPhoneNumber='" + getWaPhoneNumber() + "'" +
            ", currentState='" + getCurrentState() + "'" +
            ", sessionData='" + getSessionData() + "'" +
            ", lastActiveAt='" + getLastActiveAt() + "'" +
            "}";
    }
}
