package com.aps.web.rest;

import com.aps.repository.CartItemRepository;
import com.aps.service.CartItemQueryService;
import com.aps.service.CartItemService;
import com.aps.service.criteria.CartItemCriteria;
import com.aps.service.dto.CartItemDTO;
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
 * REST controller for managing {@link com.aps.domain.CartItem}.
 */
@RestController
@RequestMapping("/api/cart-items")
public class CartItemResource {

    private static final Logger LOG = LoggerFactory.getLogger(CartItemResource.class);

    private static final String ENTITY_NAME = "cartItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CartItemService cartItemService;

    private final CartItemRepository cartItemRepository;

    private final CartItemQueryService cartItemQueryService;

    public CartItemResource(
        CartItemService cartItemService,
        CartItemRepository cartItemRepository,
        CartItemQueryService cartItemQueryService
    ) {
        this.cartItemService = cartItemService;
        this.cartItemRepository = cartItemRepository;
        this.cartItemQueryService = cartItemQueryService;
    }

    /**
     * {@code POST  /cart-items} : Create a new cartItem.
     *
     * @param cartItemDTO the cartItemDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new cartItemDTO, or with status {@code 400 (Bad Request)} if the cartItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CartItemDTO> createCartItem(@Valid @RequestBody CartItemDTO cartItemDTO) throws URISyntaxException {
        LOG.debug("REST request to save CartItem : {}", cartItemDTO);
        if (cartItemDTO.getId() != null) {
            throw new BadRequestAlertException("A new cartItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        cartItemDTO = cartItemService.save(cartItemDTO);
        return ResponseEntity.created(new URI("/api/cart-items/" + cartItemDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, cartItemDTO.getId().toString()))
            .body(cartItemDTO);
    }

    /**
     * {@code PUT  /cart-items/:id} : Updates an existing cartItem.
     *
     * @param id the id of the cartItemDTO to save.
     * @param cartItemDTO the cartItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cartItemDTO,
     * or with status {@code 400 (Bad Request)} if the cartItemDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the cartItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CartItemDTO> updateCartItem(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CartItemDTO cartItemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CartItem : {}, {}", id, cartItemDTO);
        if (cartItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cartItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cartItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        cartItemDTO = cartItemService.update(cartItemDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, cartItemDTO.getId().toString()))
            .body(cartItemDTO);
    }

    /**
     * {@code PATCH  /cart-items/:id} : Partial updates given fields of an existing cartItem, field will ignore if it is null
     *
     * @param id the id of the cartItemDTO to save.
     * @param cartItemDTO the cartItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cartItemDTO,
     * or with status {@code 400 (Bad Request)} if the cartItemDTO is not valid,
     * or with status {@code 404 (Not Found)} if the cartItemDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the cartItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CartItemDTO> partialUpdateCartItem(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CartItemDTO cartItemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CartItem partially : {}, {}", id, cartItemDTO);
        if (cartItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cartItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cartItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CartItemDTO> result = cartItemService.partialUpdate(cartItemDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, cartItemDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /cart-items} : get all the cartItems.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of cartItems in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CartItemDTO>> getAllCartItems(
        CartItemCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get CartItems by criteria: {}", criteria);

        Page<CartItemDTO> page = cartItemQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /cart-items/count} : count all the cartItems.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countCartItems(CartItemCriteria criteria) {
        LOG.debug("REST request to count CartItems by criteria: {}", criteria);
        return ResponseEntity.ok().body(cartItemQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /cart-items/:id} : get the "id" cartItem.
     *
     * @param id the id of the cartItemDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cartItemDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CartItemDTO> getCartItem(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CartItem : {}", id);
        Optional<CartItemDTO> cartItemDTO = cartItemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(cartItemDTO);
    }

    /**
     * {@code DELETE  /cart-items/:id} : delete the "id" cartItem.
     *
     * @param id the id of the cartItemDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CartItem : {}", id);
        cartItemService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
