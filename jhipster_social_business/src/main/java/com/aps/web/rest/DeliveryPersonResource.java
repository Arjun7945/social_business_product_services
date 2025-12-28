package com.aps.web.rest;

import com.aps.repository.DeliveryPersonRepository;
import com.aps.service.DeliveryPersonQueryService;
import com.aps.service.DeliveryPersonService;
import com.aps.service.criteria.DeliveryPersonCriteria;
import com.aps.service.dto.DeliveryPersonDTO;
import com.aps.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.aps.domain.DeliveryPerson}.
 */
@RestController
@RequestMapping("/api/delivery-people")
public class DeliveryPersonResource {

    private static final Logger LOG = LoggerFactory.getLogger(DeliveryPersonResource.class);

    private static final String ENTITY_NAME = "deliveryPerson";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DeliveryPersonService deliveryPersonService;

    private final DeliveryPersonRepository deliveryPersonRepository;

    private final DeliveryPersonQueryService deliveryPersonQueryService;

    public DeliveryPersonResource(
        DeliveryPersonService deliveryPersonService,
        DeliveryPersonRepository deliveryPersonRepository,
        DeliveryPersonQueryService deliveryPersonQueryService
    ) {
        this.deliveryPersonService = deliveryPersonService;
        this.deliveryPersonRepository = deliveryPersonRepository;
        this.deliveryPersonQueryService = deliveryPersonQueryService;
    }

    /**
     * {@code POST  /delivery-people} : Create a new deliveryPerson.
     *
     * @param deliveryPersonDTO the deliveryPersonDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new deliveryPersonDTO, or with status {@code 400 (Bad Request)} if the deliveryPerson has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<DeliveryPersonDTO> createDeliveryPerson(@Valid @RequestBody DeliveryPersonDTO deliveryPersonDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save DeliveryPerson : {}", deliveryPersonDTO);
        if (deliveryPersonDTO.getId() != null) {
            throw new BadRequestAlertException("A new deliveryPerson cannot already have an ID", ENTITY_NAME, "idexists");
        }
        deliveryPersonDTO = deliveryPersonService.save(deliveryPersonDTO);
        return ResponseEntity.created(new URI("/api/delivery-people/" + deliveryPersonDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, deliveryPersonDTO.getId().toString()))
            .body(deliveryPersonDTO);
    }

    /**
     * {@code PUT  /delivery-people/:id} : Updates an existing deliveryPerson.
     *
     * @param id the id of the deliveryPersonDTO to save.
     * @param deliveryPersonDTO the deliveryPersonDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated deliveryPersonDTO,
     * or with status {@code 400 (Bad Request)} if the deliveryPersonDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the deliveryPersonDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DeliveryPersonDTO> updateDeliveryPerson(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DeliveryPersonDTO deliveryPersonDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update DeliveryPerson : {}, {}", id, deliveryPersonDTO);
        if (deliveryPersonDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, deliveryPersonDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!deliveryPersonRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        deliveryPersonDTO = deliveryPersonService.update(deliveryPersonDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, deliveryPersonDTO.getId().toString()))
            .body(deliveryPersonDTO);
    }

    /**
     * {@code PATCH  /delivery-people/:id} : Partial updates given fields of an existing deliveryPerson, field will ignore if it is null
     *
     * @param id the id of the deliveryPersonDTO to save.
     * @param deliveryPersonDTO the deliveryPersonDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated deliveryPersonDTO,
     * or with status {@code 400 (Bad Request)} if the deliveryPersonDTO is not valid,
     * or with status {@code 404 (Not Found)} if the deliveryPersonDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the deliveryPersonDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DeliveryPersonDTO> partialUpdateDeliveryPerson(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DeliveryPersonDTO deliveryPersonDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update DeliveryPerson partially : {}, {}", id, deliveryPersonDTO);
        if (deliveryPersonDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, deliveryPersonDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!deliveryPersonRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DeliveryPersonDTO> result = deliveryPersonService.partialUpdate(deliveryPersonDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, deliveryPersonDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /delivery-people} : get all the deliveryPeople.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of deliveryPeople in body.
     */
    @GetMapping("")
    public ResponseEntity<List<DeliveryPersonDTO>> getAllDeliveryPeople(
        DeliveryPersonCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get DeliveryPeople by criteria: {}", criteria);

        Page<DeliveryPersonDTO> page = deliveryPersonQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /delivery-people/count} : count all the deliveryPeople.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countDeliveryPeople(DeliveryPersonCriteria criteria) {
        LOG.debug("REST request to count DeliveryPeople by criteria: {}", criteria);
        return ResponseEntity.ok().body(deliveryPersonQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /delivery-people/:id} : get the "id" deliveryPerson.
     *
     * @param id the id of the deliveryPersonDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the deliveryPersonDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DeliveryPersonDTO> getDeliveryPerson(@PathVariable("id") Long id) {
        LOG.debug("REST request to get DeliveryPerson : {}", id);
        Optional<DeliveryPersonDTO> deliveryPersonDTO = deliveryPersonService.findOne(id);
        return ResponseUtil.wrapOrNotFound(deliveryPersonDTO);
    }

    /**
     * {@code DELETE  /delivery-people/:id} : delete the "id" deliveryPerson.
     *
     * @param id the id of the deliveryPersonDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeliveryPerson(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete DeliveryPerson : {}", id);
        deliveryPersonService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
