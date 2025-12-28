package com.aps.web.rest;

import com.aps.repository.ReturnStatusHistoryRepository;
import com.aps.service.ReturnStatusHistoryQueryService;
import com.aps.service.ReturnStatusHistoryService;
import com.aps.service.criteria.ReturnStatusHistoryCriteria;
import com.aps.service.dto.ReturnStatusHistoryDTO;
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
 * REST controller for managing {@link com.aps.domain.ReturnStatusHistory}.
 */
@RestController
@RequestMapping("/api/return-status-histories")
public class ReturnStatusHistoryResource {

    private static final Logger LOG = LoggerFactory.getLogger(ReturnStatusHistoryResource.class);

    private static final String ENTITY_NAME = "returnStatusHistory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ReturnStatusHistoryService returnStatusHistoryService;

    private final ReturnStatusHistoryRepository returnStatusHistoryRepository;

    private final ReturnStatusHistoryQueryService returnStatusHistoryQueryService;

    public ReturnStatusHistoryResource(
        ReturnStatusHistoryService returnStatusHistoryService,
        ReturnStatusHistoryRepository returnStatusHistoryRepository,
        ReturnStatusHistoryQueryService returnStatusHistoryQueryService
    ) {
        this.returnStatusHistoryService = returnStatusHistoryService;
        this.returnStatusHistoryRepository = returnStatusHistoryRepository;
        this.returnStatusHistoryQueryService = returnStatusHistoryQueryService;
    }

    /**
     * {@code POST  /return-status-histories} : Create a new returnStatusHistory.
     *
     * @param returnStatusHistoryDTO the returnStatusHistoryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new returnStatusHistoryDTO, or with status {@code 400 (Bad Request)} if the returnStatusHistory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ReturnStatusHistoryDTO> createReturnStatusHistory(
        @Valid @RequestBody ReturnStatusHistoryDTO returnStatusHistoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save ReturnStatusHistory : {}", returnStatusHistoryDTO);
        if (returnStatusHistoryDTO.getId() != null) {
            throw new BadRequestAlertException("A new returnStatusHistory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        returnStatusHistoryDTO = returnStatusHistoryService.save(returnStatusHistoryDTO);
        return ResponseEntity.created(new URI("/api/return-status-histories/" + returnStatusHistoryDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, returnStatusHistoryDTO.getId().toString()))
            .body(returnStatusHistoryDTO);
    }

    /**
     * {@code PUT  /return-status-histories/:id} : Updates an existing returnStatusHistory.
     *
     * @param id the id of the returnStatusHistoryDTO to save.
     * @param returnStatusHistoryDTO the returnStatusHistoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated returnStatusHistoryDTO,
     * or with status {@code 400 (Bad Request)} if the returnStatusHistoryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the returnStatusHistoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ReturnStatusHistoryDTO> updateReturnStatusHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ReturnStatusHistoryDTO returnStatusHistoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ReturnStatusHistory : {}, {}", id, returnStatusHistoryDTO);
        if (returnStatusHistoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, returnStatusHistoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!returnStatusHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        returnStatusHistoryDTO = returnStatusHistoryService.update(returnStatusHistoryDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, returnStatusHistoryDTO.getId().toString()))
            .body(returnStatusHistoryDTO);
    }

    /**
     * {@code PATCH  /return-status-histories/:id} : Partial updates given fields of an existing returnStatusHistory, field will ignore if it is null
     *
     * @param id the id of the returnStatusHistoryDTO to save.
     * @param returnStatusHistoryDTO the returnStatusHistoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated returnStatusHistoryDTO,
     * or with status {@code 400 (Bad Request)} if the returnStatusHistoryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the returnStatusHistoryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the returnStatusHistoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ReturnStatusHistoryDTO> partialUpdateReturnStatusHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ReturnStatusHistoryDTO returnStatusHistoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ReturnStatusHistory partially : {}, {}", id, returnStatusHistoryDTO);
        if (returnStatusHistoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, returnStatusHistoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!returnStatusHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ReturnStatusHistoryDTO> result = returnStatusHistoryService.partialUpdate(returnStatusHistoryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, returnStatusHistoryDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /return-status-histories} : get all the returnStatusHistories.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of returnStatusHistories in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ReturnStatusHistoryDTO>> getAllReturnStatusHistories(
        ReturnStatusHistoryCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get ReturnStatusHistories by criteria: {}", criteria);

        Page<ReturnStatusHistoryDTO> page = returnStatusHistoryQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /return-status-histories/count} : count all the returnStatusHistories.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countReturnStatusHistories(ReturnStatusHistoryCriteria criteria) {
        LOG.debug("REST request to count ReturnStatusHistories by criteria: {}", criteria);
        return ResponseEntity.ok().body(returnStatusHistoryQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /return-status-histories/:id} : get the "id" returnStatusHistory.
     *
     * @param id the id of the returnStatusHistoryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the returnStatusHistoryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReturnStatusHistoryDTO> getReturnStatusHistory(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ReturnStatusHistory : {}", id);
        Optional<ReturnStatusHistoryDTO> returnStatusHistoryDTO = returnStatusHistoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(returnStatusHistoryDTO);
    }

    /**
     * {@code DELETE  /return-status-histories/:id} : delete the "id" returnStatusHistory.
     *
     * @param id the id of the returnStatusHistoryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReturnStatusHistory(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ReturnStatusHistory : {}", id);
        returnStatusHistoryService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
