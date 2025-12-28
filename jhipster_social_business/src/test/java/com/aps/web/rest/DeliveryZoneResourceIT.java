package com.aps.web.rest;

import static com.aps.domain.DeliveryZoneAsserts.*;
import static com.aps.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.aps.IntegrationTest;
import com.aps.domain.DeliveryZone;
import com.aps.repository.DeliveryZoneRepository;
import com.aps.service.dto.DeliveryZoneDTO;
import com.aps.service.mapper.DeliveryZoneMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link DeliveryZoneResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DeliveryZoneResourceIT {

    private static final String DEFAULT_ZONE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_ZONE_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PINCODE = "AAAAAAAAAA";
    private static final String UPDATED_PINCODE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/delivery-zones";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DeliveryZoneRepository deliveryZoneRepository;

    @Autowired
    private DeliveryZoneMapper deliveryZoneMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDeliveryZoneMockMvc;

    private DeliveryZone deliveryZone;

    private DeliveryZone insertedDeliveryZone;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DeliveryZone createEntity() {
        return new DeliveryZone().zoneName(DEFAULT_ZONE_NAME).pincode(DEFAULT_PINCODE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DeliveryZone createUpdatedEntity() {
        return new DeliveryZone().zoneName(UPDATED_ZONE_NAME).pincode(UPDATED_PINCODE);
    }

    @BeforeEach
    public void initTest() {
        deliveryZone = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedDeliveryZone != null) {
            deliveryZoneRepository.delete(insertedDeliveryZone);
            insertedDeliveryZone = null;
        }
    }

    @Test
    @Transactional
    void createDeliveryZone() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the DeliveryZone
        DeliveryZoneDTO deliveryZoneDTO = deliveryZoneMapper.toDto(deliveryZone);
        var returnedDeliveryZoneDTO = om.readValue(
            restDeliveryZoneMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(deliveryZoneDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DeliveryZoneDTO.class
        );

        // Validate the DeliveryZone in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDeliveryZone = deliveryZoneMapper.toEntity(returnedDeliveryZoneDTO);
        assertDeliveryZoneUpdatableFieldsEquals(returnedDeliveryZone, getPersistedDeliveryZone(returnedDeliveryZone));

        insertedDeliveryZone = returnedDeliveryZone;
    }

    @Test
    @Transactional
    void createDeliveryZoneWithExistingId() throws Exception {
        // Create the DeliveryZone with an existing ID
        deliveryZone.setId(1L);
        DeliveryZoneDTO deliveryZoneDTO = deliveryZoneMapper.toDto(deliveryZone);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDeliveryZoneMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(deliveryZoneDTO)))
            .andExpect(status().isBadRequest());

        // Validate the DeliveryZone in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkZoneNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        deliveryZone.setZoneName(null);

        // Create the DeliveryZone, which fails.
        DeliveryZoneDTO deliveryZoneDTO = deliveryZoneMapper.toDto(deliveryZone);

        restDeliveryZoneMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(deliveryZoneDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDeliveryZones() throws Exception {
        // Initialize the database
        insertedDeliveryZone = deliveryZoneRepository.saveAndFlush(deliveryZone);

        // Get all the deliveryZoneList
        restDeliveryZoneMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(deliveryZone.getId().intValue())))
            .andExpect(jsonPath("$.[*].zoneName").value(hasItem(DEFAULT_ZONE_NAME)))
            .andExpect(jsonPath("$.[*].pincode").value(hasItem(DEFAULT_PINCODE)));
    }

    @Test
    @Transactional
    void getDeliveryZone() throws Exception {
        // Initialize the database
        insertedDeliveryZone = deliveryZoneRepository.saveAndFlush(deliveryZone);

        // Get the deliveryZone
        restDeliveryZoneMockMvc
            .perform(get(ENTITY_API_URL_ID, deliveryZone.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(deliveryZone.getId().intValue()))
            .andExpect(jsonPath("$.zoneName").value(DEFAULT_ZONE_NAME))
            .andExpect(jsonPath("$.pincode").value(DEFAULT_PINCODE));
    }

    @Test
    @Transactional
    void getDeliveryZonesByIdFiltering() throws Exception {
        // Initialize the database
        insertedDeliveryZone = deliveryZoneRepository.saveAndFlush(deliveryZone);

        Long id = deliveryZone.getId();

        defaultDeliveryZoneFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultDeliveryZoneFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultDeliveryZoneFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDeliveryZonesByZoneNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDeliveryZone = deliveryZoneRepository.saveAndFlush(deliveryZone);

        // Get all the deliveryZoneList where zoneName equals to
        defaultDeliveryZoneFiltering("zoneName.equals=" + DEFAULT_ZONE_NAME, "zoneName.equals=" + UPDATED_ZONE_NAME);
    }

    @Test
    @Transactional
    void getAllDeliveryZonesByZoneNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDeliveryZone = deliveryZoneRepository.saveAndFlush(deliveryZone);

        // Get all the deliveryZoneList where zoneName in
        defaultDeliveryZoneFiltering("zoneName.in=" + DEFAULT_ZONE_NAME + "," + UPDATED_ZONE_NAME, "zoneName.in=" + UPDATED_ZONE_NAME);
    }

    @Test
    @Transactional
    void getAllDeliveryZonesByZoneNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDeliveryZone = deliveryZoneRepository.saveAndFlush(deliveryZone);

        // Get all the deliveryZoneList where zoneName is not null
        defaultDeliveryZoneFiltering("zoneName.specified=true", "zoneName.specified=false");
    }

    @Test
    @Transactional
    void getAllDeliveryZonesByZoneNameContainsSomething() throws Exception {
        // Initialize the database
        insertedDeliveryZone = deliveryZoneRepository.saveAndFlush(deliveryZone);

        // Get all the deliveryZoneList where zoneName contains
        defaultDeliveryZoneFiltering("zoneName.contains=" + DEFAULT_ZONE_NAME, "zoneName.contains=" + UPDATED_ZONE_NAME);
    }

    @Test
    @Transactional
    void getAllDeliveryZonesByZoneNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDeliveryZone = deliveryZoneRepository.saveAndFlush(deliveryZone);

        // Get all the deliveryZoneList where zoneName does not contain
        defaultDeliveryZoneFiltering("zoneName.doesNotContain=" + UPDATED_ZONE_NAME, "zoneName.doesNotContain=" + DEFAULT_ZONE_NAME);
    }

    @Test
    @Transactional
    void getAllDeliveryZonesByPincodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDeliveryZone = deliveryZoneRepository.saveAndFlush(deliveryZone);

        // Get all the deliveryZoneList where pincode equals to
        defaultDeliveryZoneFiltering("pincode.equals=" + DEFAULT_PINCODE, "pincode.equals=" + UPDATED_PINCODE);
    }

    @Test
    @Transactional
    void getAllDeliveryZonesByPincodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDeliveryZone = deliveryZoneRepository.saveAndFlush(deliveryZone);

        // Get all the deliveryZoneList where pincode in
        defaultDeliveryZoneFiltering("pincode.in=" + DEFAULT_PINCODE + "," + UPDATED_PINCODE, "pincode.in=" + UPDATED_PINCODE);
    }

    @Test
    @Transactional
    void getAllDeliveryZonesByPincodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDeliveryZone = deliveryZoneRepository.saveAndFlush(deliveryZone);

        // Get all the deliveryZoneList where pincode is not null
        defaultDeliveryZoneFiltering("pincode.specified=true", "pincode.specified=false");
    }

    @Test
    @Transactional
    void getAllDeliveryZonesByPincodeContainsSomething() throws Exception {
        // Initialize the database
        insertedDeliveryZone = deliveryZoneRepository.saveAndFlush(deliveryZone);

        // Get all the deliveryZoneList where pincode contains
        defaultDeliveryZoneFiltering("pincode.contains=" + DEFAULT_PINCODE, "pincode.contains=" + UPDATED_PINCODE);
    }

    @Test
    @Transactional
    void getAllDeliveryZonesByPincodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDeliveryZone = deliveryZoneRepository.saveAndFlush(deliveryZone);

        // Get all the deliveryZoneList where pincode does not contain
        defaultDeliveryZoneFiltering("pincode.doesNotContain=" + UPDATED_PINCODE, "pincode.doesNotContain=" + DEFAULT_PINCODE);
    }

    private void defaultDeliveryZoneFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultDeliveryZoneShouldBeFound(shouldBeFound);
        defaultDeliveryZoneShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDeliveryZoneShouldBeFound(String filter) throws Exception {
        restDeliveryZoneMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(deliveryZone.getId().intValue())))
            .andExpect(jsonPath("$.[*].zoneName").value(hasItem(DEFAULT_ZONE_NAME)))
            .andExpect(jsonPath("$.[*].pincode").value(hasItem(DEFAULT_PINCODE)));

        // Check, that the count call also returns 1
        restDeliveryZoneMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDeliveryZoneShouldNotBeFound(String filter) throws Exception {
        restDeliveryZoneMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDeliveryZoneMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDeliveryZone() throws Exception {
        // Get the deliveryZone
        restDeliveryZoneMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDeliveryZone() throws Exception {
        // Initialize the database
        insertedDeliveryZone = deliveryZoneRepository.saveAndFlush(deliveryZone);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the deliveryZone
        DeliveryZone updatedDeliveryZone = deliveryZoneRepository.findById(deliveryZone.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDeliveryZone are not directly saved in db
        em.detach(updatedDeliveryZone);
        updatedDeliveryZone.zoneName(UPDATED_ZONE_NAME).pincode(UPDATED_PINCODE);
        DeliveryZoneDTO deliveryZoneDTO = deliveryZoneMapper.toDto(updatedDeliveryZone);

        restDeliveryZoneMockMvc
            .perform(
                put(ENTITY_API_URL_ID, deliveryZoneDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(deliveryZoneDTO))
            )
            .andExpect(status().isOk());

        // Validate the DeliveryZone in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDeliveryZoneToMatchAllProperties(updatedDeliveryZone);
    }

    @Test
    @Transactional
    void putNonExistingDeliveryZone() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        deliveryZone.setId(longCount.incrementAndGet());

        // Create the DeliveryZone
        DeliveryZoneDTO deliveryZoneDTO = deliveryZoneMapper.toDto(deliveryZone);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDeliveryZoneMockMvc
            .perform(
                put(ENTITY_API_URL_ID, deliveryZoneDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(deliveryZoneDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DeliveryZone in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDeliveryZone() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        deliveryZone.setId(longCount.incrementAndGet());

        // Create the DeliveryZone
        DeliveryZoneDTO deliveryZoneDTO = deliveryZoneMapper.toDto(deliveryZone);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeliveryZoneMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(deliveryZoneDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DeliveryZone in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDeliveryZone() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        deliveryZone.setId(longCount.incrementAndGet());

        // Create the DeliveryZone
        DeliveryZoneDTO deliveryZoneDTO = deliveryZoneMapper.toDto(deliveryZone);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeliveryZoneMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(deliveryZoneDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DeliveryZone in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDeliveryZoneWithPatch() throws Exception {
        // Initialize the database
        insertedDeliveryZone = deliveryZoneRepository.saveAndFlush(deliveryZone);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the deliveryZone using partial update
        DeliveryZone partialUpdatedDeliveryZone = new DeliveryZone();
        partialUpdatedDeliveryZone.setId(deliveryZone.getId());

        partialUpdatedDeliveryZone.zoneName(UPDATED_ZONE_NAME);

        restDeliveryZoneMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDeliveryZone.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDeliveryZone))
            )
            .andExpect(status().isOk());

        // Validate the DeliveryZone in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDeliveryZoneUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDeliveryZone, deliveryZone),
            getPersistedDeliveryZone(deliveryZone)
        );
    }

    @Test
    @Transactional
    void fullUpdateDeliveryZoneWithPatch() throws Exception {
        // Initialize the database
        insertedDeliveryZone = deliveryZoneRepository.saveAndFlush(deliveryZone);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the deliveryZone using partial update
        DeliveryZone partialUpdatedDeliveryZone = new DeliveryZone();
        partialUpdatedDeliveryZone.setId(deliveryZone.getId());

        partialUpdatedDeliveryZone.zoneName(UPDATED_ZONE_NAME).pincode(UPDATED_PINCODE);

        restDeliveryZoneMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDeliveryZone.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDeliveryZone))
            )
            .andExpect(status().isOk());

        // Validate the DeliveryZone in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDeliveryZoneUpdatableFieldsEquals(partialUpdatedDeliveryZone, getPersistedDeliveryZone(partialUpdatedDeliveryZone));
    }

    @Test
    @Transactional
    void patchNonExistingDeliveryZone() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        deliveryZone.setId(longCount.incrementAndGet());

        // Create the DeliveryZone
        DeliveryZoneDTO deliveryZoneDTO = deliveryZoneMapper.toDto(deliveryZone);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDeliveryZoneMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, deliveryZoneDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(deliveryZoneDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DeliveryZone in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDeliveryZone() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        deliveryZone.setId(longCount.incrementAndGet());

        // Create the DeliveryZone
        DeliveryZoneDTO deliveryZoneDTO = deliveryZoneMapper.toDto(deliveryZone);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeliveryZoneMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(deliveryZoneDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DeliveryZone in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDeliveryZone() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        deliveryZone.setId(longCount.incrementAndGet());

        // Create the DeliveryZone
        DeliveryZoneDTO deliveryZoneDTO = deliveryZoneMapper.toDto(deliveryZone);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeliveryZoneMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(deliveryZoneDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DeliveryZone in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDeliveryZone() throws Exception {
        // Initialize the database
        insertedDeliveryZone = deliveryZoneRepository.saveAndFlush(deliveryZone);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the deliveryZone
        restDeliveryZoneMockMvc
            .perform(delete(ENTITY_API_URL_ID, deliveryZone.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return deliveryZoneRepository.count();
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

    protected DeliveryZone getPersistedDeliveryZone(DeliveryZone deliveryZone) {
        return deliveryZoneRepository.findById(deliveryZone.getId()).orElseThrow();
    }

    protected void assertPersistedDeliveryZoneToMatchAllProperties(DeliveryZone expectedDeliveryZone) {
        assertDeliveryZoneAllPropertiesEquals(expectedDeliveryZone, getPersistedDeliveryZone(expectedDeliveryZone));
    }

    protected void assertPersistedDeliveryZoneToMatchUpdatableProperties(DeliveryZone expectedDeliveryZone) {
        assertDeliveryZoneAllUpdatablePropertiesEquals(expectedDeliveryZone, getPersistedDeliveryZone(expectedDeliveryZone));
    }
}
