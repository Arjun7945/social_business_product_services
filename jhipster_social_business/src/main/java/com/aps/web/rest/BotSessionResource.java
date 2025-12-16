package com.aps.web.rest;

import com.aps.repository.BotSessionRepository;
import com.aps.service.BotSessionQueryService;
import com.aps.service.BotSessionService;
import com.aps.service.criteria.BotSessionCriteria;
import com.aps.service.dto.BotSessionDTO;
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
 * REST controller for managing {@link com.aps.domain.BotSession}.
 */
@RestController
@RequestMapping("/api/bot-sessions")
public class BotSessionResource {

    private static final Logger LOG = LoggerFactory.getLogger(BotSessionResource.class);

    private static final String ENTITY_NAME = "botSession";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BotSessionService botSessionService;

    private final BotSessionRepository botSessionRepository;

    private final BotSessionQueryService botSessionQueryService;

    public BotSessionResource(
        BotSessionService botSessionService,
        BotSessionRepository botSessionRepository,
        BotSessionQueryService botSessionQueryService
    ) {
        this.botSessionService = botSessionService;
        this.botSessionRepository = botSessionRepository;
        this.botSessionQueryService = botSessionQueryService;
    }

    /**
     * {@code POST  /bot-sessions} : Create a new botSession.
     *
     * @param botSessionDTO the botSessionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new botSessionDTO, or with status {@code 400 (Bad Request)} if the botSession has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<BotSessionDTO> createBotSession(@Valid @RequestBody BotSessionDTO botSessionDTO) throws URISyntaxException {
        LOG.debug("REST request to save BotSession : {}", botSessionDTO);
        if (botSessionDTO.getId() != null) {
            throw new BadRequestAlertException("A new botSession cannot already have an ID", ENTITY_NAME, "idexists");
        }
        botSessionDTO = botSessionService.save(botSessionDTO);
        return ResponseEntity.created(new URI("/api/bot-sessions/" + botSessionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, botSessionDTO.getId().toString()))
            .body(botSessionDTO);
    }

    /**
     * {@code PUT  /bot-sessions/:id} : Updates an existing botSession.
     *
     * @param id the id of the botSessionDTO to save.
     * @param botSessionDTO the botSessionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated botSessionDTO,
     * or with status {@code 400 (Bad Request)} if the botSessionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the botSessionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BotSessionDTO> updateBotSession(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BotSessionDTO botSessionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update BotSession : {}, {}", id, botSessionDTO);
        if (botSessionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, botSessionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!botSessionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        botSessionDTO = botSessionService.update(botSessionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, botSessionDTO.getId().toString()))
            .body(botSessionDTO);
    }

    /**
     * {@code PATCH  /bot-sessions/:id} : Partial updates given fields of an existing botSession, field will ignore if it is null
     *
     * @param id the id of the botSessionDTO to save.
     * @param botSessionDTO the botSessionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated botSessionDTO,
     * or with status {@code 400 (Bad Request)} if the botSessionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the botSessionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the botSessionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BotSessionDTO> partialUpdateBotSession(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BotSessionDTO botSessionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update BotSession partially : {}, {}", id, botSessionDTO);
        if (botSessionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, botSessionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!botSessionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BotSessionDTO> result = botSessionService.partialUpdate(botSessionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, botSessionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /bot-sessions} : get all the botSessions.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of botSessions in body.
     */
    @GetMapping("")
    public ResponseEntity<List<BotSessionDTO>> getAllBotSessions(
        BotSessionCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get BotSessions by criteria: {}", criteria);

        Page<BotSessionDTO> page = botSessionQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /bot-sessions/count} : count all the botSessions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countBotSessions(BotSessionCriteria criteria) {
        LOG.debug("REST request to count BotSessions by criteria: {}", criteria);
        return ResponseEntity.ok().body(botSessionQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /bot-sessions/:id} : get the "id" botSession.
     *
     * @param id the id of the botSessionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the botSessionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BotSessionDTO> getBotSession(@PathVariable("id") Long id) {
        LOG.debug("REST request to get BotSession : {}", id);
        Optional<BotSessionDTO> botSessionDTO = botSessionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(botSessionDTO);
    }

    /**
     * {@code DELETE  /bot-sessions/:id} : delete the "id" botSession.
     *
     * @param id the id of the botSessionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBotSession(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete BotSession : {}", id);
        botSessionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
