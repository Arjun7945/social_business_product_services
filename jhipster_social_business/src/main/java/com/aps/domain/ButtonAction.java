package com.aps.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Context ID tracking for buttons.
 */
@Entity
@Table(name = "button_action")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ButtonAction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "wa_message_id", nullable = false, unique = true)
    private String waMessageId;

    @Column(name = "button_id")
    private String buttonId;

    @Column(name = "clicked_at")
    private Instant clickedAt;

    @Column(name = "clicked_by")
    private String clickedBy;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ButtonAction id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWaMessageId() {
        return this.waMessageId;
    }

    public ButtonAction waMessageId(String waMessageId) {
        this.setWaMessageId(waMessageId);
        return this;
    }

    public void setWaMessageId(String waMessageId) {
        this.waMessageId = waMessageId;
    }

    public String getButtonId() {
        return this.buttonId;
    }

    public ButtonAction buttonId(String buttonId) {
        this.setButtonId(buttonId);
        return this;
    }

    public void setButtonId(String buttonId) {
        this.buttonId = buttonId;
    }

    public Instant getClickedAt() {
        return this.clickedAt;
    }

    public ButtonAction clickedAt(Instant clickedAt) {
        this.setClickedAt(clickedAt);
        return this;
    }

    public void setClickedAt(Instant clickedAt) {
        this.clickedAt = clickedAt;
    }

    public String getClickedBy() {
        return this.clickedBy;
    }

    public ButtonAction clickedBy(String clickedBy) {
        this.setClickedBy(clickedBy);
        return this;
    }

    public void setClickedBy(String clickedBy) {
        this.clickedBy = clickedBy;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ButtonAction)) {
            return false;
        }
        return getId() != null && getId().equals(((ButtonAction) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ButtonAction{" +
            "id=" + getId() +
            ", waMessageId='" + getWaMessageId() + "'" +
            ", buttonId='" + getButtonId() + "'" +
            ", clickedAt='" + getClickedAt() + "'" +
            ", clickedBy='" + getClickedBy() + "'" +
            "}";
    }
}
