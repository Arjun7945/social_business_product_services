package com.aps.service;

import com.aps.domain.ButtonAction;
import com.aps.repository.ButtonActionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Service for managing button action idempotency / duplicate checks.
 */
@Service
@Transactional
public class ButtonActionService {

    private final Logger log = LoggerFactory.getLogger(ButtonActionService.class);
    private final ButtonActionRepository buttonActionRepository;

    public ButtonActionService(ButtonActionRepository buttonActionRepository) {
        this.buttonActionRepository = buttonActionRepository;
    }

    /**
     * Checks if a button action (identified by Message ID) has already been
     * processed.
     *
     * @param waMessageId The WhatsApp Message ID (context.id)
     * @return true if the message ID has already been recorded (consumed), false
     *         otherwise.
     */
    @Transactional(readOnly = true)
    public boolean isButtonAlreadyClicked(String waMessageId) {
        if (waMessageId == null || waMessageId.isBlank()) {
            return false; // No ID, cannot check, assume unused (or rely on other validations)
        }
        return buttonActionRepository.existsByWaMessageId(waMessageId);
    }

    /**
     * Records a consumed button action.
     *
     * @param waMessageId The WhatsApp Message ID (context.id)
     * @param buttonId    The payload of the button clicked
     * @param userPhone   The phone number of the user
     */
    public void recordButtonAction(String waMessageId, String buttonId, String userPhone) {
        if (waMessageId == null || waMessageId.isBlank()) {
            log.warn("Attempted to record button action without waMessageId");
            return;
        }

        // Double check to prevent unique constraint violation if called in parallel
        if (buttonActionRepository.existsByWaMessageId(waMessageId)) {
            log.warn("Button action already recorded for ID: {}", waMessageId);
            return;
        }

        ButtonAction action = new ButtonAction();
        action.setWaMessageId(waMessageId);
        action.setButtonId(buttonId);
        action.setClickedBy(userPhone);
        action.setClickedAt(Instant.now());

        buttonActionRepository.save(action);
        log.debug("Recorded button action for Message ID: {}", waMessageId);
    }
}
