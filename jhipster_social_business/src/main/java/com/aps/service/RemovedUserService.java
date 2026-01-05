package com.aps.service;

import com.aps.domain.RemovedUser;
import com.aps.repository.RemovedOrderSummaryRepository;
import com.aps.repository.RemovedUserRepository;
import com.aps.service.dto.RemovedUserDTO;
import com.aps.service.mapper.RemovedUserMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.aps.domain.RemovedUser}.
 */
@Service
@Transactional
public class RemovedUserService {

    private static final Logger LOG = LoggerFactory.getLogger(RemovedUserService.class);

    private final RemovedUserRepository removedUserRepository;

    private final RemovedUserMapper removedUserMapper;

    private final UserRemovalService userRemovalService;

    private final RemovedOrderSummaryRepository removedOrderSummaryRepository;

    public RemovedUserService(RemovedUserRepository removedUserRepository, RemovedUserMapper removedUserMapper,
            UserRemovalService userRemovalService, RemovedOrderSummaryRepository removedOrderSummaryRepository) {
        this.removedUserRepository = removedUserRepository;
        this.removedUserMapper = removedUserMapper;
        this.userRemovalService = userRemovalService;
        this.removedOrderSummaryRepository = removedOrderSummaryRepository;
    }

    /**
     * Save a removedUser.
     *
     * @param removedUserDTO the entity to save.
     * @return the persisted entity.
     */
    public RemovedUserDTO save(RemovedUserDTO removedUserDTO) {
        LOG.debug("Request to save RemovedUser : {}", removedUserDTO);
        RemovedUser removedUser = removedUserMapper.toEntity(removedUserDTO);
        removedUser = removedUserRepository.save(removedUser);
        return removedUserMapper.toDto(removedUser);
    }

    /**
     * Update a removedUser.
     *
     * @param removedUserDTO the entity to save.
     * @return the persisted entity.
     */
    public RemovedUserDTO update(RemovedUserDTO removedUserDTO) {
        LOG.debug("Request to update RemovedUser : {}", removedUserDTO);
        RemovedUser removedUser = removedUserMapper.toEntity(removedUserDTO);
        removedUser = removedUserRepository.save(removedUser);
        return removedUserMapper.toDto(removedUser);
    }

    /**
     * Partially update a removedUser.
     *
     * @param removedUserDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<RemovedUserDTO> partialUpdate(RemovedUserDTO removedUserDTO) {
        LOG.debug("Request to partially update RemovedUser : {}", removedUserDTO);

        return removedUserRepository
                .findById(removedUserDTO.getId())
                .map(existingRemovedUser -> {
                    removedUserMapper.partialUpdate(existingRemovedUser, removedUserDTO);

                    return existingRemovedUser;
                })
                .map(removedUserRepository::save)
                .map(removedUserMapper::toDto);
    }

    /**
     * Get one removedUser by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<RemovedUserDTO> findOne(Long id) {
        LOG.debug("Request to get RemovedUser : {}", id);
        return removedUserRepository.findById(id).map(removedUserMapper::toDto);
    }

    /**
     * Delete the removedUser by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete RemovedUser : {}", id);
        removedUserRepository.findById(id).ifPresent(removedUser -> {
            Long orderHistoryId = removedUser.getOrderHistoryId();
            removedUserRepository.deleteById(id);
            if (orderHistoryId != null) {
                removedOrderSummaryRepository.deleteById(orderHistoryId);
            }
        });
    }

    /**
     * Restore a removed user.
     *
     * @param id the id of the removedUser to restore.
     * @return the new entity ID.
     */
    public Long restoreUser(Long id) {
        LOG.debug("Request to restore RemovedUser : {}", id);
        return userRemovalService.restoreUser(id);
    }
}
