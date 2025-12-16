package com.aps.service;

import com.aps.domain.BotSession;
import com.aps.repository.BotSessionRepository;
import com.aps.service.dto.BotSessionDTO;
import com.aps.service.mapper.BotSessionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.aps.domain.BotSession}.
 */
@Service
@Transactional
public class BotSessionService {

    private static final Logger LOG = LoggerFactory.getLogger(BotSessionService.class);

    private final BotSessionRepository botSessionRepository;

    private final BotSessionMapper botSessionMapper;

    public BotSessionService(BotSessionRepository botSessionRepository, BotSessionMapper botSessionMapper) {
        this.botSessionRepository = botSessionRepository;
        this.botSessionMapper = botSessionMapper;
    }

    /**
     * Save a botSession.
     *
     * @param botSessionDTO the entity to save.
     * @return the persisted entity.
     */
    public BotSessionDTO save(BotSessionDTO botSessionDTO) {
        LOG.debug("Request to save BotSession : {}", botSessionDTO);
        BotSession botSession = botSessionMapper.toEntity(botSessionDTO);
        botSession = botSessionRepository.save(botSession);
        return botSessionMapper.toDto(botSession);
    }

    /**
     * Update a botSession.
     *
     * @param botSessionDTO the entity to save.
     * @return the persisted entity.
     */
    public BotSessionDTO update(BotSessionDTO botSessionDTO) {
        LOG.debug("Request to update BotSession : {}", botSessionDTO);
        BotSession botSession = botSessionMapper.toEntity(botSessionDTO);
        botSession = botSessionRepository.save(botSession);
        return botSessionMapper.toDto(botSession);
    }

    /**
     * Partially update a botSession.
     *
     * @param botSessionDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<BotSessionDTO> partialUpdate(BotSessionDTO botSessionDTO) {
        LOG.debug("Request to partially update BotSession : {}", botSessionDTO);

        return botSessionRepository
            .findById(botSessionDTO.getId())
            .map(existingBotSession -> {
                botSessionMapper.partialUpdate(existingBotSession, botSessionDTO);

                return existingBotSession;
            })
            .map(botSessionRepository::save)
            .map(botSessionMapper::toDto);
    }

    /**
     * Get one botSession by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BotSessionDTO> findOne(Long id) {
        LOG.debug("Request to get BotSession : {}", id);
        return botSessionRepository.findById(id).map(botSessionMapper::toDto);
    }

    /**
     * Delete the botSession by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete BotSession : {}", id);
        botSessionRepository.deleteById(id);
    }
}
