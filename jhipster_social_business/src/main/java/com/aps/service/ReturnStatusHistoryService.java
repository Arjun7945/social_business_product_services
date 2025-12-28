package com.aps.service;

import com.aps.domain.ReturnStatusHistory;
import com.aps.repository.ReturnStatusHistoryRepository;
import com.aps.service.dto.ReturnStatusHistoryDTO;
import com.aps.service.mapper.ReturnStatusHistoryMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.aps.domain.ReturnStatusHistory}.
 */
@Service
@Transactional
public class ReturnStatusHistoryService {

    private static final Logger LOG = LoggerFactory.getLogger(ReturnStatusHistoryService.class);

    private final ReturnStatusHistoryRepository returnStatusHistoryRepository;

    private final ReturnStatusHistoryMapper returnStatusHistoryMapper;

    public ReturnStatusHistoryService(
        ReturnStatusHistoryRepository returnStatusHistoryRepository,
        ReturnStatusHistoryMapper returnStatusHistoryMapper
    ) {
        this.returnStatusHistoryRepository = returnStatusHistoryRepository;
        this.returnStatusHistoryMapper = returnStatusHistoryMapper;
    }

    /**
     * Save a returnStatusHistory.
     *
     * @param returnStatusHistoryDTO the entity to save.
     * @return the persisted entity.
     */
    public ReturnStatusHistoryDTO save(ReturnStatusHistoryDTO returnStatusHistoryDTO) {
        LOG.debug("Request to save ReturnStatusHistory : {}", returnStatusHistoryDTO);
        ReturnStatusHistory returnStatusHistory = returnStatusHistoryMapper.toEntity(returnStatusHistoryDTO);
        returnStatusHistory = returnStatusHistoryRepository.save(returnStatusHistory);
        return returnStatusHistoryMapper.toDto(returnStatusHistory);
    }

    /**
     * Update a returnStatusHistory.
     *
     * @param returnStatusHistoryDTO the entity to save.
     * @return the persisted entity.
     */
    public ReturnStatusHistoryDTO update(ReturnStatusHistoryDTO returnStatusHistoryDTO) {
        LOG.debug("Request to update ReturnStatusHistory : {}", returnStatusHistoryDTO);
        ReturnStatusHistory returnStatusHistory = returnStatusHistoryMapper.toEntity(returnStatusHistoryDTO);
        returnStatusHistory = returnStatusHistoryRepository.save(returnStatusHistory);
        return returnStatusHistoryMapper.toDto(returnStatusHistory);
    }

    /**
     * Partially update a returnStatusHistory.
     *
     * @param returnStatusHistoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ReturnStatusHistoryDTO> partialUpdate(ReturnStatusHistoryDTO returnStatusHistoryDTO) {
        LOG.debug("Request to partially update ReturnStatusHistory : {}", returnStatusHistoryDTO);

        return returnStatusHistoryRepository
            .findById(returnStatusHistoryDTO.getId())
            .map(existingReturnStatusHistory -> {
                returnStatusHistoryMapper.partialUpdate(existingReturnStatusHistory, returnStatusHistoryDTO);

                return existingReturnStatusHistory;
            })
            .map(returnStatusHistoryRepository::save)
            .map(returnStatusHistoryMapper::toDto);
    }

    /**
     * Get one returnStatusHistory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ReturnStatusHistoryDTO> findOne(Long id) {
        LOG.debug("Request to get ReturnStatusHistory : {}", id);
        return returnStatusHistoryRepository.findById(id).map(returnStatusHistoryMapper::toDto);
    }

    /**
     * Delete the returnStatusHistory by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ReturnStatusHistory : {}", id);
        returnStatusHistoryRepository.deleteById(id);
    }
}
