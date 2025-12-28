package com.aps.web.rest;

import com.aps.domain.RemovedOrderSummary;
import com.aps.repository.RemovedOrderSummaryRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

@RestController
@RequestMapping("/api")
@Transactional
public class RemovedOrderSummaryResource {

    private final Logger log = LoggerFactory.getLogger(RemovedOrderSummaryResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RemovedOrderSummaryRepository removedOrderSummaryRepository;

    public RemovedOrderSummaryResource(RemovedOrderSummaryRepository removedOrderSummaryRepository) {
        this.removedOrderSummaryRepository = removedOrderSummaryRepository;
    }

    /**
     * {@code GET  /removed-order-summaries} : get all the removedOrderSummaries.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of removedOrderSummaries in body.
     */
    @GetMapping("/removed-order-summaries")
    public ResponseEntity<List<RemovedOrderSummary>> getAllRemovedOrderSummaries(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get a page of RemovedOrderSummaries");
        Page<RemovedOrderSummary> page = removedOrderSummaryRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /removed-order-summaries/:id} : get the "id" removedOrderSummary.
     *
     * @param id the id of the removedOrderSummary to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the removedOrderSummary, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/removed-order-summaries/{id}")
    public ResponseEntity<RemovedOrderSummary> getRemovedOrderSummary(@PathVariable Long id) {
        log.debug("REST request to get RemovedOrderSummary : {}", id);
        Optional<RemovedOrderSummary> removedOrderSummary = removedOrderSummaryRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(removedOrderSummary);
    }
}
