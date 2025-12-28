package com.aps.service;

import com.aps.domain.RemovedOrderSummary;
import com.aps.repository.RemovedOrderSummaryRepository;
import com.aps.service.dto.RemovedOrderSummaryDTO;
import com.aps.service.mapper.RemovedOrderSummaryMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.aps.domain.RemovedOrderSummary}.
 */
@Service
@Transactional
public class RemovedOrderSummaryService {

    private static final Logger LOG = LoggerFactory.getLogger(RemovedOrderSummaryService.class);

    private final RemovedOrderSummaryRepository removedOrderSummaryRepository;

    private final RemovedOrderSummaryMapper removedOrderSummaryMapper;

    public RemovedOrderSummaryService(
        RemovedOrderSummaryRepository removedOrderSummaryRepository,
        RemovedOrderSummaryMapper removedOrderSummaryMapper
    ) {
        this.removedOrderSummaryRepository = removedOrderSummaryRepository;
        this.removedOrderSummaryMapper = removedOrderSummaryMapper;
    }

    /**
     * Save a removedOrderSummary.
     *
     * @param removedOrderSummaryDTO the entity to save.
     * @return the persisted entity.
     */
    public RemovedOrderSummaryDTO save(RemovedOrderSummaryDTO removedOrderSummaryDTO) {
        LOG.debug("Request to save RemovedOrderSummary : {}", removedOrderSummaryDTO);
        RemovedOrderSummary removedOrderSummary = removedOrderSummaryMapper.toEntity(removedOrderSummaryDTO);
        removedOrderSummary = removedOrderSummaryRepository.save(removedOrderSummary);
        return removedOrderSummaryMapper.toDto(removedOrderSummary);
    }

    /**
     * Update a removedOrderSummary.
     *
     * @param removedOrderSummaryDTO the entity to save.
     * @return the persisted entity.
     */
    public RemovedOrderSummaryDTO update(RemovedOrderSummaryDTO removedOrderSummaryDTO) {
        LOG.debug("Request to update RemovedOrderSummary : {}", removedOrderSummaryDTO);
        RemovedOrderSummary removedOrderSummary = removedOrderSummaryMapper.toEntity(removedOrderSummaryDTO);
        removedOrderSummary = removedOrderSummaryRepository.save(removedOrderSummary);
        return removedOrderSummaryMapper.toDto(removedOrderSummary);
    }

    /**
     * Partially update a removedOrderSummary.
     *
     * @param removedOrderSummaryDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<RemovedOrderSummaryDTO> partialUpdate(RemovedOrderSummaryDTO removedOrderSummaryDTO) {
        LOG.debug("Request to partially update RemovedOrderSummary : {}", removedOrderSummaryDTO);

        return removedOrderSummaryRepository
            .findById(removedOrderSummaryDTO.getId())
            .map(existingRemovedOrderSummary -> {
                removedOrderSummaryMapper.partialUpdate(existingRemovedOrderSummary, removedOrderSummaryDTO);

                return existingRemovedOrderSummary;
            })
            .map(removedOrderSummaryRepository::save)
            .map(removedOrderSummaryMapper::toDto);
    }

    /**
     * Get one removedOrderSummary by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<RemovedOrderSummaryDTO> findOne(Long id) {
        LOG.debug("Request to get RemovedOrderSummary : {}", id);
        return removedOrderSummaryRepository.findById(id).map(removedOrderSummaryMapper::toDto);
    }

    /**
     * Delete the removedOrderSummary by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete RemovedOrderSummary : {}", id);
        removedOrderSummaryRepository.deleteById(id);
    }
}
