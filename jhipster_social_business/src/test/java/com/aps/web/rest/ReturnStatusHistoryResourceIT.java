package com.aps.web.rest;

import static com.aps.domain.ReturnStatusHistoryAsserts.*;
import static com.aps.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.aps.IntegrationTest;
import com.aps.domain.ReturnStatusHistory;
import com.aps.domain.ReturnedOrder;
import com.aps.domain.enumeration.ReturnStatus;
import com.aps.repository.ReturnStatusHistoryRepository;
import com.aps.service.dto.ReturnStatusHistoryDTO;
import com.aps.service.mapper.ReturnStatusHistoryMapper;
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
 * Integration tests for the {@link ReturnStatusHistoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ReturnStatusHistoryResourceIT {

    private static final ReturnStatus DEFAULT_STATUS = ReturnStatus.RETURN_REQUESTED;
    private static final ReturnStatus UPDATED_STATUS = ReturnStatus.RETURN_APPROVED;

    private static final Instant DEFAULT_CHANGE_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CHANGE_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/return-status-histories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ReturnStatusHistoryRepository returnStatusHistoryRepository;

    @Autowired
    private ReturnStatusHistoryMapper returnStatusHistoryMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restReturnStatusHistoryMockMvc;

    private ReturnStatusHistory returnStatusHistory;

    private ReturnStatusHistory insertedReturnStatusHistory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ReturnStatusHistory createEntity() {
        return new ReturnStatusHistory().status(DEFAULT_STATUS).changeTime(DEFAULT_CHANGE_TIME);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ReturnStatusHistory createUpdatedEntity() {
        return new ReturnStatusHistory().status(UPDATED_STATUS).changeTime(UPDATED_CHANGE_TIME);
    }

    @BeforeEach
    public void initTest() {
        returnStatusHistory = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedReturnStatusHistory != null) {
            returnStatusHistoryRepository.delete(insertedReturnStatusHistory);
            insertedReturnStatusHistory = null;
        }
    }

    @Test
    @Transactional
    void createReturnStatusHistory() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ReturnStatusHistory
        ReturnStatusHistoryDTO returnStatusHistoryDTO = returnStatusHistoryMapper.toDto(returnStatusHistory);
        var returnedReturnStatusHistoryDTO = om.readValue(
            restReturnStatusHistoryMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(returnStatusHistoryDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ReturnStatusHistoryDTO.class
        );

        // Validate the ReturnStatusHistory in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedReturnStatusHistory = returnStatusHistoryMapper.toEntity(returnedReturnStatusHistoryDTO);
        assertReturnStatusHistoryUpdatableFieldsEquals(
            returnedReturnStatusHistory,
            getPersistedReturnStatusHistory(returnedReturnStatusHistory)
        );

        insertedReturnStatusHistory = returnedReturnStatusHistory;
    }

    @Test
    @Transactional
    void createReturnStatusHistoryWithExistingId() throws Exception {
        // Create the ReturnStatusHistory with an existing ID
        returnStatusHistory.setId(1L);
        ReturnStatusHistoryDTO returnStatusHistoryDTO = returnStatusHistoryMapper.toDto(returnStatusHistory);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restReturnStatusHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(returnStatusHistoryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ReturnStatusHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        returnStatusHistory.setStatus(null);

        // Create the ReturnStatusHistory, which fails.
        ReturnStatusHistoryDTO returnStatusHistoryDTO = returnStatusHistoryMapper.toDto(returnStatusHistory);

        restReturnStatusHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(returnStatusHistoryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkChangeTimeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        returnStatusHistory.setChangeTime(null);

        // Create the ReturnStatusHistory, which fails.
        ReturnStatusHistoryDTO returnStatusHistoryDTO = returnStatusHistoryMapper.toDto(returnStatusHistory);

        restReturnStatusHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(returnStatusHistoryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllReturnStatusHistories() throws Exception {
        // Initialize the database
        insertedReturnStatusHistory = returnStatusHistoryRepository.saveAndFlush(returnStatusHistory);

        // Get all the returnStatusHistoryList
        restReturnStatusHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(returnStatusHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].changeTime").value(hasItem(DEFAULT_CHANGE_TIME.toString())));
    }

    @Test
    @Transactional
    void getReturnStatusHistory() throws Exception {
        // Initialize the database
        insertedReturnStatusHistory = returnStatusHistoryRepository.saveAndFlush(returnStatusHistory);

        // Get the returnStatusHistory
        restReturnStatusHistoryMockMvc
            .perform(get(ENTITY_API_URL_ID, returnStatusHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(returnStatusHistory.getId().intValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.changeTime").value(DEFAULT_CHANGE_TIME.toString()));
    }

    @Test
    @Transactional
    void getReturnStatusHistoriesByIdFiltering() throws Exception {
        // Initialize the database
        insertedReturnStatusHistory = returnStatusHistoryRepository.saveAndFlush(returnStatusHistory);

        Long id = returnStatusHistory.getId();

        defaultReturnStatusHistoryFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultReturnStatusHistoryFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultReturnStatusHistoryFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllReturnStatusHistoriesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReturnStatusHistory = returnStatusHistoryRepository.saveAndFlush(returnStatusHistory);

        // Get all the returnStatusHistoryList where status equals to
        defaultReturnStatusHistoryFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllReturnStatusHistoriesByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReturnStatusHistory = returnStatusHistoryRepository.saveAndFlush(returnStatusHistory);

        // Get all the returnStatusHistoryList where status in
        defaultReturnStatusHistoryFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllReturnStatusHistoriesByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReturnStatusHistory = returnStatusHistoryRepository.saveAndFlush(returnStatusHistory);

        // Get all the returnStatusHistoryList where status is not null
        defaultReturnStatusHistoryFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllReturnStatusHistoriesByChangeTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReturnStatusHistory = returnStatusHistoryRepository.saveAndFlush(returnStatusHistory);

        // Get all the returnStatusHistoryList where changeTime equals to
        defaultReturnStatusHistoryFiltering("changeTime.equals=" + DEFAULT_CHANGE_TIME, "changeTime.equals=" + UPDATED_CHANGE_TIME);
    }

    @Test
    @Transactional
    void getAllReturnStatusHistoriesByChangeTimeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReturnStatusHistory = returnStatusHistoryRepository.saveAndFlush(returnStatusHistory);

        // Get all the returnStatusHistoryList where changeTime in
        defaultReturnStatusHistoryFiltering(
            "changeTime.in=" + DEFAULT_CHANGE_TIME + "," + UPDATED_CHANGE_TIME,
            "changeTime.in=" + UPDATED_CHANGE_TIME
        );
    }

    @Test
    @Transactional
    void getAllReturnStatusHistoriesByChangeTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReturnStatusHistory = returnStatusHistoryRepository.saveAndFlush(returnStatusHistory);

        // Get all the returnStatusHistoryList where changeTime is not null
        defaultReturnStatusHistoryFiltering("changeTime.specified=true", "changeTime.specified=false");
    }

    @Test
    @Transactional
    void getAllReturnStatusHistoriesByReturnedOrderIsEqualToSomething() throws Exception {
        ReturnedOrder returnedOrder;
        if (TestUtil.findAll(em, ReturnedOrder.class).isEmpty()) {
            returnStatusHistoryRepository.saveAndFlush(returnStatusHistory);
            returnedOrder = ReturnedOrderResourceIT.createEntity(em);
        } else {
            returnedOrder = TestUtil.findAll(em, ReturnedOrder.class).get(0);
        }
        em.persist(returnedOrder);
        em.flush();
        returnStatusHistory.setReturnedOrder(returnedOrder);
        returnStatusHistoryRepository.saveAndFlush(returnStatusHistory);
        Long returnedOrderId = returnedOrder.getId();
        // Get all the returnStatusHistoryList where returnedOrder equals to returnedOrderId
        defaultReturnStatusHistoryShouldBeFound("returnedOrderId.equals=" + returnedOrderId);

        // Get all the returnStatusHistoryList where returnedOrder equals to (returnedOrderId + 1)
        defaultReturnStatusHistoryShouldNotBeFound("returnedOrderId.equals=" + (returnedOrderId + 1));
    }

    private void defaultReturnStatusHistoryFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultReturnStatusHistoryShouldBeFound(shouldBeFound);
        defaultReturnStatusHistoryShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultReturnStatusHistoryShouldBeFound(String filter) throws Exception {
        restReturnStatusHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(returnStatusHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].changeTime").value(hasItem(DEFAULT_CHANGE_TIME.toString())));

        // Check, that the count call also returns 1
        restReturnStatusHistoryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultReturnStatusHistoryShouldNotBeFound(String filter) throws Exception {
        restReturnStatusHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restReturnStatusHistoryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingReturnStatusHistory() throws Exception {
        // Get the returnStatusHistory
        restReturnStatusHistoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingReturnStatusHistory() throws Exception {
        // Initialize the database
        insertedReturnStatusHistory = returnStatusHistoryRepository.saveAndFlush(returnStatusHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the returnStatusHistory
        ReturnStatusHistory updatedReturnStatusHistory = returnStatusHistoryRepository.findById(returnStatusHistory.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedReturnStatusHistory are not directly saved in db
        em.detach(updatedReturnStatusHistory);
        updatedReturnStatusHistory.status(UPDATED_STATUS).changeTime(UPDATED_CHANGE_TIME);
        ReturnStatusHistoryDTO returnStatusHistoryDTO = returnStatusHistoryMapper.toDto(updatedReturnStatusHistory);

        restReturnStatusHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, returnStatusHistoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(returnStatusHistoryDTO))
            )
            .andExpect(status().isOk());

        // Validate the ReturnStatusHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedReturnStatusHistoryToMatchAllProperties(updatedReturnStatusHistory);
    }

    @Test
    @Transactional
    void putNonExistingReturnStatusHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        returnStatusHistory.setId(longCount.incrementAndGet());

        // Create the ReturnStatusHistory
        ReturnStatusHistoryDTO returnStatusHistoryDTO = returnStatusHistoryMapper.toDto(returnStatusHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReturnStatusHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, returnStatusHistoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(returnStatusHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReturnStatusHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchReturnStatusHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        returnStatusHistory.setId(longCount.incrementAndGet());

        // Create the ReturnStatusHistory
        ReturnStatusHistoryDTO returnStatusHistoryDTO = returnStatusHistoryMapper.toDto(returnStatusHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReturnStatusHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(returnStatusHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReturnStatusHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamReturnStatusHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        returnStatusHistory.setId(longCount.incrementAndGet());

        // Create the ReturnStatusHistory
        ReturnStatusHistoryDTO returnStatusHistoryDTO = returnStatusHistoryMapper.toDto(returnStatusHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReturnStatusHistoryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(returnStatusHistoryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ReturnStatusHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateReturnStatusHistoryWithPatch() throws Exception {
        // Initialize the database
        insertedReturnStatusHistory = returnStatusHistoryRepository.saveAndFlush(returnStatusHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the returnStatusHistory using partial update
        ReturnStatusHistory partialUpdatedReturnStatusHistory = new ReturnStatusHistory();
        partialUpdatedReturnStatusHistory.setId(returnStatusHistory.getId());

        partialUpdatedReturnStatusHistory.status(UPDATED_STATUS).changeTime(UPDATED_CHANGE_TIME);

        restReturnStatusHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReturnStatusHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReturnStatusHistory))
            )
            .andExpect(status().isOk());

        // Validate the ReturnStatusHistory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReturnStatusHistoryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedReturnStatusHistory, returnStatusHistory),
            getPersistedReturnStatusHistory(returnStatusHistory)
        );
    }

    @Test
    @Transactional
    void fullUpdateReturnStatusHistoryWithPatch() throws Exception {
        // Initialize the database
        insertedReturnStatusHistory = returnStatusHistoryRepository.saveAndFlush(returnStatusHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the returnStatusHistory using partial update
        ReturnStatusHistory partialUpdatedReturnStatusHistory = new ReturnStatusHistory();
        partialUpdatedReturnStatusHistory.setId(returnStatusHistory.getId());

        partialUpdatedReturnStatusHistory.status(UPDATED_STATUS).changeTime(UPDATED_CHANGE_TIME);

        restReturnStatusHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReturnStatusHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReturnStatusHistory))
            )
            .andExpect(status().isOk());

        // Validate the ReturnStatusHistory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReturnStatusHistoryUpdatableFieldsEquals(
            partialUpdatedReturnStatusHistory,
            getPersistedReturnStatusHistory(partialUpdatedReturnStatusHistory)
        );
    }

    @Test
    @Transactional
    void patchNonExistingReturnStatusHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        returnStatusHistory.setId(longCount.incrementAndGet());

        // Create the ReturnStatusHistory
        ReturnStatusHistoryDTO returnStatusHistoryDTO = returnStatusHistoryMapper.toDto(returnStatusHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReturnStatusHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, returnStatusHistoryDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(returnStatusHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReturnStatusHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchReturnStatusHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        returnStatusHistory.setId(longCount.incrementAndGet());

        // Create the ReturnStatusHistory
        ReturnStatusHistoryDTO returnStatusHistoryDTO = returnStatusHistoryMapper.toDto(returnStatusHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReturnStatusHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(returnStatusHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReturnStatusHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamReturnStatusHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        returnStatusHistory.setId(longCount.incrementAndGet());

        // Create the ReturnStatusHistory
        ReturnStatusHistoryDTO returnStatusHistoryDTO = returnStatusHistoryMapper.toDto(returnStatusHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReturnStatusHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(returnStatusHistoryDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ReturnStatusHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteReturnStatusHistory() throws Exception {
        // Initialize the database
        insertedReturnStatusHistory = returnStatusHistoryRepository.saveAndFlush(returnStatusHistory);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the returnStatusHistory
        restReturnStatusHistoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, returnStatusHistory.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return returnStatusHistoryRepository.count();
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

    protected ReturnStatusHistory getPersistedReturnStatusHistory(ReturnStatusHistory returnStatusHistory) {
        return returnStatusHistoryRepository.findById(returnStatusHistory.getId()).orElseThrow();
    }

    protected void assertPersistedReturnStatusHistoryToMatchAllProperties(ReturnStatusHistory expectedReturnStatusHistory) {
        assertReturnStatusHistoryAllPropertiesEquals(
            expectedReturnStatusHistory,
            getPersistedReturnStatusHistory(expectedReturnStatusHistory)
        );
    }

    protected void assertPersistedReturnStatusHistoryToMatchUpdatableProperties(ReturnStatusHistory expectedReturnStatusHistory) {
        assertReturnStatusHistoryAllUpdatablePropertiesEquals(
            expectedReturnStatusHistory,
            getPersistedReturnStatusHistory(expectedReturnStatusHistory)
        );
    }
}
