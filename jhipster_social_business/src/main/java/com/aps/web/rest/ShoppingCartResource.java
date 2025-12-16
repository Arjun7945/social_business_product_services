package com.aps.web.rest;

import com.aps.repository.ShoppingCartRepository;
import com.aps.service.ShoppingCartQueryService;
import com.aps.service.ShoppingCartService;
import com.aps.service.criteria.ShoppingCartCriteria;
import com.aps.service.dto.ShoppingCartDTO;
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
 * REST controller for managing {@link com.aps.domain.ShoppingCart}.
 */
@RestController
@RequestMapping("/api/shopping-carts")
public class ShoppingCartResource {

    private static final Logger LOG = LoggerFactory.getLogger(ShoppingCartResource.class);

    private static final String ENTITY_NAME = "shoppingCart";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ShoppingCartService shoppingCartService;

    private final ShoppingCartRepository shoppingCartRepository;

    private final ShoppingCartQueryService shoppingCartQueryService;

    public ShoppingCartResource(
        ShoppingCartService shoppingCartService,
        ShoppingCartRepository shoppingCartRepository,
        ShoppingCartQueryService shoppingCartQueryService
    ) {
        this.shoppingCartService = shoppingCartService;
        this.shoppingCartRepository = shoppingCartRepository;
        this.shoppingCartQueryService = shoppingCartQueryService;
    }

    /**
     * {@code POST  /shopping-carts} : Create a new shoppingCart.
     *
     * @param shoppingCartDTO the shoppingCartDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new shoppingCartDTO, or with status {@code 400 (Bad Request)} if the shoppingCart has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ShoppingCartDTO> createShoppingCart(@Valid @RequestBody ShoppingCartDTO shoppingCartDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ShoppingCart : {}", shoppingCartDTO);
        if (shoppingCartDTO.getId() != null) {
            throw new BadRequestAlertException("A new shoppingCart cannot already have an ID", ENTITY_NAME, "idexists");
        }
        shoppingCartDTO = shoppingCartService.save(shoppingCartDTO);
        return ResponseEntity.created(new URI("/api/shopping-carts/" + shoppingCartDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, shoppingCartDTO.getId().toString()))
            .body(shoppingCartDTO);
    }

    /**
     * {@code PUT  /shopping-carts/:id} : Updates an existing shoppingCart.
     *
     * @param id the id of the shoppingCartDTO to save.
     * @param shoppingCartDTO the shoppingCartDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shoppingCartDTO,
     * or with status {@code 400 (Bad Request)} if the shoppingCartDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the shoppingCartDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ShoppingCartDTO> updateShoppingCart(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ShoppingCartDTO shoppingCartDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ShoppingCart : {}, {}", id, shoppingCartDTO);
        if (shoppingCartDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shoppingCartDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!shoppingCartRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        shoppingCartDTO = shoppingCartService.update(shoppingCartDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, shoppingCartDTO.getId().toString()))
            .body(shoppingCartDTO);
    }

    /**
     * {@code PATCH  /shopping-carts/:id} : Partial updates given fields of an existing shoppingCart, field will ignore if it is null
     *
     * @param id the id of the shoppingCartDTO to save.
     * @param shoppingCartDTO the shoppingCartDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shoppingCartDTO,
     * or with status {@code 400 (Bad Request)} if the shoppingCartDTO is not valid,
     * or with status {@code 404 (Not Found)} if the shoppingCartDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the shoppingCartDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ShoppingCartDTO> partialUpdateShoppingCart(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ShoppingCartDTO shoppingCartDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ShoppingCart partially : {}, {}", id, shoppingCartDTO);
        if (shoppingCartDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shoppingCartDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!shoppingCartRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ShoppingCartDTO> result = shoppingCartService.partialUpdate(shoppingCartDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, shoppingCartDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /shopping-carts} : get all the shoppingCarts.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of shoppingCarts in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ShoppingCartDTO>> getAllShoppingCarts(
        ShoppingCartCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get ShoppingCarts by criteria: {}", criteria);

        Page<ShoppingCartDTO> page = shoppingCartQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /shopping-carts/count} : count all the shoppingCarts.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countShoppingCarts(ShoppingCartCriteria criteria) {
        LOG.debug("REST request to count ShoppingCarts by criteria: {}", criteria);
        return ResponseEntity.ok().body(shoppingCartQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /shopping-carts/:id} : get the "id" shoppingCart.
     *
     * @param id the id of the shoppingCartDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the shoppingCartDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ShoppingCartDTO> getShoppingCart(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ShoppingCart : {}", id);
        Optional<ShoppingCartDTO> shoppingCartDTO = shoppingCartService.findOne(id);
        return ResponseUtil.wrapOrNotFound(shoppingCartDTO);
    }

    /**
     * {@code DELETE  /shopping-carts/:id} : delete the "id" shoppingCart.
     *
     * @param id the id of the shoppingCartDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShoppingCart(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ShoppingCart : {}", id);
        shoppingCartService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
