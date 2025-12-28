package com.aps.service;

import com.aps.domain.ReturnedOrderItem;
import com.aps.repository.ReturnedOrderItemRepository;
import com.aps.service.dto.ReturnedOrderItemDTO;
import com.aps.service.mapper.ReturnedOrderItemMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.aps.domain.ReturnedOrderItem}.
 */
@Service
@Transactional
public class ReturnedOrderItemService {

    private static final Logger LOG = LoggerFactory.getLogger(ReturnedOrderItemService.class);

    private final ReturnedOrderItemRepository returnedOrderItemRepository;

    private final ReturnedOrderItemMapper returnedOrderItemMapper;

    public ReturnedOrderItemService(
        ReturnedOrderItemRepository returnedOrderItemRepository,
        ReturnedOrderItemMapper returnedOrderItemMapper
    ) {
        this.returnedOrderItemRepository = returnedOrderItemRepository;
        this.returnedOrderItemMapper = returnedOrderItemMapper;
    }

    /**
     * Save a returnedOrderItem.
     *
     * @param returnedOrderItemDTO the entity to save.
     * @return the persisted entity.
     */
    public ReturnedOrderItemDTO save(ReturnedOrderItemDTO returnedOrderItemDTO) {
        LOG.debug("Request to save ReturnedOrderItem : {}", returnedOrderItemDTO);
        ReturnedOrderItem returnedOrderItem = returnedOrderItemMapper.toEntity(returnedOrderItemDTO);
        returnedOrderItem = returnedOrderItemRepository.save(returnedOrderItem);
        return returnedOrderItemMapper.toDto(returnedOrderItem);
    }

    /**
     * Update a returnedOrderItem.
     *
     * @param returnedOrderItemDTO the entity to save.
     * @return the persisted entity.
     */
    public ReturnedOrderItemDTO update(ReturnedOrderItemDTO returnedOrderItemDTO) {
        LOG.debug("Request to update ReturnedOrderItem : {}", returnedOrderItemDTO);
        ReturnedOrderItem returnedOrderItem = returnedOrderItemMapper.toEntity(returnedOrderItemDTO);
        returnedOrderItem = returnedOrderItemRepository.save(returnedOrderItem);
        return returnedOrderItemMapper.toDto(returnedOrderItem);
    }

    /**
     * Partially update a returnedOrderItem.
     *
     * @param returnedOrderItemDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ReturnedOrderItemDTO> partialUpdate(ReturnedOrderItemDTO returnedOrderItemDTO) {
        LOG.debug("Request to partially update ReturnedOrderItem : {}", returnedOrderItemDTO);

        return returnedOrderItemRepository
            .findById(returnedOrderItemDTO.getId())
            .map(existingReturnedOrderItem -> {
                returnedOrderItemMapper.partialUpdate(existingReturnedOrderItem, returnedOrderItemDTO);

                return existingReturnedOrderItem;
            })
            .map(returnedOrderItemRepository::save)
            .map(returnedOrderItemMapper::toDto);
    }

    /**
     * Get all the returnedOrderItems with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ReturnedOrderItemDTO> findAllWithEagerRelationships(Pageable pageable) {
        return returnedOrderItemRepository.findAllWithEagerRelationships(pageable).map(returnedOrderItemMapper::toDto);
    }

    /**
     * Get one returnedOrderItem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ReturnedOrderItemDTO> findOne(Long id) {
        LOG.debug("Request to get ReturnedOrderItem : {}", id);
        return returnedOrderItemRepository.findOneWithEagerRelationships(id).map(returnedOrderItemMapper::toDto);
    }

    /**
     * Delete the returnedOrderItem by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ReturnedOrderItem : {}", id);
        returnedOrderItemRepository.deleteById(id);
    }
}
