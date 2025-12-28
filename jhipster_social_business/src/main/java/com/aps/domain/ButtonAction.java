package com.aps.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ButtonAction to track consumed WhatsApp button messages (Context IDs).
 */
@Entity
@Table(name = "button_action")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ButtonAction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "wa_message_id", unique = true, nullable = false)
    private String waMessageId;

    @Column(name = "button_id")
    private String buttonId;

    @Column(name = "clicked_at")
    private Instant clickedAt;

    @Column(name = "clicked_by")
    private String clickedBy;

    // Getters and Setters

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
        if (!(o instanceof ButtonAction)) {
            return false;
        }
        return id != null && id.equals(((ButtonAction) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return (
            "ButtonAction{" +
            "id=" +
            getId() +
            ", waMessageId='" +
            getWaMessageId() +
            "'" +
            ", buttonId='" +
            getButtonId() +
            "'" +
            ", clickedAt='" +
            getClickedAt() +
            "'" +
            ", clickedBy='" +
            getClickedBy() +
            "'" +
            "}"
        );
    }
}
