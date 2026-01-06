package com.aps.web.rest;

import com.aps.repository.CustomerOrderRepository;
import com.aps.service.CustomerOrderQueryService;
import com.aps.service.CustomerOrderService;
import com.aps.service.criteria.CustomerOrderCriteria;
import com.aps.service.dto.CustomerOrderDTO;
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
 * REST controller for managing {@link com.aps.domain.CustomerOrder}.
 */
@RestController
@RequestMapping("/api/customer-orders")
public class CustomerOrderResource {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerOrderResource.class);

    private static final String ENTITY_NAME = "customerOrder";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CustomerOrderService customerOrderService;

    private final CustomerOrderRepository customerOrderRepository;

    private final CustomerOrderQueryService customerOrderQueryService;

    private final com.aps.service.OrderItemQueryService orderItemQueryService;

    public CustomerOrderResource(
            CustomerOrderService customerOrderService,
            CustomerOrderRepository customerOrderRepository,
            CustomerOrderQueryService customerOrderQueryService,
            com.aps.service.OrderItemQueryService orderItemQueryService) {
        this.customerOrderService = customerOrderService;
        this.customerOrderRepository = customerOrderRepository;
        this.customerOrderQueryService = customerOrderQueryService;
        this.orderItemQueryService = orderItemQueryService;
    }

    /**
     * {@code POST  /customer-orders} : Create a new customerOrder.
     *
     * @param customerOrderDTO the customerOrderDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     *         body the new customerOrderDTO, or with status
     *         {@code 400 (Bad Request)} if the customerOrder has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CustomerOrderDTO> createCustomerOrder(@Valid @RequestBody CustomerOrderDTO customerOrderDTO)
            throws URISyntaxException {
        LOG.debug("REST request to save CustomerOrder : {}", customerOrderDTO);
        if (customerOrderDTO.getId() != null) {
            throw new BadRequestAlertException("A new customerOrder cannot already have an ID", ENTITY_NAME,
                    "idexists");
        }
        customerOrderDTO = customerOrderService.save(customerOrderDTO);
        return ResponseEntity.created(new URI("/api/customer-orders/" + customerOrderDTO.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME,
                        customerOrderDTO.getId().toString()))
                .body(customerOrderDTO);
    }

    /**
     * {@code PUT  /customer-orders/:id} : Updates an existing customerOrder.
     *
     * @param id               the id of the customerOrderDTO to save.
     * @param customerOrderDTO the customerOrderDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated customerOrderDTO,
     *         or with status {@code 400 (Bad Request)} if the customerOrderDTO is
     *         not valid,
     *         or with status {@code 500 (Internal Server Error)} if the
     *         customerOrderDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CustomerOrderDTO> updateCustomerOrder(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody CustomerOrderDTO customerOrderDTO) throws URISyntaxException {
        LOG.debug("REST request to update CustomerOrder : {}, {}", id, customerOrderDTO);
        if (customerOrderDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, customerOrderDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!customerOrderRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        customerOrderDTO = customerOrderService.update(customerOrderDTO);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME,
                        customerOrderDTO.getId().toString()))
                .body(customerOrderDTO);
    }

    /**
     * {@code PATCH  /customer-orders/:id} : Partial updates given fields of an
     * existing customerOrder, field will ignore if it is null
     *
     * @param id               the id of the customerOrderDTO to save.
     * @param customerOrderDTO the customerOrderDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated customerOrderDTO,
     *         or with status {@code 400 (Bad Request)} if the customerOrderDTO is
     *         not valid,
     *         or with status {@code 404 (Not Found)} if the customerOrderDTO is not
     *         found,
     *         or with status {@code 500 (Internal Server Error)} if the
     *         customerOrderDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CustomerOrderDTO> partialUpdateCustomerOrder(
            @PathVariable(value = "id", required = false) final Long id,
            @NotNull @RequestBody CustomerOrderDTO customerOrderDTO) throws URISyntaxException {
        LOG.debug("REST request to partial update CustomerOrder partially : {}, {}", id, customerOrderDTO);
        if (customerOrderDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, customerOrderDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!customerOrderRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CustomerOrderDTO> result = customerOrderService.partialUpdate(customerOrderDTO);

        return ResponseUtil.wrapOrNotFound(
                result,
                HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME,
                        customerOrderDTO.getId().toString()));
    }

    /**
     * {@code GET  /customer-orders} : get all the customerOrders.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of customerOrders in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CustomerOrderDTO>> getAllCustomerOrders(
            CustomerOrderCriteria criteria,
            @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get CustomerOrders by criteria: {}", criteria);

        Page<CustomerOrderDTO> page = customerOrderQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil
                .generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /customer-orders/count} : count all the customerOrders.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count
     *         in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countCustomerOrders(CustomerOrderCriteria criteria) {
        LOG.debug("REST request to count CustomerOrders by criteria: {}", criteria);
        return ResponseEntity.ok().body(customerOrderQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /customer-orders/:id} : get the "id" customerOrder.
     *
     * @param id the id of the customerOrderDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the customerOrderDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CustomerOrderDTO> getCustomerOrder(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CustomerOrder : {}", id);
        Optional<CustomerOrderDTO> customerOrderDTO = customerOrderService.findOne(id);
        return ResponseUtil.wrapOrNotFound(customerOrderDTO);
    }

    /**
     * {@code DELETE  /customer-orders/:id} : delete the "id" customerOrder.
     *
     * @param id the id of the customerOrderDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomerOrder(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CustomerOrder : {}", id);
        customerOrderService.delete(id);
        return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                .build();
    }

    /**
     * {@code GET  /customer-orders/public/:id} : get the "id" customerOrder for
     * public tracking.
     *
     * @param id the id of the customerOrderDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the customerOrderDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/public/{id}")
    public ResponseEntity<CustomerOrderDTO> getPublicCustomerOrder(@PathVariable("id") Long id) {
        LOG.debug("REST request to get public CustomerOrder : {}", id);
        Optional<CustomerOrderDTO> customerOrderDTO = customerOrderService.findOne(id);
        return customerOrderDTO.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * {@code GET  /customer-orders/public/:id/items} : get the orderItems for a
     * public order.
     *
     * @param id the id of the customerOrderDTO.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of orderItems in body.
     */
    @GetMapping("/public/{id}/items")
    public ResponseEntity<List<com.aps.service.dto.OrderItemDTO>> getPublicCustomerOrderItems(
            @PathVariable("id") Long id) {
        LOG.debug("REST request to get public CustomerOrder Items : {}", id);
        com.aps.service.criteria.OrderItemCriteria criteria = new com.aps.service.criteria.OrderItemCriteria();
        tech.jhipster.service.filter.LongFilter orderIdFilter = new tech.jhipster.service.filter.LongFilter();
        orderIdFilter.setEquals(id);
        criteria.setOrderId(orderIdFilter);

        List<com.aps.service.dto.OrderItemDTO> items = orderItemQueryService
                .findByCriteria(criteria, Pageable.unpaged()).getContent();
        return ResponseEntity.ok().body(items);
    }

    /**
     * {@code GET  /customer-orders/by-phone/:phone} : get customerOrders by
     * customer phone.
     *
     * @param phone the phone number of the customer.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of customerOrders in body.
     */
    @GetMapping("/by-phone/{phone}")
    public ResponseEntity<List<CustomerOrderDTO>> getCustomerOrdersByPhone(@PathVariable("phone") String phone) {
        LOG.debug("REST request to get CustomerOrders by phone : {}", phone);
        List<CustomerOrderDTO> orders = customerOrderService.findAllByCustomerWaPhoneNumber(phone);
        return ResponseEntity.ok().body(orders);
    }
}
