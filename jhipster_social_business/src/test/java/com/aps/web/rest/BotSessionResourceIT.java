package com.aps.web.rest;

import static com.aps.domain.BotSessionAsserts.*;
import static com.aps.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.aps.IntegrationTest;
import com.aps.domain.BotSession;
import com.aps.repository.BotSessionRepository;
import com.aps.service.dto.BotSessionDTO;
import com.aps.service.mapper.BotSessionMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link BotSessionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BotSessionResourceIT {

    private static final String DEFAULT_WA_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_WA_PHONE_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_CURRENT_STATE = "AAAAAAAAAA";
    private static final String UPDATED_CURRENT_STATE = "BBBBBBBBBB";

    private static final String DEFAULT_SESSION_DATA = "AAAAAAAAAA";
    private static final String UPDATED_SESSION_DATA = "BBBBBBBBBB";

    private static final Instant DEFAULT_LAST_ACTIVE_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_ACTIVE_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/bot-sessions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BotSessionRepository botSessionRepository;

    @Autowired
    private BotSessionMapper botSessionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBotSessionMockMvc;

    private BotSession botSession;

    private BotSession insertedBotSession;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BotSession createEntity() {
        return new BotSession()
            .waPhoneNumber(DEFAULT_WA_PHONE_NUMBER)
            .currentState(DEFAULT_CURRENT_STATE)
            .sessionData(DEFAULT_SESSION_DATA)
            .lastActiveAt(DEFAULT_LAST_ACTIVE_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BotSession createUpdatedEntity() {
        return new BotSession()
            .waPhoneNumber(UPDATED_WA_PHONE_NUMBER)
            .currentState(UPDATED_CURRENT_STATE)
            .sessionData(UPDATED_SESSION_DATA)
            .lastActiveAt(UPDATED_LAST_ACTIVE_AT);
    }

    @BeforeEach
    public void initTest() {
        botSession = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedBotSession != null) {
            botSessionRepository.delete(insertedBotSession);
            insertedBotSession = null;
        }
    }

    @Test
    @Transactional
    void createBotSession() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the BotSession
        BotSessionDTO botSessionDTO = botSessionMapper.toDto(botSession);
        var returnedBotSessionDTO = om.readValue(
            restBotSessionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(botSessionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            BotSessionDTO.class
        );

        // Validate the BotSession in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedBotSession = botSessionMapper.toEntity(returnedBotSessionDTO);
        assertBotSessionUpdatableFieldsEquals(returnedBotSession, getPersistedBotSession(returnedBotSession));

        insertedBotSession = returnedBotSession;
    }

    @Test
    @Transactional
    void createBotSessionWithExistingId() throws Exception {
        // Create the BotSession with an existing ID
        botSession.setId(1L);
        BotSessionDTO botSessionDTO = botSessionMapper.toDto(botSession);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBotSessionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(botSessionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the BotSession in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkWaPhoneNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        botSession.setWaPhoneNumber(null);

        // Create the BotSession, which fails.
        BotSessionDTO botSessionDTO = botSessionMapper.toDto(botSession);

        restBotSessionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(botSessionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCurrentStateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        botSession.setCurrentState(null);

        // Create the BotSession, which fails.
        BotSessionDTO botSessionDTO = botSessionMapper.toDto(botSession);

        restBotSessionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(botSessionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBotSessions() throws Exception {
        // Initialize the database
        insertedBotSession = botSessionRepository.saveAndFlush(botSession);

        // Get all the botSessionList
        restBotSessionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(botSession.getId().intValue())))
            .andExpect(jsonPath("$.[*].waPhoneNumber").value(hasItem(DEFAULT_WA_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].currentState").value(hasItem(DEFAULT_CURRENT_STATE)))
            .andExpect(jsonPath("$.[*].sessionData").value(hasItem(DEFAULT_SESSION_DATA)))
            .andExpect(jsonPath("$.[*].lastActiveAt").value(hasItem(DEFAULT_LAST_ACTIVE_AT.toString())));
    }

    @Test
    @Transactional
    void getBotSession() throws Exception {
        // Initialize the database
        insertedBotSession = botSessionRepository.saveAndFlush(botSession);

        // Get the botSession
        restBotSessionMockMvc
            .perform(get(ENTITY_API_URL_ID, botSession.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(botSession.getId().intValue()))
            .andExpect(jsonPath("$.waPhoneNumber").value(DEFAULT_WA_PHONE_NUMBER))
            .andExpect(jsonPath("$.currentState").value(DEFAULT_CURRENT_STATE))
            .andExpect(jsonPath("$.sessionData").value(DEFAULT_SESSION_DATA))
            .andExpect(jsonPath("$.lastActiveAt").value(DEFAULT_LAST_ACTIVE_AT.toString()));
    }

    @Test
    @Transactional
    void getBotSessionsByIdFiltering() throws Exception {
        // Initialize the database
        insertedBotSession = botSessionRepository.saveAndFlush(botSession);

        Long id = botSession.getId();

        defaultBotSessionFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultBotSessionFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultBotSessionFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllBotSessionsByWaPhoneNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBotSession = botSessionRepository.saveAndFlush(botSession);

        // Get all the botSessionList where waPhoneNumber equals to
        defaultBotSessionFiltering("waPhoneNumber.equals=" + DEFAULT_WA_PHONE_NUMBER, "waPhoneNumber.equals=" + UPDATED_WA_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllBotSessionsByWaPhoneNumberIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBotSession = botSessionRepository.saveAndFlush(botSession);

        // Get all the botSessionList where waPhoneNumber in
        defaultBotSessionFiltering(
            "waPhoneNumber.in=" + DEFAULT_WA_PHONE_NUMBER + "," + UPDATED_WA_PHONE_NUMBER,
            "waPhoneNumber.in=" + UPDATED_WA_PHONE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllBotSessionsByWaPhoneNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBotSession = botSessionRepository.saveAndFlush(botSession);

        // Get all the botSessionList where waPhoneNumber is not null
        defaultBotSessionFiltering("waPhoneNumber.specified=true", "waPhoneNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllBotSessionsByWaPhoneNumberContainsSomething() throws Exception {
        // Initialize the database
        insertedBotSession = botSessionRepository.saveAndFlush(botSession);

        // Get all the botSessionList where waPhoneNumber contains
        defaultBotSessionFiltering(
            "waPhoneNumber.contains=" + DEFAULT_WA_PHONE_NUMBER,
            "waPhoneNumber.contains=" + UPDATED_WA_PHONE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllBotSessionsByWaPhoneNumberNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBotSession = botSessionRepository.saveAndFlush(botSession);

        // Get all the botSessionList where waPhoneNumber does not contain
        defaultBotSessionFiltering(
            "waPhoneNumber.doesNotContain=" + UPDATED_WA_PHONE_NUMBER,
            "waPhoneNumber.doesNotContain=" + DEFAULT_WA_PHONE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllBotSessionsByCurrentStateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBotSession = botSessionRepository.saveAndFlush(botSession);

        // Get all the botSessionList where currentState equals to
        defaultBotSessionFiltering("currentState.equals=" + DEFAULT_CURRENT_STATE, "currentState.equals=" + UPDATED_CURRENT_STATE);
    }

    @Test
    @Transactional
    void getAllBotSessionsByCurrentStateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBotSession = botSessionRepository.saveAndFlush(botSession);

        // Get all the botSessionList where currentState in
        defaultBotSessionFiltering(
            "currentState.in=" + DEFAULT_CURRENT_STATE + "," + UPDATED_CURRENT_STATE,
            "currentState.in=" + UPDATED_CURRENT_STATE
        );
    }

    @Test
    @Transactional
    void getAllBotSessionsByCurrentStateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBotSession = botSessionRepository.saveAndFlush(botSession);

        // Get all the botSessionList where currentState is not null
        defaultBotSessionFiltering("currentState.specified=true", "currentState.specified=false");
    }

    @Test
    @Transactional
    void getAllBotSessionsByCurrentStateContainsSomething() throws Exception {
        // Initialize the database
        insertedBotSession = botSessionRepository.saveAndFlush(botSession);

        // Get all the botSessionList where currentState contains
        defaultBotSessionFiltering("currentState.contains=" + DEFAULT_CURRENT_STATE, "currentState.contains=" + UPDATED_CURRENT_STATE);
    }

    @Test
    @Transactional
    void getAllBotSessionsByCurrentStateNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBotSession = botSessionRepository.saveAndFlush(botSession);

        // Get all the botSessionList where currentState does not contain
        defaultBotSessionFiltering(
            "currentState.doesNotContain=" + UPDATED_CURRENT_STATE,
            "currentState.doesNotContain=" + DEFAULT_CURRENT_STATE
        );
    }

    @Test
    @Transactional
    void getAllBotSessionsByLastActiveAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBotSession = botSessionRepository.saveAndFlush(botSession);

        // Get all the botSessionList where lastActiveAt equals to
        defaultBotSessionFiltering("lastActiveAt.equals=" + DEFAULT_LAST_ACTIVE_AT, "lastActiveAt.equals=" + UPDATED_LAST_ACTIVE_AT);
    }

    @Test
    @Transactional
    void getAllBotSessionsByLastActiveAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBotSession = botSessionRepository.saveAndFlush(botSession);

        // Get all the botSessionList where lastActiveAt in
        defaultBotSessionFiltering(
            "lastActiveAt.in=" + DEFAULT_LAST_ACTIVE_AT + "," + UPDATED_LAST_ACTIVE_AT,
            "lastActiveAt.in=" + UPDATED_LAST_ACTIVE_AT
        );
    }

    @Test
    @Transactional
    void getAllBotSessionsByLastActiveAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBotSession = botSessionRepository.saveAndFlush(botSession);

        // Get all the botSessionList where lastActiveAt is not null
        defaultBotSessionFiltering("lastActiveAt.specified=true", "lastActiveAt.specified=false");
    }

    private void defaultBotSessionFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultBotSessionShouldBeFound(shouldBeFound);
        defaultBotSessionShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBotSessionShouldBeFound(String filter) throws Exception {
        restBotSessionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(botSession.getId().intValue())))
            .andExpect(jsonPath("$.[*].waPhoneNumber").value(hasItem(DEFAULT_WA_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].currentState").value(hasItem(DEFAULT_CURRENT_STATE)))
            .andExpect(jsonPath("$.[*].sessionData").value(hasItem(DEFAULT_SESSION_DATA)))
            .andExpect(jsonPath("$.[*].lastActiveAt").value(hasItem(DEFAULT_LAST_ACTIVE_AT.toString())));

        // Check, that the count call also returns 1
        restBotSessionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBotSessionShouldNotBeFound(String filter) throws Exception {
        restBotSessionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBotSessionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingBotSession() throws Exception {
        // Get the botSession
        restBotSessionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBotSession() throws Exception {
        // Initialize the database
        insertedBotSession = botSessionRepository.saveAndFlush(botSession);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the botSession
        BotSession updatedBotSession = botSessionRepository.findById(botSession.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBotSession are not directly saved in db
        em.detach(updatedBotSession);
        updatedBotSession
            .waPhoneNumber(UPDATED_WA_PHONE_NUMBER)
            .currentState(UPDATED_CURRENT_STATE)
            .sessionData(UPDATED_SESSION_DATA)
            .lastActiveAt(UPDATED_LAST_ACTIVE_AT);
        BotSessionDTO botSessionDTO = botSessionMapper.toDto(updatedBotSession);

        restBotSessionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, botSessionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(botSessionDTO))
            )
            .andExpect(status().isOk());

        // Validate the BotSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBotSessionToMatchAllProperties(updatedBotSession);
    }

    @Test
    @Transactional
    void putNonExistingBotSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        botSession.setId(longCount.incrementAndGet());

        // Create the BotSession
        BotSessionDTO botSessionDTO = botSessionMapper.toDto(botSession);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBotSessionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, botSessionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(botSessionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BotSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBotSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        botSession.setId(longCount.incrementAndGet());

        // Create the BotSession
        BotSessionDTO botSessionDTO = botSessionMapper.toDto(botSession);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBotSessionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(botSessionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BotSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBotSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        botSession.setId(longCount.incrementAndGet());

        // Create the BotSession
        BotSessionDTO botSessionDTO = botSessionMapper.toDto(botSession);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBotSessionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(botSessionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BotSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBotSessionWithPatch() throws Exception {
        // Initialize the database
        insertedBotSession = botSessionRepository.saveAndFlush(botSession);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the botSession using partial update
        BotSession partialUpdatedBotSession = new BotSession();
        partialUpdatedBotSession.setId(botSession.getId());

        partialUpdatedBotSession.lastActiveAt(UPDATED_LAST_ACTIVE_AT);

        restBotSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBotSession.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBotSession))
            )
            .andExpect(status().isOk());

        // Validate the BotSession in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBotSessionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedBotSession, botSession),
            getPersistedBotSession(botSession)
        );
    }

    @Test
    @Transactional
    void fullUpdateBotSessionWithPatch() throws Exception {
        // Initialize the database
        insertedBotSession = botSessionRepository.saveAndFlush(botSession);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the botSession using partial update
        BotSession partialUpdatedBotSession = new BotSession();
        partialUpdatedBotSession.setId(botSession.getId());

        partialUpdatedBotSession
            .waPhoneNumber(UPDATED_WA_PHONE_NUMBER)
            .currentState(UPDATED_CURRENT_STATE)
            .sessionData(UPDATED_SESSION_DATA)
            .lastActiveAt(UPDATED_LAST_ACTIVE_AT);

        restBotSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBotSession.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBotSession))
            )
            .andExpect(status().isOk());

        // Validate the BotSession in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBotSessionUpdatableFieldsEquals(partialUpdatedBotSession, getPersistedBotSession(partialUpdatedBotSession));
    }

    @Test
    @Transactional
    void patchNonExistingBotSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        botSession.setId(longCount.incrementAndGet());

        // Create the BotSession
        BotSessionDTO botSessionDTO = botSessionMapper.toDto(botSession);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBotSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, botSessionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(botSessionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BotSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBotSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        botSession.setId(longCount.incrementAndGet());

        // Create the BotSession
        BotSessionDTO botSessionDTO = botSessionMapper.toDto(botSession);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBotSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(botSessionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BotSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBotSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        botSession.setId(longCount.incrementAndGet());

        // Create the BotSession
        BotSessionDTO botSessionDTO = botSessionMapper.toDto(botSession);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBotSessionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(botSessionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BotSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBotSession() throws Exception {
        // Initialize the database
        insertedBotSession = botSessionRepository.saveAndFlush(botSession);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the botSession
        restBotSessionMockMvc
            .perform(delete(ENTITY_API_URL_ID, botSession.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return botSessionRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected BotSession getPersistedBotSession(BotSession botSession) {
        return botSessionRepository.findById(botSession.getId()).orElseThrow();
    }

    protected void assertPersistedBotSessionToMatchAllProperties(BotSession expectedBotSession) {
        assertBotSessionAllPropertiesEquals(expectedBotSession, getPersistedBotSession(expectedBotSession));
    }

    protected void assertPersistedBotSessionToMatchUpdatableProperties(BotSession expectedBotSession) {
        assertBotSessionAllUpdatablePropertiesEquals(expectedBotSession, getPersistedBotSession(expectedBotSession));
    }
}
