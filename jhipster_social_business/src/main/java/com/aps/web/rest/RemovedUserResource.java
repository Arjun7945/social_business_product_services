package com.aps.web.rest;

import com.aps.domain.RemovedUser;
import com.aps.repository.RemovedUserRepository;
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
public class RemovedUserResource {

    private final Logger log = LoggerFactory.getLogger(RemovedUserResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RemovedUserRepository removedUserRepository;

    private final com.aps.service.UserRemovalService userRemovalService;

    public RemovedUserResource(RemovedUserRepository removedUserRepository, com.aps.service.UserRemovalService userRemovalService) {
        this.removedUserRepository = removedUserRepository;
        this.userRemovalService = userRemovalService;
    }

    /**
     * {@code POST  /removed-users/:id/restore} : Restore a removed user.
     *
     * @param id the id of the removedUser to restore.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the new entity ID.
     */
    @PostMapping("/removed-users/{id}/restore")
    public ResponseEntity<?> restoreUser(@PathVariable Long id) {
        log.debug("REST request to restore RemovedUser : {}", id);
        try {
            Long newId = userRemovalService.restoreUser(id);
            return ResponseEntity.ok().body(newId);
        } catch (IllegalStateException | IllegalArgumentException e) {
            log.error("Restore failed for user {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().header("X-App-Error", "restore.failed.conflict").body(new ErrorResponse(e.getMessage())); // Using a simple Map or custom error object might be
            // better, but strict types... let's return a Map for
            // flexibility or just a string if simpler.
            // Actually, for JHipster/Spring Boot, we better return a standardized error
            // structure or just a badRequest with a header/body.
            // Let's stick to a simple body map.
        } catch (Exception e) {
            log.error("Unexpected error restoring user {}", id, e);
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

    /**
     * {@code GET  /removed-users} : get all the removedUsers.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of removedUsers in body.
     */
    @GetMapping("/removed-users")
    public ResponseEntity<List<RemovedUser>> getAllRemovedUsers(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of RemovedUsers");
        Page<RemovedUser> page = removedUserRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /removed-users/:id} : get the "id" removedUser.
     *
     * @param id the id of the removedUser to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the removedUser, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/removed-users/{id}")
    public ResponseEntity<RemovedUser> getRemovedUser(@PathVariable Long id) {
        log.debug("REST request to get RemovedUser : {}", id);
        Optional<RemovedUser> removedUser = removedUserRepository.findById(id);

        if (removedUser.isEmpty()) {
            RemovedUser placeholder = new RemovedUser();
            placeholder.setId(id);
            placeholder.setName("Unknown (Restored?)");
            placeholder.setReasonForRemoval("Record missing. User may have been restored manually.");
            placeholder.setRole(com.aps.domain.enumeration.UserRole.CUSTOMER); // Default
            placeholder.setStatus(com.aps.domain.enumeration.AccountStatus.ACCOUNT_REMOVED);
            placeholder.setRemovedAt(java.time.Instant.now());
            return ResponseEntity.ok(placeholder);
        }

        return ResponseUtil.wrapOrNotFound(removedUser);
    }
}
