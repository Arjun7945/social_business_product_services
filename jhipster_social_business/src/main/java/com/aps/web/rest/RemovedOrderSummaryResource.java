package com.aps.web.rest;

import com.aps.repository.RemovedOrderSummaryRepository;
import com.aps.service.RemovedOrderSummaryQueryService;
import com.aps.service.RemovedOrderSummaryService;
import com.aps.service.criteria.RemovedOrderSummaryCriteria;
import com.aps.service.dto.RemovedOrderSummaryDTO;
import com.aps.web.rest.errors.BadRequestAlertException;
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
 * REST controller for managing {@link com.aps.domain.RemovedOrderSummary}.
 */
@RestController
@RequestMapping("/api/removed-order-summaries")
public class RemovedOrderSummaryResource {

    private static final Logger LOG = LoggerFactory.getLogger(RemovedOrderSummaryResource.class);

    private static final String ENTITY_NAME = "removedOrderSummary";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RemovedOrderSummaryService removedOrderSummaryService;

    private final RemovedOrderSummaryRepository removedOrderSummaryRepository;

    private final RemovedOrderSummaryQueryService removedOrderSummaryQueryService;

    public RemovedOrderSummaryResource(
        RemovedOrderSummaryService removedOrderSummaryService,
        RemovedOrderSummaryRepository removedOrderSummaryRepository,
        RemovedOrderSummaryQueryService removedOrderSummaryQueryService
    ) {
        this.removedOrderSummaryService = removedOrderSummaryService;
        this.removedOrderSummaryRepository = removedOrderSummaryRepository;
        this.removedOrderSummaryQueryService = removedOrderSummaryQueryService;
    }

    /**
     * {@code POST  /removed-order-summaries} : Create a new removedOrderSummary.
     *
     * @param removedOrderSummaryDTO the removedOrderSummaryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new removedOrderSummaryDTO, or with status {@code 400 (Bad Request)} if the removedOrderSummary has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<RemovedOrderSummaryDTO> createRemovedOrderSummary(@RequestBody RemovedOrderSummaryDTO removedOrderSummaryDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save RemovedOrderSummary : {}", removedOrderSummaryDTO);
        if (removedOrderSummaryDTO.getId() != null) {
            throw new BadRequestAlertException("A new removedOrderSummary cannot already have an ID", ENTITY_NAME, "idexists");
        }
        removedOrderSummaryDTO = removedOrderSummaryService.save(removedOrderSummaryDTO);
        return ResponseEntity.created(new URI("/api/removed-order-summaries/" + removedOrderSummaryDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, removedOrderSummaryDTO.getId().toString()))
            .body(removedOrderSummaryDTO);
    }

    /**
     * {@code PUT  /removed-order-summaries/:id} : Updates an existing removedOrderSummary.
     *
     * @param id the id of the removedOrderSummaryDTO to save.
     * @param removedOrderSummaryDTO the removedOrderSummaryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated removedOrderSummaryDTO,
     * or with status {@code 400 (Bad Request)} if the removedOrderSummaryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the removedOrderSummaryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RemovedOrderSummaryDTO> updateRemovedOrderSummary(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody RemovedOrderSummaryDTO removedOrderSummaryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update RemovedOrderSummary : {}, {}", id, removedOrderSummaryDTO);
        if (removedOrderSummaryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, removedOrderSummaryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!removedOrderSummaryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        removedOrderSummaryDTO = removedOrderSummaryService.update(removedOrderSummaryDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, removedOrderSummaryDTO.getId().toString()))
            .body(removedOrderSummaryDTO);
    }

    /**
     * {@code PATCH  /removed-order-summaries/:id} : Partial updates given fields of an existing removedOrderSummary, field will ignore if it is null
     *
     * @param id the id of the removedOrderSummaryDTO to save.
     * @param removedOrderSummaryDTO the removedOrderSummaryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated removedOrderSummaryDTO,
     * or with status {@code 400 (Bad Request)} if the removedOrderSummaryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the removedOrderSummaryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the removedOrderSummaryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<RemovedOrderSummaryDTO> partialUpdateRemovedOrderSummary(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody RemovedOrderSummaryDTO removedOrderSummaryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update RemovedOrderSummary partially : {}, {}", id, removedOrderSummaryDTO);
        if (removedOrderSummaryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, removedOrderSummaryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!removedOrderSummaryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<RemovedOrderSummaryDTO> result = removedOrderSummaryService.partialUpdate(removedOrderSummaryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, removedOrderSummaryDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /removed-order-summaries} : get all the removedOrderSummaries.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of removedOrderSummaries in body.
     */
    @GetMapping("")
    public ResponseEntity<List<RemovedOrderSummaryDTO>> getAllRemovedOrderSummaries(
        RemovedOrderSummaryCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get RemovedOrderSummaries by criteria: {}", criteria);

        Page<RemovedOrderSummaryDTO> page = removedOrderSummaryQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /removed-order-summaries/count} : count all the removedOrderSummaries.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countRemovedOrderSummaries(RemovedOrderSummaryCriteria criteria) {
        LOG.debug("REST request to count RemovedOrderSummaries by criteria: {}", criteria);
        return ResponseEntity.ok().body(removedOrderSummaryQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /removed-order-summaries/:id} : get the "id" removedOrderSummary.
     *
     * @param id the id of the removedOrderSummaryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the removedOrderSummaryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RemovedOrderSummaryDTO> getRemovedOrderSummary(@PathVariable("id") Long id) {
        LOG.debug("REST request to get RemovedOrderSummary : {}", id);
        Optional<RemovedOrderSummaryDTO> removedOrderSummaryDTO = removedOrderSummaryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(removedOrderSummaryDTO);
    }

    /**
     * {@code DELETE  /removed-order-summaries/:id} : delete the "id" removedOrderSummary.
     *
     * @param id the id of the removedOrderSummaryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRemovedOrderSummary(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete RemovedOrderSummary : {}", id);
        removedOrderSummaryService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
