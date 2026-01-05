package com.aps.web.rest;

import static com.aps.domain.RemovedUserAsserts.*;
import static com.aps.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.aps.IntegrationTest;
import com.aps.domain.RemovedUser;
import com.aps.domain.enumeration.AccountStatus;
import com.aps.domain.enumeration.UserRole;
import com.aps.domain.RemovedOrderSummary;
import com.aps.repository.RemovedOrderSummaryRepository;
import com.aps.repository.RemovedUserRepository;
import com.aps.service.dto.RemovedUserDTO;
import com.aps.service.mapper.RemovedUserMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
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
 * Integration tests for the {@link RemovedUserResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RemovedUserResourceIT {

    private static final Long DEFAULT_ORIGINAL_ID = 1L;
    private static final Long UPDATED_ORIGINAL_ID = 2L;
    private static final Long SMALLER_ORIGINAL_ID = 1L - 1L;

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final UserRole DEFAULT_ROLE = UserRole.CUSTOMER;
    private static final UserRole UPDATED_ROLE = UserRole.EXECUTIVE;

    private static final String DEFAULT_WHATSAPP_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_WHATSAPP_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final Double DEFAULT_LOCATION_LAT = 1D;
    private static final Double UPDATED_LOCATION_LAT = 2D;
    private static final Double SMALLER_LOCATION_LAT = 1D - 1D;

    private static final Double DEFAULT_LOCATION_LON = 1D;
    private static final Double UPDATED_LOCATION_LON = 2D;
    private static final Double SMALLER_LOCATION_LON = 1D - 1D;

    private static final Instant DEFAULT_JOINED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_JOINED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_REMOVED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_REMOVED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_REASON_FOR_REMOVAL = "AAAAAAAAAA";
    private static final String UPDATED_REASON_FOR_REMOVAL = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_SESSION_DATA = "AAAAAAAAAA";
    private static final String UPDATED_LAST_SESSION_DATA = "BBBBBBBBBB";

    private static final AccountStatus DEFAULT_STATUS = AccountStatus.ACCOUNT_REMOVED;
    private static final AccountStatus UPDATED_STATUS = AccountStatus.RESTORE_ACCOUNT;

    private static final Long DEFAULT_ORDER_HISTORY_ID = 1L;
    private static final Long UPDATED_ORDER_HISTORY_ID = 2L;
    private static final Long SMALLER_ORDER_HISTORY_ID = 1L - 1L;

    private static final Double DEFAULT_DISTANCE_FROM_BUSINESS_KM = 1D;
    private static final Double UPDATED_DISTANCE_FROM_BUSINESS_KM = 2D;
    private static final Double SMALLER_DISTANCE_FROM_BUSINESS_KM = 1D - 1D;

    private static final Boolean DEFAULT_IS_PINCODE_VALID = false;
    private static final Boolean UPDATED_IS_PINCODE_VALID = true;

    private static final String ENTITY_API_URL = "/api/removed-users";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RemovedUserRepository removedUserRepository;

    @Autowired
    private RemovedOrderSummaryRepository removedOrderSummaryRepository;

    @Autowired
    private RemovedUserMapper removedUserMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRemovedUserMockMvc;

    private RemovedUser removedUser;

    private RemovedUser insertedRemovedUser;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RemovedUser createEntity() {
        return new RemovedUser()
                .originalId(DEFAULT_ORIGINAL_ID)
                .name(DEFAULT_NAME)
                .role(DEFAULT_ROLE)
                .whatsappNumber(DEFAULT_WHATSAPP_NUMBER)
                .phoneNumber(DEFAULT_PHONE_NUMBER)
                .address(DEFAULT_ADDRESS)
                .locationLat(DEFAULT_LOCATION_LAT)
                .locationLon(DEFAULT_LOCATION_LON)
                .joinedAt(DEFAULT_JOINED_AT)
                .removedAt(DEFAULT_REMOVED_AT)
                .reasonForRemoval(DEFAULT_REASON_FOR_REMOVAL)
                .lastSessionData(DEFAULT_LAST_SESSION_DATA)
                .status(DEFAULT_STATUS)
                .orderHistoryId(DEFAULT_ORDER_HISTORY_ID)
                .distanceFromBusinessKm(DEFAULT_DISTANCE_FROM_BUSINESS_KM)
                .isPincodeValid(DEFAULT_IS_PINCODE_VALID);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RemovedUser createUpdatedEntity() {
        return new RemovedUser()
                .originalId(UPDATED_ORIGINAL_ID)
                .name(UPDATED_NAME)
                .role(UPDATED_ROLE)
                .whatsappNumber(UPDATED_WHATSAPP_NUMBER)
                .phoneNumber(UPDATED_PHONE_NUMBER)
                .address(UPDATED_ADDRESS)
                .locationLat(UPDATED_LOCATION_LAT)
                .locationLon(UPDATED_LOCATION_LON)
                .joinedAt(UPDATED_JOINED_AT)
                .removedAt(UPDATED_REMOVED_AT)
                .reasonForRemoval(UPDATED_REASON_FOR_REMOVAL)
                .lastSessionData(UPDATED_LAST_SESSION_DATA)
                .status(UPDATED_STATUS)
                .orderHistoryId(UPDATED_ORDER_HISTORY_ID)
                .distanceFromBusinessKm(UPDATED_DISTANCE_FROM_BUSINESS_KM)
                .isPincodeValid(UPDATED_IS_PINCODE_VALID);
    }

    @BeforeEach
    public void initTest() {
        removedUser = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedRemovedUser != null) {
            removedUserRepository.delete(insertedRemovedUser);
            insertedRemovedUser = null;
        }
    }

    @Test
    @Transactional
    void createRemovedUser() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the RemovedUser
        RemovedUserDTO removedUserDTO = removedUserMapper.toDto(removedUser);
        var returnedRemovedUserDTO = om.readValue(
                restRemovedUserMockMvc
                        .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsBytes(removedUserDTO)))
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(),
                RemovedUserDTO.class);

        // Validate the RemovedUser in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedRemovedUser = removedUserMapper.toEntity(returnedRemovedUserDTO);
        assertRemovedUserUpdatableFieldsEquals(returnedRemovedUser, getPersistedRemovedUser(returnedRemovedUser));

        insertedRemovedUser = returnedRemovedUser;
    }

    @Test
    @Transactional
    void createRemovedUserWithExistingId() throws Exception {
        // Create the RemovedUser with an existing ID
        removedUser.setId(1L);
        RemovedUserDTO removedUserDTO = removedUserMapper.toDto(removedUser);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRemovedUserMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(removedUserDTO)))
                .andExpect(status().isBadRequest());

        // Validate the RemovedUser in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllRemovedUsers() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList
        restRemovedUserMockMvc
                .perform(get(ENTITY_API_URL + "?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(removedUser.getId().intValue())))
                .andExpect(jsonPath("$.[*].originalId").value(hasItem(DEFAULT_ORIGINAL_ID.intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
                .andExpect(jsonPath("$.[*].role").value(hasItem(DEFAULT_ROLE.toString())))
                .andExpect(jsonPath("$.[*].whatsappNumber").value(hasItem(DEFAULT_WHATSAPP_NUMBER)))
                .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
                .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
                .andExpect(jsonPath("$.[*].locationLat").value(hasItem(DEFAULT_LOCATION_LAT)))
                .andExpect(jsonPath("$.[*].locationLon").value(hasItem(DEFAULT_LOCATION_LON)))
                .andExpect(jsonPath("$.[*].joinedAt").value(hasItem(DEFAULT_JOINED_AT.toString())))
                .andExpect(jsonPath("$.[*].removedAt").value(hasItem(DEFAULT_REMOVED_AT.toString())))
                .andExpect(jsonPath("$.[*].reasonForRemoval").value(hasItem(DEFAULT_REASON_FOR_REMOVAL)))
                .andExpect(jsonPath("$.[*].lastSessionData").value(hasItem(DEFAULT_LAST_SESSION_DATA)))
                .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
                .andExpect(jsonPath("$.[*].orderHistoryId").value(hasItem(DEFAULT_ORDER_HISTORY_ID.intValue())))
                .andExpect(jsonPath("$.[*].distanceFromBusinessKm").value(hasItem(DEFAULT_DISTANCE_FROM_BUSINESS_KM)))
                .andExpect(jsonPath("$.[*].isPincodeValid").value(hasItem(DEFAULT_IS_PINCODE_VALID)));
    }

    @Test
    @Transactional
    void getRemovedUser() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get the removedUser
        restRemovedUserMockMvc
                .perform(get(ENTITY_API_URL_ID, removedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(removedUser.getId().intValue()))
                .andExpect(jsonPath("$.originalId").value(DEFAULT_ORIGINAL_ID.intValue()))
                .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
                .andExpect(jsonPath("$.role").value(DEFAULT_ROLE.toString()))
                .andExpect(jsonPath("$.whatsappNumber").value(DEFAULT_WHATSAPP_NUMBER))
                .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER))
                .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS))
                .andExpect(jsonPath("$.locationLat").value(DEFAULT_LOCATION_LAT))
                .andExpect(jsonPath("$.locationLon").value(DEFAULT_LOCATION_LON))
                .andExpect(jsonPath("$.joinedAt").value(DEFAULT_JOINED_AT.toString()))
                .andExpect(jsonPath("$.removedAt").value(DEFAULT_REMOVED_AT.toString()))
                .andExpect(jsonPath("$.reasonForRemoval").value(DEFAULT_REASON_FOR_REMOVAL))
                .andExpect(jsonPath("$.lastSessionData").value(DEFAULT_LAST_SESSION_DATA))
                .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
                .andExpect(jsonPath("$.orderHistoryId").value(DEFAULT_ORDER_HISTORY_ID.intValue()))
                .andExpect(jsonPath("$.distanceFromBusinessKm").value(DEFAULT_DISTANCE_FROM_BUSINESS_KM))
                .andExpect(jsonPath("$.isPincodeValid").value(DEFAULT_IS_PINCODE_VALID));
    }

    @Test
    @Transactional
    void getRemovedUsersByIdFiltering() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        Long id = removedUser.getId();

        defaultRemovedUserFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultRemovedUserFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultRemovedUserFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByOriginalIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where originalId equals to
        defaultRemovedUserFiltering("originalId.equals=" + DEFAULT_ORIGINAL_ID,
                "originalId.equals=" + UPDATED_ORIGINAL_ID);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByOriginalIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where originalId in
        defaultRemovedUserFiltering(
                "originalId.in=" + DEFAULT_ORIGINAL_ID + "," + UPDATED_ORIGINAL_ID,
                "originalId.in=" + UPDATED_ORIGINAL_ID);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByOriginalIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where originalId is not null
        defaultRemovedUserFiltering("originalId.specified=true", "originalId.specified=false");
    }

    @Test
    @Transactional
    void getAllRemovedUsersByOriginalIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where originalId is greater than or equal to
        defaultRemovedUserFiltering(
                "originalId.greaterThanOrEqual=" + DEFAULT_ORIGINAL_ID,
                "originalId.greaterThanOrEqual=" + UPDATED_ORIGINAL_ID);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByOriginalIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where originalId is less than or equal to
        defaultRemovedUserFiltering(
                "originalId.lessThanOrEqual=" + DEFAULT_ORIGINAL_ID,
                "originalId.lessThanOrEqual=" + SMALLER_ORIGINAL_ID);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByOriginalIdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where originalId is less than
        defaultRemovedUserFiltering("originalId.lessThan=" + UPDATED_ORIGINAL_ID,
                "originalId.lessThan=" + DEFAULT_ORIGINAL_ID);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByOriginalIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where originalId is greater than
        defaultRemovedUserFiltering("originalId.greaterThan=" + SMALLER_ORIGINAL_ID,
                "originalId.greaterThan=" + DEFAULT_ORIGINAL_ID);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where name equals to
        defaultRemovedUserFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where name in
        defaultRemovedUserFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where name is not null
        defaultRemovedUserFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllRemovedUsersByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where name contains
        defaultRemovedUserFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where name does not contain
        defaultRemovedUserFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByRoleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where role equals to
        defaultRemovedUserFiltering("role.equals=" + DEFAULT_ROLE, "role.equals=" + UPDATED_ROLE);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByRoleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where role in
        defaultRemovedUserFiltering("role.in=" + DEFAULT_ROLE + "," + UPDATED_ROLE, "role.in=" + UPDATED_ROLE);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByRoleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where role is not null
        defaultRemovedUserFiltering("role.specified=true", "role.specified=false");
    }

    @Test
    @Transactional
    void getAllRemovedUsersByWhatsappNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where whatsappNumber equals to
        defaultRemovedUserFiltering("whatsappNumber.equals=" + DEFAULT_WHATSAPP_NUMBER,
                "whatsappNumber.equals=" + UPDATED_WHATSAPP_NUMBER);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByWhatsappNumberIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where whatsappNumber in
        defaultRemovedUserFiltering(
                "whatsappNumber.in=" + DEFAULT_WHATSAPP_NUMBER + "," + UPDATED_WHATSAPP_NUMBER,
                "whatsappNumber.in=" + UPDATED_WHATSAPP_NUMBER);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByWhatsappNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where whatsappNumber is not null
        defaultRemovedUserFiltering("whatsappNumber.specified=true", "whatsappNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllRemovedUsersByWhatsappNumberContainsSomething() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where whatsappNumber contains
        defaultRemovedUserFiltering(
                "whatsappNumber.contains=" + DEFAULT_WHATSAPP_NUMBER,
                "whatsappNumber.contains=" + UPDATED_WHATSAPP_NUMBER);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByWhatsappNumberNotContainsSomething() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where whatsappNumber does not contain
        defaultRemovedUserFiltering(
                "whatsappNumber.doesNotContain=" + UPDATED_WHATSAPP_NUMBER,
                "whatsappNumber.doesNotContain=" + DEFAULT_WHATSAPP_NUMBER);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByPhoneNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where phoneNumber equals to
        defaultRemovedUserFiltering("phoneNumber.equals=" + DEFAULT_PHONE_NUMBER,
                "phoneNumber.equals=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByPhoneNumberIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where phoneNumber in
        defaultRemovedUserFiltering(
                "phoneNumber.in=" + DEFAULT_PHONE_NUMBER + "," + UPDATED_PHONE_NUMBER,
                "phoneNumber.in=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByPhoneNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where phoneNumber is not null
        defaultRemovedUserFiltering("phoneNumber.specified=true", "phoneNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllRemovedUsersByPhoneNumberContainsSomething() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where phoneNumber contains
        defaultRemovedUserFiltering("phoneNumber.contains=" + DEFAULT_PHONE_NUMBER,
                "phoneNumber.contains=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByPhoneNumberNotContainsSomething() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where phoneNumber does not contain
        defaultRemovedUserFiltering(
                "phoneNumber.doesNotContain=" + UPDATED_PHONE_NUMBER,
                "phoneNumber.doesNotContain=" + DEFAULT_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where address equals to
        defaultRemovedUserFiltering("address.equals=" + DEFAULT_ADDRESS, "address.equals=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByAddressIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where address in
        defaultRemovedUserFiltering("address.in=" + DEFAULT_ADDRESS + "," + UPDATED_ADDRESS,
                "address.in=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where address is not null
        defaultRemovedUserFiltering("address.specified=true", "address.specified=false");
    }

    @Test
    @Transactional
    void getAllRemovedUsersByAddressContainsSomething() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where address contains
        defaultRemovedUserFiltering("address.contains=" + DEFAULT_ADDRESS, "address.contains=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByAddressNotContainsSomething() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where address does not contain
        defaultRemovedUserFiltering("address.doesNotContain=" + UPDATED_ADDRESS,
                "address.doesNotContain=" + DEFAULT_ADDRESS);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByLocationLatIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where locationLat equals to
        defaultRemovedUserFiltering("locationLat.equals=" + DEFAULT_LOCATION_LAT,
                "locationLat.equals=" + UPDATED_LOCATION_LAT);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByLocationLatIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where locationLat in
        defaultRemovedUserFiltering(
                "locationLat.in=" + DEFAULT_LOCATION_LAT + "," + UPDATED_LOCATION_LAT,
                "locationLat.in=" + UPDATED_LOCATION_LAT);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByLocationLatIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where locationLat is not null
        defaultRemovedUserFiltering("locationLat.specified=true", "locationLat.specified=false");
    }

    @Test
    @Transactional
    void getAllRemovedUsersByLocationLatIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where locationLat is greater than or equal to
        defaultRemovedUserFiltering(
                "locationLat.greaterThanOrEqual=" + DEFAULT_LOCATION_LAT,
                "locationLat.greaterThanOrEqual=" + UPDATED_LOCATION_LAT);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByLocationLatIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where locationLat is less than or equal to
        defaultRemovedUserFiltering(
                "locationLat.lessThanOrEqual=" + DEFAULT_LOCATION_LAT,
                "locationLat.lessThanOrEqual=" + SMALLER_LOCATION_LAT);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByLocationLatIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where locationLat is less than
        defaultRemovedUserFiltering("locationLat.lessThan=" + UPDATED_LOCATION_LAT,
                "locationLat.lessThan=" + DEFAULT_LOCATION_LAT);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByLocationLatIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where locationLat is greater than
        defaultRemovedUserFiltering("locationLat.greaterThan=" + SMALLER_LOCATION_LAT,
                "locationLat.greaterThan=" + DEFAULT_LOCATION_LAT);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByLocationLonIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where locationLon equals to
        defaultRemovedUserFiltering("locationLon.equals=" + DEFAULT_LOCATION_LON,
                "locationLon.equals=" + UPDATED_LOCATION_LON);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByLocationLonIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where locationLon in
        defaultRemovedUserFiltering(
                "locationLon.in=" + DEFAULT_LOCATION_LON + "," + UPDATED_LOCATION_LON,
                "locationLon.in=" + UPDATED_LOCATION_LON);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByLocationLonIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where locationLon is not null
        defaultRemovedUserFiltering("locationLon.specified=true", "locationLon.specified=false");
    }

    @Test
    @Transactional
    void getAllRemovedUsersByLocationLonIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where locationLon is greater than or equal to
        defaultRemovedUserFiltering(
                "locationLon.greaterThanOrEqual=" + DEFAULT_LOCATION_LON,
                "locationLon.greaterThanOrEqual=" + UPDATED_LOCATION_LON);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByLocationLonIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where locationLon is less than or equal to
        defaultRemovedUserFiltering(
                "locationLon.lessThanOrEqual=" + DEFAULT_LOCATION_LON,
                "locationLon.lessThanOrEqual=" + SMALLER_LOCATION_LON);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByLocationLonIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where locationLon is less than
        defaultRemovedUserFiltering("locationLon.lessThan=" + UPDATED_LOCATION_LON,
                "locationLon.lessThan=" + DEFAULT_LOCATION_LON);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByLocationLonIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where locationLon is greater than
        defaultRemovedUserFiltering("locationLon.greaterThan=" + SMALLER_LOCATION_LON,
                "locationLon.greaterThan=" + DEFAULT_LOCATION_LON);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByJoinedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where joinedAt equals to
        defaultRemovedUserFiltering("joinedAt.equals=" + DEFAULT_JOINED_AT, "joinedAt.equals=" + UPDATED_JOINED_AT);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByJoinedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where joinedAt in
        defaultRemovedUserFiltering("joinedAt.in=" + DEFAULT_JOINED_AT + "," + UPDATED_JOINED_AT,
                "joinedAt.in=" + UPDATED_JOINED_AT);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByJoinedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where joinedAt is not null
        defaultRemovedUserFiltering("joinedAt.specified=true", "joinedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllRemovedUsersByRemovedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where removedAt equals to
        defaultRemovedUserFiltering("removedAt.equals=" + DEFAULT_REMOVED_AT, "removedAt.equals=" + UPDATED_REMOVED_AT);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByRemovedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where removedAt in
        defaultRemovedUserFiltering("removedAt.in=" + DEFAULT_REMOVED_AT + "," + UPDATED_REMOVED_AT,
                "removedAt.in=" + UPDATED_REMOVED_AT);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByRemovedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where removedAt is not null
        defaultRemovedUserFiltering("removedAt.specified=true", "removedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllRemovedUsersByReasonForRemovalIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where reasonForRemoval equals to
        defaultRemovedUserFiltering(
                "reasonForRemoval.equals=" + DEFAULT_REASON_FOR_REMOVAL,
                "reasonForRemoval.equals=" + UPDATED_REASON_FOR_REMOVAL);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByReasonForRemovalIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where reasonForRemoval in
        defaultRemovedUserFiltering(
                "reasonForRemoval.in=" + DEFAULT_REASON_FOR_REMOVAL + "," + UPDATED_REASON_FOR_REMOVAL,
                "reasonForRemoval.in=" + UPDATED_REASON_FOR_REMOVAL);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByReasonForRemovalIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where reasonForRemoval is not null
        defaultRemovedUserFiltering("reasonForRemoval.specified=true", "reasonForRemoval.specified=false");
    }

    @Test
    @Transactional
    void getAllRemovedUsersByReasonForRemovalContainsSomething() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where reasonForRemoval contains
        defaultRemovedUserFiltering(
                "reasonForRemoval.contains=" + DEFAULT_REASON_FOR_REMOVAL,
                "reasonForRemoval.contains=" + UPDATED_REASON_FOR_REMOVAL);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByReasonForRemovalNotContainsSomething() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where reasonForRemoval does not contain
        defaultRemovedUserFiltering(
                "reasonForRemoval.doesNotContain=" + UPDATED_REASON_FOR_REMOVAL,
                "reasonForRemoval.doesNotContain=" + DEFAULT_REASON_FOR_REMOVAL);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where status equals to
        defaultRemovedUserFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where status in
        defaultRemovedUserFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS,
                "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where status is not null
        defaultRemovedUserFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllRemovedUsersByOrderHistoryIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where orderHistoryId equals to
        defaultRemovedUserFiltering(
                "orderHistoryId.equals=" + DEFAULT_ORDER_HISTORY_ID,
                "orderHistoryId.equals=" + UPDATED_ORDER_HISTORY_ID);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByOrderHistoryIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where orderHistoryId in
        defaultRemovedUserFiltering(
                "orderHistoryId.in=" + DEFAULT_ORDER_HISTORY_ID + "," + UPDATED_ORDER_HISTORY_ID,
                "orderHistoryId.in=" + UPDATED_ORDER_HISTORY_ID);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByOrderHistoryIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where orderHistoryId is not null
        defaultRemovedUserFiltering("orderHistoryId.specified=true", "orderHistoryId.specified=false");
    }

    @Test
    @Transactional
    void getAllRemovedUsersByOrderHistoryIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where orderHistoryId is greater than or equal to
        defaultRemovedUserFiltering(
                "orderHistoryId.greaterThanOrEqual=" + DEFAULT_ORDER_HISTORY_ID,
                "orderHistoryId.greaterThanOrEqual=" + UPDATED_ORDER_HISTORY_ID);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByOrderHistoryIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where orderHistoryId is less than or equal to
        defaultRemovedUserFiltering(
                "orderHistoryId.lessThanOrEqual=" + DEFAULT_ORDER_HISTORY_ID,
                "orderHistoryId.lessThanOrEqual=" + SMALLER_ORDER_HISTORY_ID);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByOrderHistoryIdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where orderHistoryId is less than
        defaultRemovedUserFiltering(
                "orderHistoryId.lessThan=" + UPDATED_ORDER_HISTORY_ID,
                "orderHistoryId.lessThan=" + DEFAULT_ORDER_HISTORY_ID);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByOrderHistoryIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where orderHistoryId is greater than
        defaultRemovedUserFiltering(
                "orderHistoryId.greaterThan=" + SMALLER_ORDER_HISTORY_ID,
                "orderHistoryId.greaterThan=" + DEFAULT_ORDER_HISTORY_ID);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByDistanceFromBusinessKmIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where distanceFromBusinessKm equals to
        defaultRemovedUserFiltering(
                "distanceFromBusinessKm.equals=" + DEFAULT_DISTANCE_FROM_BUSINESS_KM,
                "distanceFromBusinessKm.equals=" + UPDATED_DISTANCE_FROM_BUSINESS_KM);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByDistanceFromBusinessKmIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where distanceFromBusinessKm in
        defaultRemovedUserFiltering(
                "distanceFromBusinessKm.in=" + DEFAULT_DISTANCE_FROM_BUSINESS_KM + ","
                        + UPDATED_DISTANCE_FROM_BUSINESS_KM,
                "distanceFromBusinessKm.in=" + UPDATED_DISTANCE_FROM_BUSINESS_KM);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByDistanceFromBusinessKmIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where distanceFromBusinessKm is not null
        defaultRemovedUserFiltering("distanceFromBusinessKm.specified=true", "distanceFromBusinessKm.specified=false");
    }

    @Test
    @Transactional
    void getAllRemovedUsersByDistanceFromBusinessKmIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where distanceFromBusinessKm is greater than or
        // equal to
        defaultRemovedUserFiltering(
                "distanceFromBusinessKm.greaterThanOrEqual=" + DEFAULT_DISTANCE_FROM_BUSINESS_KM,
                "distanceFromBusinessKm.greaterThanOrEqual=" + UPDATED_DISTANCE_FROM_BUSINESS_KM);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByDistanceFromBusinessKmIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where distanceFromBusinessKm is less than or
        // equal to
        defaultRemovedUserFiltering(
                "distanceFromBusinessKm.lessThanOrEqual=" + DEFAULT_DISTANCE_FROM_BUSINESS_KM,
                "distanceFromBusinessKm.lessThanOrEqual=" + SMALLER_DISTANCE_FROM_BUSINESS_KM);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByDistanceFromBusinessKmIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where distanceFromBusinessKm is less than
        defaultRemovedUserFiltering(
                "distanceFromBusinessKm.lessThan=" + UPDATED_DISTANCE_FROM_BUSINESS_KM,
                "distanceFromBusinessKm.lessThan=" + DEFAULT_DISTANCE_FROM_BUSINESS_KM);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByDistanceFromBusinessKmIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where distanceFromBusinessKm is greater than
        defaultRemovedUserFiltering(
                "distanceFromBusinessKm.greaterThan=" + SMALLER_DISTANCE_FROM_BUSINESS_KM,
                "distanceFromBusinessKm.greaterThan=" + DEFAULT_DISTANCE_FROM_BUSINESS_KM);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByIsPincodeValidIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where isPincodeValid equals to
        defaultRemovedUserFiltering(
                "isPincodeValid.equals=" + DEFAULT_IS_PINCODE_VALID,
                "isPincodeValid.equals=" + UPDATED_IS_PINCODE_VALID);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByIsPincodeValidIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where isPincodeValid in
        defaultRemovedUserFiltering(
                "isPincodeValid.in=" + DEFAULT_IS_PINCODE_VALID + "," + UPDATED_IS_PINCODE_VALID,
                "isPincodeValid.in=" + UPDATED_IS_PINCODE_VALID);
    }

    @Test
    @Transactional
    void getAllRemovedUsersByIsPincodeValidIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        // Get all the removedUserList where isPincodeValid is not null
        defaultRemovedUserFiltering("isPincodeValid.specified=true", "isPincodeValid.specified=false");
    }

    private void defaultRemovedUserFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultRemovedUserShouldBeFound(shouldBeFound);
        defaultRemovedUserShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRemovedUserShouldBeFound(String filter) throws Exception {
        restRemovedUserMockMvc
                .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(removedUser.getId().intValue())))
                .andExpect(jsonPath("$.[*].originalId").value(hasItem(DEFAULT_ORIGINAL_ID.intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
                .andExpect(jsonPath("$.[*].role").value(hasItem(DEFAULT_ROLE.toString())))
                .andExpect(jsonPath("$.[*].whatsappNumber").value(hasItem(DEFAULT_WHATSAPP_NUMBER)))
                .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
                .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
                .andExpect(jsonPath("$.[*].locationLat").value(hasItem(DEFAULT_LOCATION_LAT)))
                .andExpect(jsonPath("$.[*].locationLon").value(hasItem(DEFAULT_LOCATION_LON)))
                .andExpect(jsonPath("$.[*].joinedAt").value(hasItem(DEFAULT_JOINED_AT.toString())))
                .andExpect(jsonPath("$.[*].removedAt").value(hasItem(DEFAULT_REMOVED_AT.toString())))
                .andExpect(jsonPath("$.[*].reasonForRemoval").value(hasItem(DEFAULT_REASON_FOR_REMOVAL)))
                .andExpect(jsonPath("$.[*].lastSessionData").value(hasItem(DEFAULT_LAST_SESSION_DATA)))
                .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
                .andExpect(jsonPath("$.[*].orderHistoryId").value(hasItem(DEFAULT_ORDER_HISTORY_ID.intValue())))
                .andExpect(jsonPath("$.[*].distanceFromBusinessKm").value(hasItem(DEFAULT_DISTANCE_FROM_BUSINESS_KM)))
                .andExpect(jsonPath("$.[*].isPincodeValid").value(hasItem(DEFAULT_IS_PINCODE_VALID)));

        // Check, that the count call also returns 1
        restRemovedUserMockMvc
                .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultRemovedUserShouldNotBeFound(String filter) throws Exception {
        restRemovedUserMockMvc
                .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restRemovedUserMockMvc
                .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingRemovedUser() throws Exception {
        // Get the removedUser
        restRemovedUserMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRemovedUser() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the removedUser
        RemovedUser updatedRemovedUser = removedUserRepository.findById(removedUser.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedRemovedUser are not
        // directly saved in db
        em.detach(updatedRemovedUser);
        updatedRemovedUser
                .originalId(UPDATED_ORIGINAL_ID)
                .name(UPDATED_NAME)
                .role(UPDATED_ROLE)
                .whatsappNumber(UPDATED_WHATSAPP_NUMBER)
                .phoneNumber(UPDATED_PHONE_NUMBER)
                .address(UPDATED_ADDRESS)
                .locationLat(UPDATED_LOCATION_LAT)
                .locationLon(UPDATED_LOCATION_LON)
                .joinedAt(UPDATED_JOINED_AT)
                .removedAt(UPDATED_REMOVED_AT)
                .reasonForRemoval(UPDATED_REASON_FOR_REMOVAL)
                .lastSessionData(UPDATED_LAST_SESSION_DATA)
                .status(UPDATED_STATUS)
                .orderHistoryId(UPDATED_ORDER_HISTORY_ID)
                .distanceFromBusinessKm(UPDATED_DISTANCE_FROM_BUSINESS_KM)
                .isPincodeValid(UPDATED_IS_PINCODE_VALID);
        RemovedUserDTO removedUserDTO = removedUserMapper.toDto(updatedRemovedUser);

        restRemovedUserMockMvc
                .perform(
                        put(ENTITY_API_URL_ID, removedUserDTO.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsBytes(removedUserDTO)))
                .andExpect(status().isOk());

        // Validate the RemovedUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRemovedUserToMatchAllProperties(updatedRemovedUser);
    }

    @Test
    @Transactional
    void putNonExistingRemovedUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        removedUser.setId(longCount.incrementAndGet());

        // Create the RemovedUser
        RemovedUserDTO removedUserDTO = removedUserMapper.toDto(removedUser);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRemovedUserMockMvc
                .perform(
                        put(ENTITY_API_URL_ID, removedUserDTO.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsBytes(removedUserDTO)))
                .andExpect(status().isBadRequest());

        // Validate the RemovedUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRemovedUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        removedUser.setId(longCount.incrementAndGet());

        // Create the RemovedUser
        RemovedUserDTO removedUserDTO = removedUserMapper.toDto(removedUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRemovedUserMockMvc
                .perform(
                        put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsBytes(removedUserDTO)))
                .andExpect(status().isBadRequest());

        // Validate the RemovedUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRemovedUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        removedUser.setId(longCount.incrementAndGet());

        // Create the RemovedUser
        RemovedUserDTO removedUserDTO = removedUserMapper.toDto(removedUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRemovedUserMockMvc
                .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(removedUserDTO)))
                .andExpect(status().isMethodNotAllowed());

        // Validate the RemovedUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRemovedUserWithPatch() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the removedUser using partial update
        RemovedUser partialUpdatedRemovedUser = new RemovedUser();
        partialUpdatedRemovedUser.setId(removedUser.getId());

        partialUpdatedRemovedUser
                .name(UPDATED_NAME)
                .role(UPDATED_ROLE)
                .phoneNumber(UPDATED_PHONE_NUMBER)
                .locationLat(UPDATED_LOCATION_LAT)
                .locationLon(UPDATED_LOCATION_LON)
                .reasonForRemoval(UPDATED_REASON_FOR_REMOVAL)
                .orderHistoryId(UPDATED_ORDER_HISTORY_ID)
                .distanceFromBusinessKm(UPDATED_DISTANCE_FROM_BUSINESS_KM)
                .isPincodeValid(UPDATED_IS_PINCODE_VALID);

        restRemovedUserMockMvc
                .perform(
                        patch(ENTITY_API_URL_ID, partialUpdatedRemovedUser.getId())
                                .contentType("application/merge-patch+json")
                                .content(om.writeValueAsBytes(partialUpdatedRemovedUser)))
                .andExpect(status().isOk());

        // Validate the RemovedUser in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRemovedUserUpdatableFieldsEquals(
                createUpdateProxyForBean(partialUpdatedRemovedUser, removedUser),
                getPersistedRemovedUser(removedUser));
    }

    @Test
    @Transactional
    void fullUpdateRemovedUserWithPatch() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the removedUser using partial update
        RemovedUser partialUpdatedRemovedUser = new RemovedUser();
        partialUpdatedRemovedUser.setId(removedUser.getId());

        partialUpdatedRemovedUser
                .originalId(UPDATED_ORIGINAL_ID)
                .name(UPDATED_NAME)
                .role(UPDATED_ROLE)
                .whatsappNumber(UPDATED_WHATSAPP_NUMBER)
                .phoneNumber(UPDATED_PHONE_NUMBER)
                .address(UPDATED_ADDRESS)
                .locationLat(UPDATED_LOCATION_LAT)
                .locationLon(UPDATED_LOCATION_LON)
                .joinedAt(UPDATED_JOINED_AT)
                .removedAt(UPDATED_REMOVED_AT)
                .reasonForRemoval(UPDATED_REASON_FOR_REMOVAL)
                .lastSessionData(UPDATED_LAST_SESSION_DATA)
                .status(UPDATED_STATUS)
                .orderHistoryId(UPDATED_ORDER_HISTORY_ID)
                .distanceFromBusinessKm(UPDATED_DISTANCE_FROM_BUSINESS_KM)
                .isPincodeValid(UPDATED_IS_PINCODE_VALID);

        restRemovedUserMockMvc
                .perform(
                        patch(ENTITY_API_URL_ID, partialUpdatedRemovedUser.getId())
                                .contentType("application/merge-patch+json")
                                .content(om.writeValueAsBytes(partialUpdatedRemovedUser)))
                .andExpect(status().isOk());

        // Validate the RemovedUser in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRemovedUserUpdatableFieldsEquals(partialUpdatedRemovedUser,
                getPersistedRemovedUser(partialUpdatedRemovedUser));
    }

    @Test
    @Transactional
    void patchNonExistingRemovedUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        removedUser.setId(longCount.incrementAndGet());

        // Create the RemovedUser
        RemovedUserDTO removedUserDTO = removedUserMapper.toDto(removedUser);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRemovedUserMockMvc
                .perform(
                        patch(ENTITY_API_URL_ID, removedUserDTO.getId())
                                .contentType("application/merge-patch+json")
                                .content(om.writeValueAsBytes(removedUserDTO)))
                .andExpect(status().isBadRequest());

        // Validate the RemovedUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRemovedUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        removedUser.setId(longCount.incrementAndGet());

        // Create the RemovedUser
        RemovedUserDTO removedUserDTO = removedUserMapper.toDto(removedUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRemovedUserMockMvc
                .perform(
                        patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                                .contentType("application/merge-patch+json")
                                .content(om.writeValueAsBytes(removedUserDTO)))
                .andExpect(status().isBadRequest());

        // Validate the RemovedUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRemovedUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        removedUser.setId(longCount.incrementAndGet());

        // Create the RemovedUser
        RemovedUserDTO removedUserDTO = removedUserMapper.toDto(removedUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRemovedUserMockMvc
                .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json")
                        .content(om.writeValueAsBytes(removedUserDTO)))
                .andExpect(status().isMethodNotAllowed());

        // Validate the RemovedUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRemovedUserWithHistory() throws Exception {
        // Initialize the database
        // Create and save a RemovedOrderSummary
        RemovedOrderSummary summary = new RemovedOrderSummary();
        summary.setUserName("Test User");
        summary = removedOrderSummaryRepository.saveAndFlush(summary);

        // Link it to RemovedUser
        removedUser.setOrderHistoryId(summary.getId());
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        long databaseSizeBeforeDelete = getRepositoryCount();
        long historySizeBeforeDelete = removedOrderSummaryRepository.count();

        // Delete the removedUser
        restRemovedUserMockMvc
                .perform(delete(ENTITY_API_URL_ID, removedUser.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);

        // Validate the history is also deleted
        assertThat(removedOrderSummaryRepository.count()).isEqualTo(historySizeBeforeDelete - 1);
        assertThat(removedOrderSummaryRepository.findById(summary.getId())).isEmpty();
    }

    @Test
    @Transactional
    void deleteRemovedUser() throws Exception {
        // Initialize the database
        insertedRemovedUser = removedUserRepository.saveAndFlush(removedUser);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the removedUser
        restRemovedUserMockMvc
                .perform(delete(ENTITY_API_URL_ID, removedUser.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return removedUserRepository.count();
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

    protected RemovedUser getPersistedRemovedUser(RemovedUser removedUser) {
        return removedUserRepository.findById(removedUser.getId()).orElseThrow();
    }

    protected void assertPersistedRemovedUserToMatchAllProperties(RemovedUser expectedRemovedUser) {
        assertRemovedUserAllPropertiesEquals(expectedRemovedUser, getPersistedRemovedUser(expectedRemovedUser));
    }

    protected void assertPersistedRemovedUserToMatchUpdatableProperties(RemovedUser expectedRemovedUser) {
        assertRemovedUserAllUpdatablePropertiesEquals(expectedRemovedUser,
                getPersistedRemovedUser(expectedRemovedUser));
    }
}
