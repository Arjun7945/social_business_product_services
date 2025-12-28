package com.aps.web.rest;

import com.aps.repository.ReturnedOrderItemRepository;
import com.aps.service.ReturnedOrderItemQueryService;
import com.aps.service.ReturnedOrderItemService;
import com.aps.service.criteria.ReturnedOrderItemCriteria;
import com.aps.service.dto.ReturnedOrderItemDTO;
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
 * REST controller for managing {@link com.aps.domain.ReturnedOrderItem}.
 */
@RestController
@RequestMapping("/api/returned-order-items")
public class ReturnedOrderItemResource {

    private static final Logger LOG = LoggerFactory.getLogger(ReturnedOrderItemResource.class);

    private static final String ENTITY_NAME = "returnedOrderItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ReturnedOrderItemService returnedOrderItemService;

    private final ReturnedOrderItemRepository returnedOrderItemRepository;

    private final ReturnedOrderItemQueryService returnedOrderItemQueryService;

    public ReturnedOrderItemResource(
        ReturnedOrderItemService returnedOrderItemService,
        ReturnedOrderItemRepository returnedOrderItemRepository,
        ReturnedOrderItemQueryService returnedOrderItemQueryService
    ) {
        this.returnedOrderItemService = returnedOrderItemService;
        this.returnedOrderItemRepository = returnedOrderItemRepository;
        this.returnedOrderItemQueryService = returnedOrderItemQueryService;
    }

    /**
     * {@code POST  /returned-order-items} : Create a new returnedOrderItem.
     *
     * @param returnedOrderItemDTO the returnedOrderItemDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new returnedOrderItemDTO, or with status {@code 400 (Bad Request)} if the returnedOrderItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ReturnedOrderItemDTO> createReturnedOrderItem(@Valid @RequestBody ReturnedOrderItemDTO returnedOrderItemDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ReturnedOrderItem : {}", returnedOrderItemDTO);
        if (returnedOrderItemDTO.getId() != null) {
            throw new BadRequestAlertException("A new returnedOrderItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        returnedOrderItemDTO = returnedOrderItemService.save(returnedOrderItemDTO);
        return ResponseEntity.created(new URI("/api/returned-order-items/" + returnedOrderItemDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, returnedOrderItemDTO.getId().toString()))
            .body(returnedOrderItemDTO);
    }

    /**
     * {@code PUT  /returned-order-items/:id} : Updates an existing returnedOrderItem.
     *
     * @param id the id of the returnedOrderItemDTO to save.
     * @param returnedOrderItemDTO the returnedOrderItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated returnedOrderItemDTO,
     * or with status {@code 400 (Bad Request)} if the returnedOrderItemDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the returnedOrderItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ReturnedOrderItemDTO> updateReturnedOrderItem(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ReturnedOrderItemDTO returnedOrderItemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ReturnedOrderItem : {}, {}", id, returnedOrderItemDTO);
        if (returnedOrderItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, returnedOrderItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!returnedOrderItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        returnedOrderItemDTO = returnedOrderItemService.update(returnedOrderItemDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, returnedOrderItemDTO.getId().toString()))
            .body(returnedOrderItemDTO);
    }

    /**
     * {@code PATCH  /returned-order-items/:id} : Partial updates given fields of an existing returnedOrderItem, field will ignore if it is null
     *
     * @param id the id of the returnedOrderItemDTO to save.
     * @param returnedOrderItemDTO the returnedOrderItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated returnedOrderItemDTO,
     * or with status {@code 400 (Bad Request)} if the returnedOrderItemDTO is not valid,
     * or with status {@code 404 (Not Found)} if the returnedOrderItemDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the returnedOrderItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ReturnedOrderItemDTO> partialUpdateReturnedOrderItem(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ReturnedOrderItemDTO returnedOrderItemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ReturnedOrderItem partially : {}, {}", id, returnedOrderItemDTO);
        if (returnedOrderItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, returnedOrderItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!returnedOrderItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ReturnedOrderItemDTO> result = returnedOrderItemService.partialUpdate(returnedOrderItemDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, returnedOrderItemDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /returned-order-items} : get all the returnedOrderItems.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of returnedOrderItems in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ReturnedOrderItemDTO>> getAllReturnedOrderItems(
        ReturnedOrderItemCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get ReturnedOrderItems by criteria: {}", criteria);

        Page<ReturnedOrderItemDTO> page = returnedOrderItemQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /returned-order-items/count} : count all the returnedOrderItems.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countReturnedOrderItems(ReturnedOrderItemCriteria criteria) {
        LOG.debug("REST request to count ReturnedOrderItems by criteria: {}", criteria);
        return ResponseEntity.ok().body(returnedOrderItemQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /returned-order-items/:id} : get the "id" returnedOrderItem.
     *
     * @param id the id of the returnedOrderItemDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the returnedOrderItemDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReturnedOrderItemDTO> getReturnedOrderItem(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ReturnedOrderItem : {}", id);
        Optional<ReturnedOrderItemDTO> returnedOrderItemDTO = returnedOrderItemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(returnedOrderItemDTO);
    }

    /**
     * {@code DELETE  /returned-order-items/:id} : delete the "id" returnedOrderItem.
     *
     * @param id the id of the returnedOrderItemDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReturnedOrderItem(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ReturnedOrderItem : {}", id);
        returnedOrderItemService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
