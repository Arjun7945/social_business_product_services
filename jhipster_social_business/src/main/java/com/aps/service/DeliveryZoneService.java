package com.aps.service;

import com.aps.domain.DeliveryZone;
import com.aps.repository.DeliveryZoneRepository;
import com.aps.service.dto.DeliveryZoneDTO;
import com.aps.service.mapper.DeliveryZoneMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.aps.domain.DeliveryZone}.
 */
@Service
@Transactional
public class DeliveryZoneService {

    private static final Logger LOG = LoggerFactory.getLogger(DeliveryZoneService.class);

    private final DeliveryZoneRepository deliveryZoneRepository;

    private final DeliveryZoneMapper deliveryZoneMapper;

    public DeliveryZoneService(DeliveryZoneRepository deliveryZoneRepository, DeliveryZoneMapper deliveryZoneMapper) {
        this.deliveryZoneRepository = deliveryZoneRepository;
        this.deliveryZoneMapper = deliveryZoneMapper;
    }

    /**
     * Save a deliveryZone.
     *
     * @param deliveryZoneDTO the entity to save.
     * @return the persisted entity.
     */
    public DeliveryZoneDTO save(DeliveryZoneDTO deliveryZoneDTO) {
        LOG.debug("Request to save DeliveryZone : {}", deliveryZoneDTO);
        DeliveryZone deliveryZone = deliveryZoneMapper.toEntity(deliveryZoneDTO);
        deliveryZone = deliveryZoneRepository.save(deliveryZone);
        return deliveryZoneMapper.toDto(deliveryZone);
    }

    /**
     * Update a deliveryZone.
     *
     * @param deliveryZoneDTO the entity to save.
     * @return the persisted entity.
     */
    public DeliveryZoneDTO update(DeliveryZoneDTO deliveryZoneDTO) {
        LOG.debug("Request to update DeliveryZone : {}", deliveryZoneDTO);
        DeliveryZone deliveryZone = deliveryZoneMapper.toEntity(deliveryZoneDTO);
        deliveryZone = deliveryZoneRepository.save(deliveryZone);
        return deliveryZoneMapper.toDto(deliveryZone);
    }

    /**
     * Partially update a deliveryZone.
     *
     * @param deliveryZoneDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<DeliveryZoneDTO> partialUpdate(DeliveryZoneDTO deliveryZoneDTO) {
        LOG.debug("Request to partially update DeliveryZone : {}", deliveryZoneDTO);

        return deliveryZoneRepository
            .findById(deliveryZoneDTO.getId())
            .map(existingDeliveryZone -> {
                deliveryZoneMapper.partialUpdate(existingDeliveryZone, deliveryZoneDTO);

                return existingDeliveryZone;
            })
            .map(deliveryZoneRepository::save)
            .map(deliveryZoneMapper::toDto);
    }

    /**
     * Get one deliveryZone by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<DeliveryZoneDTO> findOne(Long id) {
        LOG.debug("Request to get DeliveryZone : {}", id);
        return deliveryZoneRepository.findById(id).map(deliveryZoneMapper::toDto);
    }

    /**
     * Delete the deliveryZone by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete DeliveryZone : {}", id);
        deliveryZoneRepository.deleteById(id);
    }
}
