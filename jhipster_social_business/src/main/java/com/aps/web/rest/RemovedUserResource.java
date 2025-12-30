package com.aps.web.rest;

import com.aps.repository.RemovedUserRepository;
import com.aps.service.RemovedUserQueryService;
import com.aps.service.RemovedUserService;
import com.aps.service.criteria.RemovedUserCriteria;
import com.aps.service.dto.RemovedUserDTO;
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
 * REST controller for managing {@link com.aps.domain.RemovedUser}.
 */
@RestController
@RequestMapping("/api/removed-users")
public class RemovedUserResource {

    private static final Logger LOG = LoggerFactory.getLogger(RemovedUserResource.class);

    private static final String ENTITY_NAME = "removedUser";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RemovedUserService removedUserService;

    private final RemovedUserRepository removedUserRepository;

    private final RemovedUserQueryService removedUserQueryService;

    public RemovedUserResource(
            RemovedUserService removedUserService,
            RemovedUserRepository removedUserRepository,
            RemovedUserQueryService removedUserQueryService) {
        this.removedUserService = removedUserService;
        this.removedUserRepository = removedUserRepository;
        this.removedUserQueryService = removedUserQueryService;
    }

    /**
     * {@code POST  /removed-users} : Create a new removedUser.
     *
     * @param removedUserDTO the removedUserDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     *         body the new removedUserDTO, or with status {@code 400 (Bad Request)}
     *         if the removedUser has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    /**
     * {@code POST  /removed-users/:id/restore} : Restore a removed user.
     *
     * @param id the id of the removedUser to restore.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the new entity ID.
     */
    @PostMapping("/{id}/restore")
    public ResponseEntity<?> restoreUser(@PathVariable Long id) {
        LOG.debug("REST request to restore RemovedUser : {}", id);
        try {
            Long newId = removedUserService.restoreUser(id);
            return ResponseEntity.ok().body(newId);
        } catch (IllegalStateException | IllegalArgumentException e) {
            LOG.error("Restore failed for user {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .header("X-App-Error", "restore.failed.conflict") // Custom header for specific client handling if
                                                                      // needed
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            LOG.error("Unexpected error restoring user {}", id, e);
            return ResponseEntity.internalServerError()
                    .header("X-App-Error", "restore.failed.internal")
                    .body(new ErrorResponse("An unexpected error occurred: " + e.getMessage()));
        }
    }

    // Simple DTO for error response
    public static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    @PostMapping("")
    public ResponseEntity<RemovedUserDTO> createRemovedUser(@RequestBody RemovedUserDTO removedUserDTO)
            throws URISyntaxException {
        LOG.debug("REST request to save RemovedUser : {}", removedUserDTO);
        if (removedUserDTO.getId() != null) {
            throw new BadRequestAlertException("A new removedUser cannot already have an ID", ENTITY_NAME, "idexists");
        }
        removedUserDTO = removedUserService.save(removedUserDTO);
        return ResponseEntity.created(new URI("/api/removed-users/" + removedUserDTO.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME,
                        removedUserDTO.getId().toString()))
                .body(removedUserDTO);
    }

    /**
     * {@code PUT  /removed-users/:id} : Updates an existing removedUser.
     *
     * @param id             the id of the removedUserDTO to save.
     * @param removedUserDTO the removedUserDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated removedUserDTO,
     *         or with status {@code 400 (Bad Request)} if the removedUserDTO is not
     *         valid,
     *         or with status {@code 500 (Internal Server Error)} if the
     *         removedUserDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RemovedUserDTO> updateRemovedUser(
            @PathVariable(value = "id", required = false) final Long id,
            @RequestBody RemovedUserDTO removedUserDTO) throws URISyntaxException {
        LOG.debug("REST request to update RemovedUser : {}, {}", id, removedUserDTO);
        if (removedUserDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, removedUserDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!removedUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        removedUserDTO = removedUserService.update(removedUserDTO);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME,
                        removedUserDTO.getId().toString()))
                .body(removedUserDTO);
    }

    /**
     * {@code PATCH  /removed-users/:id} : Partial updates given fields of an
     * existing removedUser, field will ignore if it is null
     *
     * @param id             the id of the removedUserDTO to save.
     * @param removedUserDTO the removedUserDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated removedUserDTO,
     *         or with status {@code 400 (Bad Request)} if the removedUserDTO is not
     *         valid,
     *         or with status {@code 404 (Not Found)} if the removedUserDTO is not
     *         found,
     *         or with status {@code 500 (Internal Server Error)} if the
     *         removedUserDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<RemovedUserDTO> partialUpdateRemovedUser(
            @PathVariable(value = "id", required = false) final Long id,
            @RequestBody RemovedUserDTO removedUserDTO) throws URISyntaxException {
        LOG.debug("REST request to partial update RemovedUser partially : {}, {}", id, removedUserDTO);
        if (removedUserDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, removedUserDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!removedUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<RemovedUserDTO> result = removedUserService.partialUpdate(removedUserDTO);

        return ResponseUtil.wrapOrNotFound(
                result,
                HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME,
                        removedUserDTO.getId().toString()));
    }

    /**
     * {@code GET  /removed-users} : get all the removedUsers.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of removedUsers in body.
     */
    @GetMapping("")
    public ResponseEntity<List<RemovedUserDTO>> getAllRemovedUsers(
            RemovedUserCriteria criteria,
            @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get RemovedUsers by criteria: {}", criteria);

        Page<RemovedUserDTO> page = removedUserQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil
                .generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /removed-users/count} : count all the removedUsers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count
     *         in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countRemovedUsers(RemovedUserCriteria criteria) {
        LOG.debug("REST request to count RemovedUsers by criteria: {}", criteria);
        return ResponseEntity.ok().body(removedUserQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /removed-users/:id} : get the "id" removedUser.
     *
     * @param id the id of the removedUserDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the removedUserDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RemovedUserDTO> getRemovedUser(@PathVariable("id") Long id) {
        LOG.debug("REST request to get RemovedUser : {}", id);
        Optional<RemovedUserDTO> removedUserDTO = removedUserService.findOne(id);
        return ResponseUtil.wrapOrNotFound(removedUserDTO);
    }

    /**
     * {@code DELETE  /removed-users/:id} : delete the "id" removedUser.
     *
     * @param id the id of the removedUserDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRemovedUser(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete RemovedUser : {}", id);
        removedUserService.delete(id);
        return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                .build();
    }
}
