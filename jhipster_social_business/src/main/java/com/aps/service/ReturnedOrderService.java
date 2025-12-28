package com.aps.service;

import com.aps.domain.ReturnedOrder;
import com.aps.repository.ReturnedOrderRepository;
import com.aps.service.dto.ReturnedOrderDTO;
import com.aps.service.mapper.ReturnedOrderMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.aps.domain.ReturnedOrder}.
 */
@Service
@Transactional
public class ReturnedOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(ReturnedOrderService.class);

    private final ReturnedOrderRepository returnedOrderRepository;

    private final ReturnedOrderMapper returnedOrderMapper;

    public ReturnedOrderService(ReturnedOrderRepository returnedOrderRepository, ReturnedOrderMapper returnedOrderMapper) {
        this.returnedOrderRepository = returnedOrderRepository;
        this.returnedOrderMapper = returnedOrderMapper;
    }

    /**
     * Save a returnedOrder.
     *
     * @param returnedOrderDTO the entity to save.
     * @return the persisted entity.
     */
    public ReturnedOrderDTO save(ReturnedOrderDTO returnedOrderDTO) {
        LOG.debug("Request to save ReturnedOrder : {}", returnedOrderDTO);
        ReturnedOrder returnedOrder = returnedOrderMapper.toEntity(returnedOrderDTO);
        returnedOrder = returnedOrderRepository.save(returnedOrder);
        return returnedOrderMapper.toDto(returnedOrder);
    }

    /**
     * Update a returnedOrder.
     *
     * @param returnedOrderDTO the entity to save.
     * @return the persisted entity.
     */
    public ReturnedOrderDTO update(ReturnedOrderDTO returnedOrderDTO) {
        LOG.debug("Request to update ReturnedOrder : {}", returnedOrderDTO);
        ReturnedOrder returnedOrder = returnedOrderMapper.toEntity(returnedOrderDTO);
        returnedOrder = returnedOrderRepository.save(returnedOrder);
        return returnedOrderMapper.toDto(returnedOrder);
    }

    /**
     * Partially update a returnedOrder.
     *
     * @param returnedOrderDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ReturnedOrderDTO> partialUpdate(ReturnedOrderDTO returnedOrderDTO) {
        LOG.debug("Request to partially update ReturnedOrder : {}", returnedOrderDTO);

        return returnedOrderRepository
            .findById(returnedOrderDTO.getId())
            .map(existingReturnedOrder -> {
                returnedOrderMapper.partialUpdate(existingReturnedOrder, returnedOrderDTO);

                return existingReturnedOrder;
            })
            .map(returnedOrderRepository::save)
            .map(returnedOrderMapper::toDto);
    }

    /**
     * Get all the returnedOrders with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ReturnedOrderDTO> findAllWithEagerRelationships(Pageable pageable) {
        return returnedOrderRepository.findAllWithEagerRelationships(pageable).map(returnedOrderMapper::toDto);
    }

    /**
     * Get one returnedOrder by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ReturnedOrderDTO> findOne(Long id) {
        LOG.debug("Request to get ReturnedOrder : {}", id);
        return returnedOrderRepository.findOneWithEagerRelationships(id).map(returnedOrderMapper::toDto);
    }

    /**
     * Delete the returnedOrder by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ReturnedOrder : {}", id);
        returnedOrderRepository.deleteById(id);
    }
}
