package com.aps.web.rest;

import com.aps.repository.ReturnedOrderRepository;
import com.aps.service.ReturnedOrderQueryService;
import com.aps.service.ReturnedOrderService;
import com.aps.service.criteria.ReturnedOrderCriteria;
import com.aps.service.dto.ReturnedOrderDTO;
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
 * REST controller for managing {@link com.aps.domain.ReturnedOrder}.
 */
@RestController
@RequestMapping("/api/returned-orders")
public class ReturnedOrderResource {

    private static final Logger LOG = LoggerFactory.getLogger(ReturnedOrderResource.class);

    private static final String ENTITY_NAME = "returnedOrder";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ReturnedOrderService returnedOrderService;

    private final ReturnedOrderRepository returnedOrderRepository;

    private final ReturnedOrderQueryService returnedOrderQueryService;

    public ReturnedOrderResource(
        ReturnedOrderService returnedOrderService,
        ReturnedOrderRepository returnedOrderRepository,
        ReturnedOrderQueryService returnedOrderQueryService
    ) {
        this.returnedOrderService = returnedOrderService;
        this.returnedOrderRepository = returnedOrderRepository;
        this.returnedOrderQueryService = returnedOrderQueryService;
    }

    /**
     * {@code POST  /returned-orders} : Create a new returnedOrder.
     *
     * @param returnedOrderDTO the returnedOrderDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new returnedOrderDTO, or with status {@code 400 (Bad Request)} if the returnedOrder has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ReturnedOrderDTO> createReturnedOrder(@Valid @RequestBody ReturnedOrderDTO returnedOrderDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ReturnedOrder : {}", returnedOrderDTO);
        if (returnedOrderDTO.getId() != null) {
            throw new BadRequestAlertException("A new returnedOrder cannot already have an ID", ENTITY_NAME, "idexists");
        }
        returnedOrderDTO = returnedOrderService.save(returnedOrderDTO);
        return ResponseEntity.created(new URI("/api/returned-orders/" + returnedOrderDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, returnedOrderDTO.getId().toString()))
            .body(returnedOrderDTO);
    }

    /**
     * {@code PUT  /returned-orders/:id} : Updates an existing returnedOrder.
     *
     * @param id the id of the returnedOrderDTO to save.
     * @param returnedOrderDTO the returnedOrderDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated returnedOrderDTO,
     * or with status {@code 400 (Bad Request)} if the returnedOrderDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the returnedOrderDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ReturnedOrderDTO> updateReturnedOrder(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ReturnedOrderDTO returnedOrderDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ReturnedOrder : {}, {}", id, returnedOrderDTO);
        if (returnedOrderDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, returnedOrderDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!returnedOrderRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        returnedOrderDTO = returnedOrderService.update(returnedOrderDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, returnedOrderDTO.getId().toString()))
            .body(returnedOrderDTO);
    }

    /**
     * {@code PATCH  /returned-orders/:id} : Partial updates given fields of an existing returnedOrder, field will ignore if it is null
     *
     * @param id the id of the returnedOrderDTO to save.
     * @param returnedOrderDTO the returnedOrderDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated returnedOrderDTO,
     * or with status {@code 400 (Bad Request)} if the returnedOrderDTO is not valid,
     * or with status {@code 404 (Not Found)} if the returnedOrderDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the returnedOrderDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ReturnedOrderDTO> partialUpdateReturnedOrder(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ReturnedOrderDTO returnedOrderDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ReturnedOrder partially : {}, {}", id, returnedOrderDTO);
        if (returnedOrderDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, returnedOrderDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!returnedOrderRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ReturnedOrderDTO> result = returnedOrderService.partialUpdate(returnedOrderDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, returnedOrderDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /returned-orders} : get all the returnedOrders.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of returnedOrders in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ReturnedOrderDTO>> getAllReturnedOrders(
        ReturnedOrderCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get ReturnedOrders by criteria: {}", criteria);

        Page<ReturnedOrderDTO> page = returnedOrderQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /returned-orders/count} : count all the returnedOrders.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countReturnedOrders(ReturnedOrderCriteria criteria) {
        LOG.debug("REST request to count ReturnedOrders by criteria: {}", criteria);
        return ResponseEntity.ok().body(returnedOrderQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /returned-orders/:id} : get the "id" returnedOrder.
     *
     * @param id the id of the returnedOrderDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the returnedOrderDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReturnedOrderDTO> getReturnedOrder(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ReturnedOrder : {}", id);
        Optional<ReturnedOrderDTO> returnedOrderDTO = returnedOrderService.findOne(id);
        return ResponseUtil.wrapOrNotFound(returnedOrderDTO);
    }

    /**
     * {@code DELETE  /returned-orders/:id} : delete the "id" returnedOrder.
     *
     * @param id the id of the returnedOrderDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReturnedOrder(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ReturnedOrder : {}", id);
        returnedOrderService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
