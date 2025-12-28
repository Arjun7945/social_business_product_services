package com.aps.web.rest;

import com.aps.repository.ButtonActionRepository;
import com.aps.service.ButtonActionQueryService;
import com.aps.service.ButtonActionService;
import com.aps.service.criteria.ButtonActionCriteria;
import com.aps.service.dto.ButtonActionDTO;
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
 * REST controller for managing {@link com.aps.domain.ButtonAction}.
 */
@RestController
@RequestMapping("/api/button-actions")
public class ButtonActionResource {

    private static final Logger LOG = LoggerFactory.getLogger(ButtonActionResource.class);

    private static final String ENTITY_NAME = "buttonAction";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ButtonActionService buttonActionService;

    private final ButtonActionRepository buttonActionRepository;

    private final ButtonActionQueryService buttonActionQueryService;

    public ButtonActionResource(
        ButtonActionService buttonActionService,
        ButtonActionRepository buttonActionRepository,
        ButtonActionQueryService buttonActionQueryService
    ) {
        this.buttonActionService = buttonActionService;
        this.buttonActionRepository = buttonActionRepository;
        this.buttonActionQueryService = buttonActionQueryService;
    }

    /**
     * {@code POST  /button-actions} : Create a new buttonAction.
     *
     * @param buttonActionDTO the buttonActionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new buttonActionDTO, or with status {@code 400 (Bad Request)} if the buttonAction has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ButtonActionDTO> createButtonAction(@Valid @RequestBody ButtonActionDTO buttonActionDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ButtonAction : {}", buttonActionDTO);
        if (buttonActionDTO.getId() != null) {
            throw new BadRequestAlertException("A new buttonAction cannot already have an ID", ENTITY_NAME, "idexists");
        }
        buttonActionDTO = buttonActionService.save(buttonActionDTO);
        return ResponseEntity.created(new URI("/api/button-actions/" + buttonActionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, buttonActionDTO.getId().toString()))
            .body(buttonActionDTO);
    }

    /**
     * {@code PUT  /button-actions/:id} : Updates an existing buttonAction.
     *
     * @param id the id of the buttonActionDTO to save.
     * @param buttonActionDTO the buttonActionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated buttonActionDTO,
     * or with status {@code 400 (Bad Request)} if the buttonActionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the buttonActionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ButtonActionDTO> updateButtonAction(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ButtonActionDTO buttonActionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ButtonAction : {}, {}", id, buttonActionDTO);
        if (buttonActionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, buttonActionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!buttonActionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        buttonActionDTO = buttonActionService.update(buttonActionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, buttonActionDTO.getId().toString()))
            .body(buttonActionDTO);
    }

    /**
     * {@code PATCH  /button-actions/:id} : Partial updates given fields of an existing buttonAction, field will ignore if it is null
     *
     * @param id the id of the buttonActionDTO to save.
     * @param buttonActionDTO the buttonActionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated buttonActionDTO,
     * or with status {@code 400 (Bad Request)} if the buttonActionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the buttonActionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the buttonActionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ButtonActionDTO> partialUpdateButtonAction(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ButtonActionDTO buttonActionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ButtonAction partially : {}, {}", id, buttonActionDTO);
        if (buttonActionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, buttonActionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!buttonActionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ButtonActionDTO> result = buttonActionService.partialUpdate(buttonActionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, buttonActionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /button-actions} : get all the buttonActions.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of buttonActions in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ButtonActionDTO>> getAllButtonActions(
        ButtonActionCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get ButtonActions by criteria: {}", criteria);

        Page<ButtonActionDTO> page = buttonActionQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /button-actions/count} : count all the buttonActions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countButtonActions(ButtonActionCriteria criteria) {
        LOG.debug("REST request to count ButtonActions by criteria: {}", criteria);
        return ResponseEntity.ok().body(buttonActionQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /button-actions/:id} : get the "id" buttonAction.
     *
     * @param id the id of the buttonActionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the buttonActionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ButtonActionDTO> getButtonAction(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ButtonAction : {}", id);
        Optional<ButtonActionDTO> buttonActionDTO = buttonActionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(buttonActionDTO);
    }

    /**
     * {@code DELETE  /button-actions/:id} : delete the "id" buttonAction.
     *
     * @param id the id of the buttonActionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteButtonAction(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ButtonAction : {}", id);
        buttonActionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
