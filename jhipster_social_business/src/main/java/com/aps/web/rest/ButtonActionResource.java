package com.aps.web.rest;

import com.aps.domain.ButtonAction;
import com.aps.repository.ButtonActionRepository;
import com.aps.web.rest.errors.BadRequestAlertException;
import java.util.List;
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

    private final Logger log = LoggerFactory.getLogger(ButtonActionResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ButtonActionRepository buttonActionRepository;

    public ButtonActionResource(ButtonActionRepository buttonActionRepository) {
        this.buttonActionRepository = buttonActionRepository;
    }

    /**
     * {@code GET  /button-actions} : get all the buttonActions.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of buttonActions in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ButtonAction>> getAllButtonActions(
            @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of ButtonActions");
        Page<ButtonAction> page = buttonActionRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil
                .generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /button-actions/:id} : get the "id" buttonAction.
     *
     * @param id the id of the buttonAction to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the buttonAction, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ButtonAction> getButtonAction(@PathVariable("id") Long id) {
        log.debug("REST request to get ButtonAction : {}", id);
        Optional<ButtonAction> buttonAction = buttonActionRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(buttonAction);
    }
}
