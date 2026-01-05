package com.aps.service;

import com.aps.domain.DeliveryPerson;
import com.aps.repository.DeliveryPersonRepository;
import com.aps.service.dto.DeliveryPersonDTO;
import com.aps.service.mapper.DeliveryPersonMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.aps.domain.DeliveryPerson}.
 */
@Service
@Transactional
public class DeliveryPersonService {

    private static final Logger LOG = LoggerFactory.getLogger(DeliveryPersonService.class);

    private final DeliveryPersonRepository deliveryPersonRepository;

    private final DeliveryPersonMapper deliveryPersonMapper;

    public DeliveryPersonService(DeliveryPersonRepository deliveryPersonRepository,
            DeliveryPersonMapper deliveryPersonMapper) {
        this.deliveryPersonRepository = deliveryPersonRepository;
        this.deliveryPersonMapper = deliveryPersonMapper;
    }

    /**
     * Save a deliveryPerson.
     *
     * @param deliveryPersonDTO the entity to save.
     * @return the persisted entity.
     */
    public DeliveryPersonDTO save(DeliveryPersonDTO deliveryPersonDTO) {
        LOG.debug("Request to save DeliveryPerson : {}", deliveryPersonDTO);
        DeliveryPerson deliveryPerson = deliveryPersonMapper.toEntity(deliveryPersonDTO);
        deliveryPerson = deliveryPersonRepository.save(deliveryPerson);
        return deliveryPersonMapper.toDto(deliveryPerson);
    }

    /**
     * Update a deliveryPerson.
     *
     * @param deliveryPersonDTO the entity to save.
     * @return the persisted entity.
     */
    public DeliveryPersonDTO update(DeliveryPersonDTO deliveryPersonDTO) {
        LOG.debug("Request to update DeliveryPerson : {}", deliveryPersonDTO);
        DeliveryPerson deliveryPerson = deliveryPersonMapper.toEntity(deliveryPersonDTO);
        deliveryPerson = deliveryPersonRepository.save(deliveryPerson);
        return deliveryPersonMapper.toDto(deliveryPerson);
    }

    /**
     * Partially update a deliveryPerson.
     *
     * @param deliveryPersonDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<DeliveryPersonDTO> partialUpdate(DeliveryPersonDTO deliveryPersonDTO) {
        LOG.debug("Request to partially update DeliveryPerson : {}", deliveryPersonDTO);

        return deliveryPersonRepository
                .findById(deliveryPersonDTO.getId())
                .map(existingDeliveryPerson -> {
                    deliveryPersonMapper.partialUpdate(existingDeliveryPerson, deliveryPersonDTO);

                    return existingDeliveryPerson;
                })
                .map(deliveryPersonRepository::save)
                .map(deliveryPersonMapper::toDto);
    }

    /**
     * Get all the deliveryPeople with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<DeliveryPersonDTO> findAllWithEagerRelationships(Pageable pageable) {
        return deliveryPersonRepository.findAllWithEagerRelationships(pageable).map(deliveryPersonMapper::toDto);
    }

    /**
     * Get one deliveryPerson by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<DeliveryPersonDTO> findOne(Long id) {
        LOG.debug("Request to get DeliveryPerson : {}", id);
        return deliveryPersonRepository.findOneWithEagerRelationships(id).map(deliveryPersonMapper::toDto);
    }

    /**
     * Delete the deliveryPerson by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete DeliveryPerson : {}", id);
        deliveryPersonRepository.deleteById(id);
    }

    /**
     * Removes an order ID from the chosen_order CSV string.
     *
     * @param deliveryPersonId the id of the delivery person.
     * @param orderId          the id of the order to remove.
     */
    public void removeOrderFromChosenList(Long deliveryPersonId, Long orderId) {
        LOG.debug("Request to remove order {} from chosen list of DeliveryPerson {}", orderId, deliveryPersonId);
        deliveryPersonRepository.findById(deliveryPersonId).ifPresent(deliveryPerson -> {
            String currentChosen = deliveryPerson.getChosenOrder();
            if (currentChosen != null && !currentChosen.isEmpty()) {
                java.util.List<String> validOrders = new java.util.ArrayList<>(
                        java.util.Arrays.asList(currentChosen.split(",")));
                boolean removed = validOrders.remove(String.valueOf(orderId));
                if (removed) {
                    deliveryPerson.setChosenOrder(String.join(",", validOrders));
                    deliveryPersonRepository.save(deliveryPerson);
                    LOG.info("Removed Order {} from DeliveryPerson {} chosen list", orderId, deliveryPersonId);
                }
            }
        });
    }
}
