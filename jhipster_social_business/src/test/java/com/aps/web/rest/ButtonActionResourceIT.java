package com.aps.web.rest;

import static com.aps.domain.ButtonActionAsserts.*;
import static com.aps.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.aps.IntegrationTest;
import com.aps.domain.ButtonAction;
import com.aps.repository.ButtonActionRepository;
import com.aps.service.dto.ButtonActionDTO;
import com.aps.service.mapper.ButtonActionMapper;
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
 * Integration tests for the {@link ButtonActionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ButtonActionResourceIT {

    private static final String DEFAULT_WA_MESSAGE_ID = "AAAAAAAAAA";
    private static final String UPDATED_WA_MESSAGE_ID = "BBBBBBBBBB";

    private static final String DEFAULT_BUTTON_ID = "AAAAAAAAAA";
    private static final String UPDATED_BUTTON_ID = "BBBBBBBBBB";

    private static final Instant DEFAULT_CLICKED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CLICKED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_CLICKED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CLICKED_BY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/button-actions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ButtonActionRepository buttonActionRepository;

    @Autowired
    private ButtonActionMapper buttonActionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restButtonActionMockMvc;

    private ButtonAction buttonAction;

    private ButtonAction insertedButtonAction;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ButtonAction createEntity() {
        return new ButtonAction()
            .waMessageId(DEFAULT_WA_MESSAGE_ID)
            .buttonId(DEFAULT_BUTTON_ID)
            .clickedAt(DEFAULT_CLICKED_AT)
            .clickedBy(DEFAULT_CLICKED_BY);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ButtonAction createUpdatedEntity() {
        return new ButtonAction()
            .waMessageId(UPDATED_WA_MESSAGE_ID)
            .buttonId(UPDATED_BUTTON_ID)
            .clickedAt(UPDATED_CLICKED_AT)
            .clickedBy(UPDATED_CLICKED_BY);
    }

    @BeforeEach
    public void initTest() {
        buttonAction = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedButtonAction != null) {
            buttonActionRepository.delete(insertedButtonAction);
            insertedButtonAction = null;
        }
    }

    @Test
    @Transactional
    void createButtonAction() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ButtonAction
        ButtonActionDTO buttonActionDTO = buttonActionMapper.toDto(buttonAction);
        var returnedButtonActionDTO = om.readValue(
            restButtonActionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(buttonActionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ButtonActionDTO.class
        );

        // Validate the ButtonAction in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedButtonAction = buttonActionMapper.toEntity(returnedButtonActionDTO);
        assertButtonActionUpdatableFieldsEquals(returnedButtonAction, getPersistedButtonAction(returnedButtonAction));

        insertedButtonAction = returnedButtonAction;
    }

    @Test
    @Transactional
    void createButtonActionWithExistingId() throws Exception {
        // Create the ButtonAction with an existing ID
        buttonAction.setId(1L);
        ButtonActionDTO buttonActionDTO = buttonActionMapper.toDto(buttonAction);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restButtonActionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(buttonActionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ButtonAction in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkWaMessageIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        buttonAction.setWaMessageId(null);

        // Create the ButtonAction, which fails.
        ButtonActionDTO buttonActionDTO = buttonActionMapper.toDto(buttonAction);

        restButtonActionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(buttonActionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllButtonActions() throws Exception {
        // Initialize the database
        insertedButtonAction = buttonActionRepository.saveAndFlush(buttonAction);

        // Get all the buttonActionList
        restButtonActionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(buttonAction.getId().intValue())))
            .andExpect(jsonPath("$.[*].waMessageId").value(hasItem(DEFAULT_WA_MESSAGE_ID)))
            .andExpect(jsonPath("$.[*].buttonId").value(hasItem(DEFAULT_BUTTON_ID)))
            .andExpect(jsonPath("$.[*].clickedAt").value(hasItem(DEFAULT_CLICKED_AT.toString())))
            .andExpect(jsonPath("$.[*].clickedBy").value(hasItem(DEFAULT_CLICKED_BY)));
    }

    @Test
    @Transactional
    void getButtonAction() throws Exception {
        // Initialize the database
        insertedButtonAction = buttonActionRepository.saveAndFlush(buttonAction);

        // Get the buttonAction
        restButtonActionMockMvc
            .perform(get(ENTITY_API_URL_ID, buttonAction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(buttonAction.getId().intValue()))
            .andExpect(jsonPath("$.waMessageId").value(DEFAULT_WA_MESSAGE_ID))
            .andExpect(jsonPath("$.buttonId").value(DEFAULT_BUTTON_ID))
            .andExpect(jsonPath("$.clickedAt").value(DEFAULT_CLICKED_AT.toString()))
            .andExpect(jsonPath("$.clickedBy").value(DEFAULT_CLICKED_BY));
    }

    @Test
    @Transactional
    void getButtonActionsByIdFiltering() throws Exception {
        // Initialize the database
        insertedButtonAction = buttonActionRepository.saveAndFlush(buttonAction);

        Long id = buttonAction.getId();

        defaultButtonActionFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultButtonActionFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultButtonActionFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllButtonActionsByWaMessageIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedButtonAction = buttonActionRepository.saveAndFlush(buttonAction);

        // Get all the buttonActionList where waMessageId equals to
        defaultButtonActionFiltering("waMessageId.equals=" + DEFAULT_WA_MESSAGE_ID, "waMessageId.equals=" + UPDATED_WA_MESSAGE_ID);
    }

    @Test
    @Transactional
    void getAllButtonActionsByWaMessageIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedButtonAction = buttonActionRepository.saveAndFlush(buttonAction);

        // Get all the buttonActionList where waMessageId in
        defaultButtonActionFiltering(
            "waMessageId.in=" + DEFAULT_WA_MESSAGE_ID + "," + UPDATED_WA_MESSAGE_ID,
            "waMessageId.in=" + UPDATED_WA_MESSAGE_ID
        );
    }

    @Test
    @Transactional
    void getAllButtonActionsByWaMessageIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedButtonAction = buttonActionRepository.saveAndFlush(buttonAction);

        // Get all the buttonActionList where waMessageId is not null
        defaultButtonActionFiltering("waMessageId.specified=true", "waMessageId.specified=false");
    }

    @Test
    @Transactional
    void getAllButtonActionsByWaMessageIdContainsSomething() throws Exception {
        // Initialize the database
        insertedButtonAction = buttonActionRepository.saveAndFlush(buttonAction);

        // Get all the buttonActionList where waMessageId contains
        defaultButtonActionFiltering("waMessageId.contains=" + DEFAULT_WA_MESSAGE_ID, "waMessageId.contains=" + UPDATED_WA_MESSAGE_ID);
    }

    @Test
    @Transactional
    void getAllButtonActionsByWaMessageIdNotContainsSomething() throws Exception {
        // Initialize the database
        insertedButtonAction = buttonActionRepository.saveAndFlush(buttonAction);

        // Get all the buttonActionList where waMessageId does not contain
        defaultButtonActionFiltering(
            "waMessageId.doesNotContain=" + UPDATED_WA_MESSAGE_ID,
            "waMessageId.doesNotContain=" + DEFAULT_WA_MESSAGE_ID
        );
    }

    @Test
    @Transactional
    void getAllButtonActionsByButtonIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedButtonAction = buttonActionRepository.saveAndFlush(buttonAction);

        // Get all the buttonActionList where buttonId equals to
        defaultButtonActionFiltering("buttonId.equals=" + DEFAULT_BUTTON_ID, "buttonId.equals=" + UPDATED_BUTTON_ID);
    }

    @Test
    @Transactional
    void getAllButtonActionsByButtonIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedButtonAction = buttonActionRepository.saveAndFlush(buttonAction);

        // Get all the buttonActionList where buttonId in
        defaultButtonActionFiltering("buttonId.in=" + DEFAULT_BUTTON_ID + "," + UPDATED_BUTTON_ID, "buttonId.in=" + UPDATED_BUTTON_ID);
    }

    @Test
    @Transactional
    void getAllButtonActionsByButtonIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedButtonAction = buttonActionRepository.saveAndFlush(buttonAction);

        // Get all the buttonActionList where buttonId is not null
        defaultButtonActionFiltering("buttonId.specified=true", "buttonId.specified=false");
    }

    @Test
    @Transactional
    void getAllButtonActionsByButtonIdContainsSomething() throws Exception {
        // Initialize the database
        insertedButtonAction = buttonActionRepository.saveAndFlush(buttonAction);

        // Get all the buttonActionList where buttonId contains
        defaultButtonActionFiltering("buttonId.contains=" + DEFAULT_BUTTON_ID, "buttonId.contains=" + UPDATED_BUTTON_ID);
    }

    @Test
    @Transactional
    void getAllButtonActionsByButtonIdNotContainsSomething() throws Exception {
        // Initialize the database
        insertedButtonAction = buttonActionRepository.saveAndFlush(buttonAction);

        // Get all the buttonActionList where buttonId does not contain
        defaultButtonActionFiltering("buttonId.doesNotContain=" + UPDATED_BUTTON_ID, "buttonId.doesNotContain=" + DEFAULT_BUTTON_ID);
    }

    @Test
    @Transactional
    void getAllButtonActionsByClickedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedButtonAction = buttonActionRepository.saveAndFlush(buttonAction);

        // Get all the buttonActionList where clickedAt equals to
        defaultButtonActionFiltering("clickedAt.equals=" + DEFAULT_CLICKED_AT, "clickedAt.equals=" + UPDATED_CLICKED_AT);
    }

    @Test
    @Transactional
    void getAllButtonActionsByClickedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedButtonAction = buttonActionRepository.saveAndFlush(buttonAction);

        // Get all the buttonActionList where clickedAt in
        defaultButtonActionFiltering("clickedAt.in=" + DEFAULT_CLICKED_AT + "," + UPDATED_CLICKED_AT, "clickedAt.in=" + UPDATED_CLICKED_AT);
    }

    @Test
    @Transactional
    void getAllButtonActionsByClickedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedButtonAction = buttonActionRepository.saveAndFlush(buttonAction);

        // Get all the buttonActionList where clickedAt is not null
        defaultButtonActionFiltering("clickedAt.specified=true", "clickedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllButtonActionsByClickedByIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedButtonAction = buttonActionRepository.saveAndFlush(buttonAction);

        // Get all the buttonActionList where clickedBy equals to
        defaultButtonActionFiltering("clickedBy.equals=" + DEFAULT_CLICKED_BY, "clickedBy.equals=" + UPDATED_CLICKED_BY);
    }

    @Test
    @Transactional
    void getAllButtonActionsByClickedByIsInShouldWork() throws Exception {
        // Initialize the database
        insertedButtonAction = buttonActionRepository.saveAndFlush(buttonAction);

        // Get all the buttonActionList where clickedBy in
        defaultButtonActionFiltering("clickedBy.in=" + DEFAULT_CLICKED_BY + "," + UPDATED_CLICKED_BY, "clickedBy.in=" + UPDATED_CLICKED_BY);
    }

    @Test
    @Transactional
    void getAllButtonActionsByClickedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedButtonAction = buttonActionRepository.saveAndFlush(buttonAction);

        // Get all the buttonActionList where clickedBy is not null
        defaultButtonActionFiltering("clickedBy.specified=true", "clickedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllButtonActionsByClickedByContainsSomething() throws Exception {
        // Initialize the database
        insertedButtonAction = buttonActionRepository.saveAndFlush(buttonAction);

        // Get all the buttonActionList where clickedBy contains
        defaultButtonActionFiltering("clickedBy.contains=" + DEFAULT_CLICKED_BY, "clickedBy.contains=" + UPDATED_CLICKED_BY);
    }

    @Test
    @Transactional
    void getAllButtonActionsByClickedByNotContainsSomething() throws Exception {
        // Initialize the database
        insertedButtonAction = buttonActionRepository.saveAndFlush(buttonAction);

        // Get all the buttonActionList where clickedBy does not contain
        defaultButtonActionFiltering("clickedBy.doesNotContain=" + UPDATED_CLICKED_BY, "clickedBy.doesNotContain=" + DEFAULT_CLICKED_BY);
    }

    private void defaultButtonActionFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultButtonActionShouldBeFound(shouldBeFound);
        defaultButtonActionShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultButtonActionShouldBeFound(String filter) throws Exception {
        restButtonActionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(buttonAction.getId().intValue())))
            .andExpect(jsonPath("$.[*].waMessageId").value(hasItem(DEFAULT_WA_MESSAGE_ID)))
            .andExpect(jsonPath("$.[*].buttonId").value(hasItem(DEFAULT_BUTTON_ID)))
            .andExpect(jsonPath("$.[*].clickedAt").value(hasItem(DEFAULT_CLICKED_AT.toString())))
            .andExpect(jsonPath("$.[*].clickedBy").value(hasItem(DEFAULT_CLICKED_BY)));

        // Check, that the count call also returns 1
        restButtonActionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultButtonActionShouldNotBeFound(String filter) throws Exception {
        restButtonActionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restButtonActionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingButtonAction() throws Exception {
        // Get the buttonAction
        restButtonActionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingButtonAction() throws Exception {
        // Initialize the database
        insertedButtonAction = buttonActionRepository.saveAndFlush(buttonAction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the buttonAction
        ButtonAction updatedButtonAction = buttonActionRepository.findById(buttonAction.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedButtonAction are not directly saved in db
        em.detach(updatedButtonAction);
        updatedButtonAction
            .waMessageId(UPDATED_WA_MESSAGE_ID)
            .buttonId(UPDATED_BUTTON_ID)
            .clickedAt(UPDATED_CLICKED_AT)
            .clickedBy(UPDATED_CLICKED_BY);
        ButtonActionDTO buttonActionDTO = buttonActionMapper.toDto(updatedButtonAction);

        restButtonActionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, buttonActionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(buttonActionDTO))
            )
            .andExpect(status().isOk());

        // Validate the ButtonAction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedButtonActionToMatchAllProperties(updatedButtonAction);
    }

    @Test
    @Transactional
    void putNonExistingButtonAction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        buttonAction.setId(longCount.incrementAndGet());

        // Create the ButtonAction
        ButtonActionDTO buttonActionDTO = buttonActionMapper.toDto(buttonAction);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restButtonActionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, buttonActionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(buttonActionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ButtonAction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchButtonAction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        buttonAction.setId(longCount.incrementAndGet());

        // Create the ButtonAction
        ButtonActionDTO buttonActionDTO = buttonActionMapper.toDto(buttonAction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restButtonActionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(buttonActionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ButtonAction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamButtonAction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        buttonAction.setId(longCount.incrementAndGet());

        // Create the ButtonAction
        ButtonActionDTO buttonActionDTO = buttonActionMapper.toDto(buttonAction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restButtonActionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(buttonActionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ButtonAction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateButtonActionWithPatch() throws Exception {
        // Initialize the database
        insertedButtonAction = buttonActionRepository.saveAndFlush(buttonAction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the buttonAction using partial update
        ButtonAction partialUpdatedButtonAction = new ButtonAction();
        partialUpdatedButtonAction.setId(buttonAction.getId());

        partialUpdatedButtonAction.waMessageId(UPDATED_WA_MESSAGE_ID).buttonId(UPDATED_BUTTON_ID).clickedAt(UPDATED_CLICKED_AT);

        restButtonActionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedButtonAction.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedButtonAction))
            )
            .andExpect(status().isOk());

        // Validate the ButtonAction in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertButtonActionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedButtonAction, buttonAction),
            getPersistedButtonAction(buttonAction)
        );
    }

    @Test
    @Transactional
    void fullUpdateButtonActionWithPatch() throws Exception {
        // Initialize the database
        insertedButtonAction = buttonActionRepository.saveAndFlush(buttonAction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the buttonAction using partial update
        ButtonAction partialUpdatedButtonAction = new ButtonAction();
        partialUpdatedButtonAction.setId(buttonAction.getId());

        partialUpdatedButtonAction
            .waMessageId(UPDATED_WA_MESSAGE_ID)
            .buttonId(UPDATED_BUTTON_ID)
            .clickedAt(UPDATED_CLICKED_AT)
            .clickedBy(UPDATED_CLICKED_BY);

        restButtonActionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedButtonAction.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedButtonAction))
            )
            .andExpect(status().isOk());

        // Validate the ButtonAction in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertButtonActionUpdatableFieldsEquals(partialUpdatedButtonAction, getPersistedButtonAction(partialUpdatedButtonAction));
    }

    @Test
    @Transactional
    void patchNonExistingButtonAction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        buttonAction.setId(longCount.incrementAndGet());

        // Create the ButtonAction
        ButtonActionDTO buttonActionDTO = buttonActionMapper.toDto(buttonAction);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restButtonActionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, buttonActionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(buttonActionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ButtonAction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchButtonAction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        buttonAction.setId(longCount.incrementAndGet());

        // Create the ButtonAction
        ButtonActionDTO buttonActionDTO = buttonActionMapper.toDto(buttonAction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restButtonActionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(buttonActionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ButtonAction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamButtonAction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        buttonAction.setId(longCount.incrementAndGet());

        // Create the ButtonAction
        ButtonActionDTO buttonActionDTO = buttonActionMapper.toDto(buttonAction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restButtonActionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(buttonActionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ButtonAction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteButtonAction() throws Exception {
        // Initialize the database
        insertedButtonAction = buttonActionRepository.saveAndFlush(buttonAction);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the buttonAction
        restButtonActionMockMvc
            .perform(delete(ENTITY_API_URL_ID, buttonAction.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return buttonActionRepository.count();
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

    protected ButtonAction getPersistedButtonAction(ButtonAction buttonAction) {
        return buttonActionRepository.findById(buttonAction.getId()).orElseThrow();
    }

    protected void assertPersistedButtonActionToMatchAllProperties(ButtonAction expectedButtonAction) {
        assertButtonActionAllPropertiesEquals(expectedButtonAction, getPersistedButtonAction(expectedButtonAction));
    }

    protected void assertPersistedButtonActionToMatchUpdatableProperties(ButtonAction expectedButtonAction) {
        assertButtonActionAllUpdatablePropertiesEquals(expectedButtonAction, getPersistedButtonAction(expectedButtonAction));
    }
}
