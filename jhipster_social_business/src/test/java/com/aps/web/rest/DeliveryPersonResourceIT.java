package com.aps.web.rest;

import static com.aps.domain.DeliveryPersonAsserts.*;
import static com.aps.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.aps.IntegrationTest;
import com.aps.domain.DeliveryPerson;
import com.aps.domain.DeliveryZone;
import com.aps.domain.enumeration.DeliveryStatus;
import com.aps.repository.DeliveryPersonRepository;
import com.aps.service.DeliveryPersonService;
import com.aps.service.UserRemovalService;
import com.aps.service.dto.DeliveryPersonDTO;
import com.aps.service.mapper.DeliveryPersonMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link DeliveryPersonResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class DeliveryPersonResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_WA_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_WA_PHONE_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBB";

    private static final DeliveryStatus DEFAULT_STATUS = DeliveryStatus.FREE;
    private static final DeliveryStatus UPDATED_STATUS = DeliveryStatus.BUSY;

    private static final Instant DEFAULT_JOINED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_JOINED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/delivery-people";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DeliveryPersonRepository deliveryPersonRepository;

    @Autowired
    private DeliveryPersonMapper deliveryPersonMapper;

    @Autowired
    private DeliveryPersonService deliveryPersonService;

    // We mock UserRemovalService because delete calls it
    @MockBean
    private UserRemovalService userRemovalService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDeliveryPersonMockMvc;

    private DeliveryPerson deliveryPerson;

    private DeliveryPerson insertedDeliveryPerson;

    /**
     * Create an entity for this test.
     */
    public static DeliveryPerson createEntity(EntityManager em) {
        DeliveryZone zone = new DeliveryZone().zoneName("Zone A").pincode("123456");
        em.persist(zone);
        em.flush();

        DeliveryPerson deliveryPerson = new DeliveryPerson()
                .name(DEFAULT_NAME)
                .waPhoneNumber(DEFAULT_WA_PHONE_NUMBER)
                .phoneNumber(DEFAULT_PHONE_NUMBER)
                .status(DEFAULT_STATUS)
                .joinedAt(DEFAULT_JOINED_AT)
                .isActive(DEFAULT_IS_ACTIVE)
                .zone(zone);
        return deliveryPerson;
    }

    /**
     * Create an updated entity for this test.
     */
    public static DeliveryPerson createUpdatedEntity(EntityManager em) {
        DeliveryZone zone = new DeliveryZone().zoneName("Zone B").pincode("654321");
        em.persist(zone);
        em.flush();

        DeliveryPerson deliveryPerson = new DeliveryPerson()
                .name(UPDATED_NAME)
                .waPhoneNumber(UPDATED_WA_PHONE_NUMBER)
                .phoneNumber(UPDATED_PHONE_NUMBER)
                .status(UPDATED_STATUS)
                .joinedAt(UPDATED_JOINED_AT)
                .isActive(UPDATED_IS_ACTIVE)
                .zone(zone);
        return deliveryPerson;
    }

    @BeforeEach
    public void initTest() {
        deliveryPerson = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedDeliveryPerson != null) {
            deliveryPersonRepository.delete(insertedDeliveryPerson);
            insertedDeliveryPerson = null;
        }
    }

    @Test
    @Transactional
    void createDeliveryPerson() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the DeliveryPerson
        DeliveryPersonDTO deliveryPersonDTO = deliveryPersonMapper.toDto(deliveryPerson);
        var returnedDeliveryPersonDTO = om.readValue(
                restDeliveryPersonMockMvc
                        .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsBytes(deliveryPersonDTO)))
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(),
                DeliveryPersonDTO.class);

        // Validate the DeliveryPerson in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDeliveryPerson = deliveryPersonMapper.toEntity(returnedDeliveryPersonDTO);
        assertDeliveryPersonUpdatableFieldsEquals(returnedDeliveryPerson,
                getPersistedDeliveryPerson(returnedDeliveryPerson));

        insertedDeliveryPerson = returnedDeliveryPerson;
    }

    @Test
    @Transactional
    void createDeliveryPersonWithExistingId() throws Exception {
        // Create the DeliveryPerson with an existing ID
        deliveryPerson.setId(1L);
        DeliveryPersonDTO deliveryPersonDTO = deliveryPersonMapper.toDto(deliveryPerson);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDeliveryPersonMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(deliveryPersonDTO)))
                .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        deliveryPerson.setName(null);

        // Create the DeliveryPerson, which fails.
        DeliveryPersonDTO deliveryPersonDTO = deliveryPersonMapper.toDto(deliveryPerson);

        restDeliveryPersonMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(deliveryPersonDTO)))
                .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPhoneNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        deliveryPerson.setPhoneNumber(null);

        // Create the DeliveryPerson, which fails.
        DeliveryPersonDTO deliveryPersonDTO = deliveryPersonMapper.toDto(deliveryPerson);

        restDeliveryPersonMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(deliveryPersonDTO)))
                .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDeliveryPeople() throws Exception {
        // Initialize the database
        insertedDeliveryPerson = deliveryPersonRepository.saveAndFlush(deliveryPerson);

        // Get all the deliveryPersonList
        restDeliveryPersonMockMvc
                .perform(get(ENTITY_API_URL + "?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(deliveryPerson.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
                .andExpect(jsonPath("$.[*].waPhoneNumber").value(hasItem(DEFAULT_WA_PHONE_NUMBER)))
                .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
                .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
                .andExpect(jsonPath("$.[*].joinedAt").value(hasItem(DEFAULT_JOINED_AT.toString())))
                .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)));
    }

    @Test
    @Transactional
    void getDeliveryPerson() throws Exception {
        // Initialize the database
        insertedDeliveryPerson = deliveryPersonRepository.saveAndFlush(deliveryPerson);

        // Get the deliveryPerson
        restDeliveryPersonMockMvc
                .perform(get(ENTITY_API_URL_ID, deliveryPerson.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(deliveryPerson.getId().intValue()))
                .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
                .andExpect(jsonPath("$.waPhoneNumber").value(DEFAULT_WA_PHONE_NUMBER))
                .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER))
                .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
                .andExpect(jsonPath("$.joinedAt").value(DEFAULT_JOINED_AT.toString()))
                .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE));
    }

    @Test
    @Transactional
    void updateDeliveryPerson() throws Exception {
        // Initialize the database
        insertedDeliveryPerson = deliveryPersonRepository.saveAndFlush(deliveryPerson);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the deliveryPerson
        DeliveryPerson updatedDeliveryPerson = deliveryPersonRepository.findById(deliveryPerson.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDeliveryPerson are not
        // directly saved in db
        em.detach(updatedDeliveryPerson);
        updatedDeliveryPerson
                .name(UPDATED_NAME)
                .waPhoneNumber(UPDATED_WA_PHONE_NUMBER)
                .phoneNumber(UPDATED_PHONE_NUMBER)
                .status(UPDATED_STATUS)
                .joinedAt(UPDATED_JOINED_AT)
                .isActive(UPDATED_IS_ACTIVE);
        DeliveryPersonDTO deliveryPersonDTO = deliveryPersonMapper.toDto(updatedDeliveryPerson);

        restDeliveryPersonMockMvc
                .perform(
                        put(ENTITY_API_URL_ID, deliveryPersonDTO.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsBytes(deliveryPersonDTO)))
                .andExpect(status().isOk());

        // Validate the DeliveryPerson in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDeliveryPersonToMatchAllProperties(updatedDeliveryPerson);
    }

    @Test
    @Transactional
    void deleteDeliveryPerson() throws Exception {
        // Initialize the database
        insertedDeliveryPerson = deliveryPersonRepository.saveAndFlush(deliveryPerson);
        insertDeliveryPerson(insertedDeliveryPerson); // Helper to verify later if needed, but not using count check
                                                      // here as we mock delete

        // We are using userRemovalService for delete, so repo count won't decrease
        // automatically in this transactional test unless service calls actual delete.
        // But we Mocked userRemovalService. So we verify the call.

        restDeliveryPersonMockMvc
                .perform(delete(ENTITY_API_URL_ID, deliveryPerson.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(userRemovalService).removeDeliveryPerson(eq(deliveryPerson.getId()), anyString());
    }

    protected long getRepositoryCount() {
        return deliveryPersonRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected DeliveryPerson getPersistedDeliveryPerson(DeliveryPerson deliveryPerson) {
        return deliveryPersonRepository.findById(deliveryPerson.getId()).orElseThrow();
    }

    protected void assertPersistedDeliveryPersonToMatchAllProperties(DeliveryPerson expectedDeliveryPerson) {
        assertDeliveryPersonAllPropertiesEquals(expectedDeliveryPerson,
                getPersistedDeliveryPerson(expectedDeliveryPerson));
    }

    protected void assertDeliveryPersonUpdatableFieldsEquals(DeliveryPerson expected, DeliveryPerson actual) {
        assertDeliveryPersonAllUpdatablePropertiesEquals(expected, actual);
    }

    protected void insertDeliveryPerson(DeliveryPerson deliveryPerson) {
        deliveryPersonRepository.saveAndFlush(deliveryPerson);
        insertedDeliveryPerson = deliveryPerson;
    }
}
