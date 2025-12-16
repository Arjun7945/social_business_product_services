package com.aps.web.rest;

import com.aps.repository.FishProductRepository;
import com.aps.service.FishProductQueryService;
import com.aps.service.FishProductService;
import com.aps.service.criteria.FishProductCriteria;
import com.aps.service.dto.FishProductDTO;
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
 * REST controller for managing {@link com.aps.domain.FishProduct}.
 */
@RestController
@RequestMapping("/api/fish-products")
public class FishProductResource {

    private static final Logger LOG = LoggerFactory.getLogger(FishProductResource.class);

    private static final String ENTITY_NAME = "fishProduct";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FishProductService fishProductService;

    private final FishProductRepository fishProductRepository;

    private final FishProductQueryService fishProductQueryService;

    public FishProductResource(
        FishProductService fishProductService,
        FishProductRepository fishProductRepository,
        FishProductQueryService fishProductQueryService
    ) {
        this.fishProductService = fishProductService;
        this.fishProductRepository = fishProductRepository;
        this.fishProductQueryService = fishProductQueryService;
    }

    /**
     * {@code POST  /fish-products} : Create a new fishProduct.
     *
     * @param fishProductDTO the fishProductDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new fishProductDTO, or with status {@code 400 (Bad Request)} if the fishProduct has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<FishProductDTO> createFishProduct(@Valid @RequestBody FishProductDTO fishProductDTO) throws URISyntaxException {
        LOG.debug("REST request to save FishProduct : {}", fishProductDTO);
        if (fishProductDTO.getId() != null) {
            throw new BadRequestAlertException("A new fishProduct cannot already have an ID", ENTITY_NAME, "idexists");
        }
        fishProductDTO = fishProductService.save(fishProductDTO);
        return ResponseEntity.created(new URI("/api/fish-products/" + fishProductDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, fishProductDTO.getId().toString()))
            .body(fishProductDTO);
    }

    /**
     * {@code PUT  /fish-products/:id} : Updates an existing fishProduct.
     *
     * @param id the id of the fishProductDTO to save.
     * @param fishProductDTO the fishProductDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fishProductDTO,
     * or with status {@code 400 (Bad Request)} if the fishProductDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the fishProductDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<FishProductDTO> updateFishProduct(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody FishProductDTO fishProductDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update FishProduct : {}, {}", id, fishProductDTO);
        if (fishProductDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, fishProductDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!fishProductRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        fishProductDTO = fishProductService.update(fishProductDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, fishProductDTO.getId().toString()))
            .body(fishProductDTO);
    }

    /**
     * {@code PATCH  /fish-products/:id} : Partial updates given fields of an existing fishProduct, field will ignore if it is null
     *
     * @param id the id of the fishProductDTO to save.
     * @param fishProductDTO the fishProductDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fishProductDTO,
     * or with status {@code 400 (Bad Request)} if the fishProductDTO is not valid,
     * or with status {@code 404 (Not Found)} if the fishProductDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the fishProductDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<FishProductDTO> partialUpdateFishProduct(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody FishProductDTO fishProductDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update FishProduct partially : {}, {}", id, fishProductDTO);
        if (fishProductDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, fishProductDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!fishProductRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FishProductDTO> result = fishProductService.partialUpdate(fishProductDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, fishProductDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /fish-products} : get all the fishProducts.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of fishProducts in body.
     */
    @GetMapping("")
    public ResponseEntity<List<FishProductDTO>> getAllFishProducts(
        FishProductCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get FishProducts by criteria: {}", criteria);

        Page<FishProductDTO> page = fishProductQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /fish-products/count} : count all the fishProducts.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countFishProducts(FishProductCriteria criteria) {
        LOG.debug("REST request to count FishProducts by criteria: {}", criteria);
        return ResponseEntity.ok().body(fishProductQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /fish-products/:id} : get the "id" fishProduct.
     *
     * @param id the id of the fishProductDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the fishProductDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<FishProductDTO> getFishProduct(@PathVariable("id") Long id) {
        LOG.debug("REST request to get FishProduct : {}", id);
        Optional<FishProductDTO> fishProductDTO = fishProductService.findOne(id);
        return ResponseUtil.wrapOrNotFound(fishProductDTO);
    }

    /**
     * {@code DELETE  /fish-products/:id} : delete the "id" fishProduct.
     *
     * @param id the id of the fishProductDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFishProduct(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete FishProduct : {}", id);
        fishProductService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
