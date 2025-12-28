package com.aps.service;

import com.aps.domain.ButtonAction;
import com.aps.repository.ButtonActionRepository;
import com.aps.service.dto.ButtonActionDTO;
import com.aps.service.mapper.ButtonActionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.aps.domain.ButtonAction}.
 */
@Service
@Transactional
public class ButtonActionService {

    private static final Logger LOG = LoggerFactory.getLogger(ButtonActionService.class);

    private final ButtonActionRepository buttonActionRepository;

    private final ButtonActionMapper buttonActionMapper;

    public ButtonActionService(ButtonActionRepository buttonActionRepository, ButtonActionMapper buttonActionMapper) {
        this.buttonActionRepository = buttonActionRepository;
        this.buttonActionMapper = buttonActionMapper;
    }

    /**
     * Save a buttonAction.
     *
     * @param buttonActionDTO the entity to save.
     * @return the persisted entity.
     */
    public ButtonActionDTO save(ButtonActionDTO buttonActionDTO) {
        LOG.debug("Request to save ButtonAction : {}", buttonActionDTO);
        ButtonAction buttonAction = buttonActionMapper.toEntity(buttonActionDTO);
        buttonAction = buttonActionRepository.save(buttonAction);
        return buttonActionMapper.toDto(buttonAction);
    }

    /**
     * Update a buttonAction.
     *
     * @param buttonActionDTO the entity to save.
     * @return the persisted entity.
     */
    public ButtonActionDTO update(ButtonActionDTO buttonActionDTO) {
        LOG.debug("Request to update ButtonAction : {}", buttonActionDTO);
        ButtonAction buttonAction = buttonActionMapper.toEntity(buttonActionDTO);
        buttonAction = buttonActionRepository.save(buttonAction);
        return buttonActionMapper.toDto(buttonAction);
    }

    /**
     * Partially update a buttonAction.
     *
     * @param buttonActionDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ButtonActionDTO> partialUpdate(ButtonActionDTO buttonActionDTO) {
        LOG.debug("Request to partially update ButtonAction : {}", buttonActionDTO);

        return buttonActionRepository
                .findById(buttonActionDTO.getId())
                .map(existingButtonAction -> {
                    buttonActionMapper.partialUpdate(existingButtonAction, buttonActionDTO);

                    return existingButtonAction;
                })
                .map(buttonActionRepository::save)
                .map(buttonActionMapper::toDto);
    }

    /**
     * Get one buttonAction by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ButtonActionDTO> findOne(Long id) {
        LOG.debug("Request to get ButtonAction : {}", id);
        return buttonActionRepository.findById(id).map(buttonActionMapper::toDto);
    }

    /**
     * Delete the buttonAction by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ButtonAction : {}", id);
        buttonActionRepository.deleteById(id);
    }

    /**
     * Check if a button action/message is already processed.
     */
    @Transactional(readOnly = true)
    public boolean isButtonAlreadyClicked(String messageId) {
        return buttonActionRepository.findByWaMessageId(messageId).isPresent();
    }

    /**
     * Record a button action.
     */
    public void recordButtonAction(String messageId, String buttonId, String actionName) {
        if (isButtonAlreadyClicked(messageId)) {
            return;
        }
        ButtonAction action = new ButtonAction();
        action.setWaMessageId(messageId);
        action.setButtonId(buttonId);
        action.setClickedAt(java.time.Instant.now());
        action.setClickedBy(actionName); // Using clickedBy field for generic action name/user info if needed
        buttonActionRepository.save(action);
    }
}
