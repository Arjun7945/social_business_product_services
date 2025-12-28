package com.aps.web.rest;

import static com.aps.domain.RemovedOrderSummaryAsserts.*;
import static com.aps.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.aps.IntegrationTest;
import com.aps.domain.RemovedOrderSummary;
import com.aps.domain.enumeration.UserRole;
import com.aps.repository.RemovedOrderSummaryRepository;
import com.aps.service.dto.RemovedOrderSummaryDTO;
import com.aps.service.mapper.RemovedOrderSummaryMapper;
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
 * Integration tests for the {@link RemovedOrderSummaryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RemovedOrderSummaryResourceIT {

    private static final Long DEFAULT_USER_ORIGINAL_ID = 1L;
    private static final Long UPDATED_USER_ORIGINAL_ID = 2L;
    private static final Long SMALLER_USER_ORIGINAL_ID = 1L - 1L;

    private static final String DEFAULT_USER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_USER_NAME = "BBBBBBBBBB";

    private static final UserRole DEFAULT_USER_ROLE = UserRole.CUSTOMER;
    private static final UserRole UPDATED_USER_ROLE = UserRole.EXECUTIVE;

    private static final Integer DEFAULT_TOTAL_ORDERS = 1;
    private static final Integer UPDATED_TOTAL_ORDERS = 2;
    private static final Integer SMALLER_TOTAL_ORDERS = 1 - 1;

    private static final Double DEFAULT_TOTAL_AMOUNT = 1D;
    private static final Double UPDATED_TOTAL_AMOUNT = 2D;
    private static final Double SMALLER_TOTAL_AMOUNT = 1D - 1D;

    private static final Instant DEFAULT_FIRST_INTERACTION_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FIRST_INTERACTION_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_LAST_INTERACTION_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_INTERACTION_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_REMOVED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_REMOVED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/removed-order-summaries";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RemovedOrderSummaryRepository removedOrderSummaryRepository;

    @Autowired
    private RemovedOrderSummaryMapper removedOrderSummaryMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRemovedOrderSummaryMockMvc;

    private RemovedOrderSummary removedOrderSummary;

    private RemovedOrderSummary insertedRemovedOrderSummary;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RemovedOrderSummary createEntity() {
        return new RemovedOrderSummary()
            .userOriginalId(DEFAULT_USER_ORIGINAL_ID)
            .userName(DEFAULT_USER_NAME)
            .userRole(DEFAULT_USER_ROLE)
            .totalOrders(DEFAULT_TOTAL_ORDERS)
            .totalAmount(DEFAULT_TOTAL_AMOUNT)
            .firstInteractionAt(DEFAULT_FIRST_INTERACTION_AT)
            .lastInteractionAt(DEFAULT_LAST_INTERACTION_AT)
            .removedAt(DEFAULT_REMOVED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RemovedOrderSummary createUpdatedEntity() {
        return new RemovedOrderSummary()
            .userOriginalId(UPDATED_USER_ORIGINAL_ID)
            .userName(UPDATED_USER_NAME)
            .userRole(UPDATED_USER_ROLE)
            .totalOrders(UPDATED_TOTAL_ORDERS)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .firstInteractionAt(UPDATED_FIRST_INTERACTION_AT)
            .lastInteractionAt(UPDATED_LAST_INTERACTION_AT)
            .removedAt(UPDATED_REMOVED_AT);
    }

    @BeforeEach
    public void initTest() {
        removedOrderSummary = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedRemovedOrderSummary != null) {
            removedOrderSummaryRepository.delete(insertedRemovedOrderSummary);
            insertedRemovedOrderSummary = null;
        }
    }

    @Test
    @Transactional
    void createRemovedOrderSummary() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the RemovedOrderSummary
        RemovedOrderSummaryDTO removedOrderSummaryDTO = removedOrderSummaryMapper.toDto(removedOrderSummary);
        var returnedRemovedOrderSummaryDTO = om.readValue(
            restRemovedOrderSummaryMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(removedOrderSummaryDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            RemovedOrderSummaryDTO.class
        );

        // Validate the RemovedOrderSummary in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedRemovedOrderSummary = removedOrderSummaryMapper.toEntity(returnedRemovedOrderSummaryDTO);
        assertRemovedOrderSummaryUpdatableFieldsEquals(
            returnedRemovedOrderSummary,
            getPersistedRemovedOrderSummary(returnedRemovedOrderSummary)
        );

        insertedRemovedOrderSummary = returnedRemovedOrderSummary;
    }

    @Test
    @Transactional
    void createRemovedOrderSummaryWithExistingId() throws Exception {
        // Create the RemovedOrderSummary with an existing ID
        removedOrderSummary.setId(1L);
        RemovedOrderSummaryDTO removedOrderSummaryDTO = removedOrderSummaryMapper.toDto(removedOrderSummary);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRemovedOrderSummaryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(removedOrderSummaryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the RemovedOrderSummary in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllRemovedOrderSummaries() throws Exception {
        // Initialize the database
        insertedRemovedOrderSummary = removedOrderSummaryRepository.saveAndFlush(removedOrderSummary);

        // Get all the removedOrderSummaryList
        restRemovedOrderSummaryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(removedOrderSummary.getId().intValue())))
            .andExpect(jsonPath("$.[*].userOriginalId").value(hasItem(DEFAULT_USER_ORIGINAL_ID.intValue())))
            .andExpect(jsonPath("$.[*].userName").value(hasItem(DEFAULT_USER_NAME)))
            .andExpect(jsonPath("$.[*].userRole").value(hasItem(DEFAULT_USER_ROLE.toString())))
            .andExpect(jsonPath("$.[*].totalOrders").value(hasItem(DEFAULT_TOTAL_ORDERS)))
            .andExpect(jsonPath("$.[*].totalAmount").value(hasItem(DEFAULT_TOTAL_AMOUNT)))
            .andExpect(jsonPath("$.[*].firstInteractionAt").value(hasItem(DEFAULT_FIRST_INTERACTION_AT.toString())))
            .andExpect(jsonPath("$.[*].lastInteractionAt").value(hasItem(DEFAULT_LAST_INTERACTION_AT.toString())))
            .andExpect(jsonPath("$.[*].removedAt").value(hasItem(DEFAULT_REMOVED_AT.toString())));
    }

    @Test
    @Transactional
    void getRemovedOrderSummary() throws Exception {
        // Initialize the database
        insertedRemovedOrderSummary = removedOrderSummaryRepository.saveAndFlush(removedOrderSummary);

        // Get the removedOrderSummary
        restRemovedOrderSummaryMockMvc
            .perform(get(ENTITY_API_URL_ID, removedOrderSummary.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(removedOrderSummary.getId().intValue()))
            .andExpect(jsonPath("$.userOriginalId").value(DEFAULT_USER_ORIGINAL_ID.intValue()))
            .andExpect(jsonPath("$.userName").value(DEFAULT_USER_NAME))
            .andExpect(jsonPath("$.userRole").value(DEFAULT_USER_ROLE.toString()))
            .andExpect(jsonPath("$.totalOrders").value(DEFAULT_TOTAL_ORDERS))
            .andExpect(jsonPath("$.totalAmount").value(DEFAULT_TOTAL_AMOUNT))
            .andExpect(jsonPath("$.firstInteractionAt").value(DEFAULT_FIRST_INTERACTION_AT.toString()))
            .andExpect(jsonPath("$.lastInteractionAt").value(DEFAULT_LAST_INTERACTION_AT.toString()))
            .andExpect(jsonPath("$.removedAt").value(DEFAULT_REMOVED_AT.toString()));
    }

    @Test
    @Transactional
    void getRemovedOrderSummariesByIdFiltering() throws Exception {
        // Initialize the database
        insertedRemovedOrderSummary = removedOrderSummaryRepository.saveAndFlush(removedOrderSummary);

        Long id = removedOrderSummary.getId();

        defaultRemovedOrderSummaryFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultRemovedOrderSummaryFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultRemovedOrderSummaryFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllRemovedOrderSummariesByUserOriginalIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRemovedOrderSummary = removedOrderSummaryRepository.saveAndFlush(removedOrderSummary);

        // Get all the removedOrderSummaryList where userOriginalId equals to
        defaultRemovedOrderSummaryFiltering(
            "userOriginalId.equals=" + DEFAULT_USER_ORIGINAL_ID,
            "userOriginalId.equals=" + UPDATED_USER_ORIGINAL_ID
        );
    }

    @Test
    @Transactional
    void getAllRemovedOrderSummariesByUserOriginalIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRemovedOrderSummary = removedOrderSummaryRepository.saveAndFlush(removedOrderSummary);

        // Get all the removedOrderSummaryList where userOriginalId in
        defaultRemovedOrderSummaryFiltering(
            "userOriginalId.in=" + DEFAULT_USER_ORIGINAL_ID + "," + UPDATED_USER_ORIGINAL_ID,
            "userOriginalId.in=" + UPDATED_USER_ORIGINAL_ID
        );
    }

    @Test
    @Transactional
    void getAllRemovedOrderSummariesByUserOriginalIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRemovedOrderSummary = removedOrderSummaryRepository.saveAndFlush(removedOrderSummary);

        // Get all the removedOrderSummaryList where userOriginalId is not null
        defaultRemovedOrderSummaryFiltering("userOriginalId.specified=true", "userOriginalId.specified=false");
    }

    @Test
    @Transactional
    void getAllRemovedOrderSummariesByUserOriginalIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRemovedOrderSummary = removedOrderSummaryRepository.saveAndFlush(removedOrderSummary);

        // Get all the removedOrderSummaryList where userOriginalId is greater than or equal to
        defaultRemovedOrderSummaryFiltering(
            "userOriginalId.greaterThanOrEqual=" + DEFAULT_USER_ORIGINAL_ID,
            "userOriginalId.greaterThanOrEqual=" + UPDATED_USER_ORIGINAL_ID
        );
    }

    @Test
    @Transactional
    void getAllRemovedOrderSummariesByUserOriginalIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRemovedOrderSummary = removedOrderSummaryRepository.saveAndFlush(removedOrderSummary);

        // Get all the removedOrderSummaryList where userOriginalId is less than or equal to
        defaultRemovedOrderSummaryFiltering(
            "userOriginalId.lessThanOrEqual=" + DEFAULT_USER_ORIGINAL_ID,
            "userOriginalId.lessThanOrEqual=" + SMALLER_USER_ORIGINAL_ID
        );
    }

    @Test
    @Transactional
    void getAllRemovedOrderSummariesByUserOriginalIdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedRemovedOrderSummary = removedOrderSummaryRepository.saveAndFlush(removedOrderSummary);

        // Get all the removedOrderSummaryList where userOriginalId is less than
        defaultRemovedOrderSummaryFiltering(
            "userOriginalId.lessThan=" + UPDATED_USER_ORIGINAL_ID,
            "userOriginalId.lessThan=" + DEFAULT_USER_ORIGINAL_ID
        );
    }

    @Test
    @Transactional
    void getAllRemovedOrderSummariesByUserOriginalIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedRemovedOrderSummary = removedOrderSummaryRepository.saveAndFlush(removedOrderSummary);

        // Get all the removedOrderSummaryList where userOriginalId is greater than
        defaultRemovedOrderSummaryFiltering(
            "userOriginalId.greaterThan=" + SMALLER_USER_ORIGINAL_ID,
            "userOriginalId.greaterThan=" + DEFAULT_USER_ORIGINAL_ID
        );
    }

    @Test
    @Transactional
    void getAllRemovedOrderSummariesByUserNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRemovedOrderSummary = removedOrderSummaryRepository.saveAndFlush(removedOrderSummary);

        // Get all the removedOrderSummaryList where userName equals to
        defaultRemovedOrderSummaryFiltering("userName.equals=" + DEFAULT_USER_NAME, "userName.equals=" + UPDATED_USER_NAME);
    }

    @Test
    @Transactional
    void getAllRemovedOrderSummariesByUserNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRemovedOrderSummary = removedOrderSummaryRepository.saveAndFlush(removedOrderSummary);

        // Get all the removedOrderSummaryList where userName in
        defaultRemovedOrderSummaryFiltering(
            "userName.in=" + DEFAULT_USER_NAME + "," + UPDATED_USER_NAME,
            "userName.in=" + UPDATED_USER_NAME
        );
    }

    @Test
    @Transactional
    void getAllRemovedOrderSummariesByUserNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRemovedOrderSummary = removedOrderSummaryRepository.saveAndFlush(removedOrderSummary);

        // Get all the removedOrderSummaryList where userName is not null
        defaultRemovedOrderSummaryFiltering("userName.specified=true", "userName.specified=false");
    }

    @Test
    @Transactional
    void getAllRemovedOrderSummariesByUserNameContainsSomething() throws Exception {
        // Initialize the database
        insertedRemovedOrderSummary = removedOrderSummaryRepository.saveAndFlush(removedOrderSummary);

        // Get all the removedOrderSummaryList where userName contains
        defaultRemovedOrderSummaryFiltering("userName.contains=" + DEFAULT_USER_NAME, "userName.contains=" + UPDATED_USER_NAME);
    }

    @Test
    @Transactional
    void getAllRemovedOrderSummariesByUserNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedRemovedOrderSummary = removedOrderSummaryRepository.saveAndFlush(removedOrderSummary);

        // Get all the removedOrderSummaryList where userName does not contain
        defaultRemovedOrderSummaryFiltering("userName.doesNotContain=" + UPDATED_USER_NAME, "userName.doesNotContain=" + DEFAULT_USER_NAME);
    }

    @Test
    @Transactional
    void getAllRemovedOrderSummariesByUserRoleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRemovedOrderSummary = removedOrderSummaryRepository.saveAndFlush(removedOrderSummary);

        // Get all the removedOrderSummaryList where userRole equals to
        defaultRemovedOrderSummaryFiltering("userRole.equals=" + DEFAULT_USER_ROLE, "userRole.equals=" + UPDATED_USER_ROLE);
    }

    @Test
    @Transactional
    void getAllRemovedOrderSummariesByUserRoleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRemovedOrderSummary = removedOrderSummaryRepository.saveAndFlush(removedOrderSummary);

        // Get all the removedOrderSummaryList where userRole in
        defaultRemovedOrderSummaryFiltering(
            "userRole.in=" + DEFAULT_USER_ROLE + "," + UPDATED_USER_ROLE,
            "userRole.in=" + UPDATED_USER_ROLE
        );
    }

    @Test
    @Transactional
    void getAllRemovedOrderSummariesByUserRoleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRemovedOrderSummary = removedOrderSummaryRepository.saveAndFlush(removedOrderSummary);

        // Get all the removedOrderSummaryList where userRole is not null
        defaultRemovedOrderSummaryFiltering("userRole.specified=true", "userRole.specified=false");
    }

    @Test
    @Transactional
    void getAllRemovedOrderSummariesByTotalOrdersIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRemovedOrderSummary = removedOrderSummaryRepository.saveAndFlush(removedOrderSummary);

        // Get all the removedOrderSummaryList where totalOrders equals to
        defaultRemovedOrderSummaryFiltering("totalOrders.equals=" + DEFAULT_TOTAL_ORDERS, "totalOrders.equals=" + UPDATED_TOTAL_ORDERS);
    }

    @Test
    @Transactional
    void getAllRemovedOrderSummariesByTotalOrdersIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRemovedOrderSummary = removedOrderSummaryRepository.saveAndFlush(removedOrderSummary);

        // Get all the removedOrderSummaryList where totalOrders in
        defaultRemovedOrderSummaryFiltering(
            "totalOrders.in=" + DEFAULT_TOTAL_ORDERS + "," + UPDATED_TOTAL_ORDERS,
            "totalOrders.in=" + UPDATED_TOTAL_ORDERS
        );
    }

    @Test
    @Transactional
    void getAllRemovedOrderSummariesByTotalOrdersIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRemovedOrderSummary = removedOrderSummaryRepository.saveAndFlush(removedOrderSummary);

        // Get all the removedOrderSummaryList where totalOrders is not null
        defaultRemovedOrderSummaryFiltering("totalOrders.specified=true", "totalOrders.specified=false");
    }

    @Test
    @Transactional
    void getAllRemovedOrderSummariesByTotalOrdersIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRemovedOrderSummary = removedOrderSummaryRepository.saveAndFlush(removedOrderSummary);

        // Get all the removedOrderSummaryList where totalOrders is greater than or equal to
        defaultRemovedOrderSummaryFiltering(
            "totalOrders.greaterThanOrEqual=" + DEFAULT_TOTAL_ORDERS,
            "totalOrders.greaterThanOrEqual=" + UPDATED_TOTAL_ORDERS
        );
    }

    @Test
    @Transactional
    void getAllRemovedOrderSummariesByTotalOrdersIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRemovedOrderSummary = removedOrderSummaryRepository.saveAndFlush(removedOrderSummary);

        // Get all the removedOrderSummaryList where totalOrders is less than or equal to
        defaultRemovedOrderSummaryFiltering(
            "totalOrders.lessThanOrEqual=" + DEFAULT_TOTAL_ORDERS,
            "totalOrders.lessThanOrEqual=" + SMALLER_TOTAL_ORDERS
        );
    }

    @Test
    @Transactional
    void getAllRemovedOrderSummariesByTotalOrdersIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedRemovedOrderSummary = removedOrderSummaryRepository.saveAndFlush(removedOrderSummary);

        // Get all the removedOrderSummaryList where totalOrders is less than
        defaultRemovedOrderSummaryFiltering("totalOrders.lessThan=" + UPDATED_TOTAL_ORDERS, "totalOrders.lessThan=" + DEFAULT_TOTAL_ORDERS);
    }

    @Test
    @Transactional
    void getAllRemovedOrderSummariesByTotalOrdersIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedRemovedOrderSummary = removedOrderSummaryRepository.saveAndFlush(removedOrderSummary);

        // Get all the removedOrderSummaryList where totalOrders is greater than
        defaultRemovedOrderSummaryFiltering(
            "totalOrders.greaterThan=" + SMALLER_TOTAL_ORDERS,
            "totalOrders.greaterThan=" + DEFAULT_TOTAL_ORDERS
        );
    }

    @Test
    @Transactional
    void getAllRemovedOrderSummariesByTotalAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRemovedOrderSummary = removedOrderSummaryRepository.saveAndFlush(removedOrderSummary);

        // Get all the removedOrderSummaryList where totalAmount equals to
        defaultRemovedOrderSummaryFiltering("totalAmount.equals=" + DEFAULT_TOTAL_AMOUNT, "totalAmount.equals=" + UPDATED_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllRemovedOrderSummariesByTotalAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRemovedOrderSummary = removedOrderSummaryRepository.saveAndFlush(removedOrderSummary);

        // Get all the removedOrderSummaryList where totalAmount in
        defaultRemovedOrderSummaryFiltering(
            "totalAmount.in=" + DEFAULT_TOTAL_AMOUNT + "," + UPDATED_TOTAL_AMOUNT,
            "totalAmount.in=" + UPDATED_TOTAL_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllRemovedOrderSummariesByTotalAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRemovedOrderSummary = removedOrderSummaryRepository.saveAndFlush(removedOrderSummary);

        // Get all the removedOrderSummaryList where totalAmount is not null
        defaultRemovedOrderSummaryFiltering("totalAmount.specified=true", "totalAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllRemovedOrderSummariesByTotalAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRemovedOrderSummary = removedOrderSummaryRepository.saveAndFlush(removedOrderSummary);

        // Get all the removedOrderSummaryList where totalAmount is greater than or equal to
        defaultRemovedOrderSummaryFiltering(
            "totalAmount.greaterThanOrEqual=" + DEFAULT_TOTAL_AMOUNT,
            "totalAmount.greaterThanOrEqual=" + UPDATED_TOTAL_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllRemovedOrderSummariesByTotalAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRemovedOrderSummary = removedOrderSummaryRepository.saveAndFlush(removedOrderSummary);

        // Get all the removedOrderSummaryList where totalAmount is less than or equal to
        defaultRemovedOrderSummaryFiltering(
            "totalAmount.lessThanOrEqual=" + DEFAULT_TOTAL_AMOUNT,
            "totalAmount.lessThanOrEqual=" + SMALLER_TOTAL_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllRemovedOrderSummariesByTotalAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedRemovedOrderSummary = removedOrderSummaryRepository.saveAndFlush(removedOrderSummary);

        // Get all the removedOrderSummaryList where totalAmount is less than
        defaultRemovedOrderSummaryFiltering("totalAmount.lessThan=" + UPDATED_TOTAL_AMOUNT, "totalAmount.lessThan=" + DEFAULT_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllRemovedOrderSummariesByTotalAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedRemovedOrderSummary = removedOrderSummaryRepository.saveAndFlush(removedOrderSummary);

        // Get all the removedOrderSummaryList where totalAmount is greater than
        defaultRemovedOrderSummaryFiltering(
            "totalAmount.greaterThan=" + SMALLER_TOTAL_AMOUNT,
            "totalAmount.greaterThan=" + DEFAULT_TOTAL_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllRemovedOrderSummariesByFirstInteractionAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRemovedOrderSummary = removedOrderSummaryRepository.saveAndFlush(removedOrderSummary);

        // Get all the removedOrderSummaryList where firstInteractionAt equals to
        defaultRemovedOrderSummaryFiltering(
            "firstInteractionAt.equals=" + DEFAULT_FIRST_INTERACTION_AT,
            "firstInteractionAt.equals=" + UPDATED_FIRST_INTERACTION_AT
        );
    }

    @Test
    @Transactional
    void getAllRemovedOrderSummariesByFirstInteractionAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRemovedOrderSummary = removedOrderSummaryRepository.saveAndFlush(removedOrderSummary);

        // Get all the removedOrderSummaryList where firstInteractionAt in
        defaultRemovedOrderSummaryFiltering(
            "firstInteractionAt.in=" + DEFAULT_FIRST_INTERACTION_AT + "," + UPDATED_FIRST_INTERACTION_AT,
            "firstInteractionAt.in=" + UPDATED_FIRST_INTERACTION_AT
        );
    }

    @Test
    @Transactional
    void getAllRemovedOrderSummariesByFirstInteractionAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRemovedOrderSummary = removedOrderSummaryRepository.saveAndFlush(removedOrderSummary);

        // Get all the removedOrderSummaryList where firstInteractionAt is not null
        defaultRemovedOrderSummaryFiltering("firstInteractionAt.specified=true", "firstInteractionAt.specified=false");
    }

    @Test
    @Transactional
    void getAllRemovedOrderSummariesByLastInteractionAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRemovedOrderSummary = removedOrderSummaryRepository.saveAndFlush(removedOrderSummary);

        // Get all the removedOrderSummaryList where lastInteractionAt equals to
        defaultRemovedOrderSummaryFiltering(
            "lastInteractionAt.equals=" + DEFAULT_LAST_INTERACTION_AT,
            "lastInteractionAt.equals=" + UPDATED_LAST_INTERACTION_AT
        );
    }

    @Test
    @Transactional
    void getAllRemovedOrderSummariesByLastInteractionAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRemovedOrderSummary = removedOrderSummaryRepository.saveAndFlush(removedOrderSummary);

        // Get all the removedOrderSummaryList where lastInteractionAt in
        defaultRemovedOrderSummaryFiltering(
            "lastInteractionAt.in=" + DEFAULT_LAST_INTERACTION_AT + "," + UPDATED_LAST_INTERACTION_AT,
            "lastInteractionAt.in=" + UPDATED_LAST_INTERACTION_AT
        );
    }

    @Test
    @Transactional
    void getAllRemovedOrderSummariesByLastInteractionAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRemovedOrderSummary = removedOrderSummaryRepository.saveAndFlush(removedOrderSummary);

        // Get all the removedOrderSummaryList where lastInteractionAt is not null
        defaultRemovedOrderSummaryFiltering("lastInteractionAt.specified=true", "lastInteractionAt.specified=false");
    }

    @Test
    @Transactional
    void getAllRemovedOrderSummariesByRemovedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRemovedOrderSummary = removedOrderSummaryRepository.saveAndFlush(removedOrderSummary);

        // Get all the removedOrderSummaryList where removedAt equals to
        defaultRemovedOrderSummaryFiltering("removedAt.equals=" + DEFAULT_REMOVED_AT, "removedAt.equals=" + UPDATED_REMOVED_AT);
    }

    @Test
    @Transactional
    void getAllRemovedOrderSummariesByRemovedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRemovedOrderSummary = removedOrderSummaryRepository.saveAndFlush(removedOrderSummary);

        // Get all the removedOrderSummaryList where removedAt in
        defaultRemovedOrderSummaryFiltering(
            "removedAt.in=" + DEFAULT_REMOVED_AT + "," + UPDATED_REMOVED_AT,
            "removedAt.in=" + UPDATED_REMOVED_AT
        );
    }

    @Test
    @Transactional
    void getAllRemovedOrderSummariesByRemovedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRemovedOrderSummary = removedOrderSummaryRepository.saveAndFlush(removedOrderSummary);

        // Get all the removedOrderSummaryList where removedAt is not null
        defaultRemovedOrderSummaryFiltering("removedAt.specified=true", "removedAt.specified=false");
    }

    private void defaultRemovedOrderSummaryFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultRemovedOrderSummaryShouldBeFound(shouldBeFound);
        defaultRemovedOrderSummaryShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRemovedOrderSummaryShouldBeFound(String filter) throws Exception {
        restRemovedOrderSummaryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(removedOrderSummary.getId().intValue())))
            .andExpect(jsonPath("$.[*].userOriginalId").value(hasItem(DEFAULT_USER_ORIGINAL_ID.intValue())))
            .andExpect(jsonPath("$.[*].userName").value(hasItem(DEFAULT_USER_NAME)))
            .andExpect(jsonPath("$.[*].userRole").value(hasItem(DEFAULT_USER_ROLE.toString())))
            .andExpect(jsonPath("$.[*].totalOrders").value(hasItem(DEFAULT_TOTAL_ORDERS)))
            .andExpect(jsonPath("$.[*].totalAmount").value(hasItem(DEFAULT_TOTAL_AMOUNT)))
            .andExpect(jsonPath("$.[*].firstInteractionAt").value(hasItem(DEFAULT_FIRST_INTERACTION_AT.toString())))
            .andExpect(jsonPath("$.[*].lastInteractionAt").value(hasItem(DEFAULT_LAST_INTERACTION_AT.toString())))
            .andExpect(jsonPath("$.[*].removedAt").value(hasItem(DEFAULT_REMOVED_AT.toString())));

        // Check, that the count call also returns 1
        restRemovedOrderSummaryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultRemovedOrderSummaryShouldNotBeFound(String filter) throws Exception {
        restRemovedOrderSummaryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restRemovedOrderSummaryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingRemovedOrderSummary() throws Exception {
        // Get the removedOrderSummary
        restRemovedOrderSummaryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRemovedOrderSummary() throws Exception {
        // Initialize the database
        insertedRemovedOrderSummary = removedOrderSummaryRepository.saveAndFlush(removedOrderSummary);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the removedOrderSummary
        RemovedOrderSummary updatedRemovedOrderSummary = removedOrderSummaryRepository.findById(removedOrderSummary.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedRemovedOrderSummary are not directly saved in db
        em.detach(updatedRemovedOrderSummary);
        updatedRemovedOrderSummary
            .userOriginalId(UPDATED_USER_ORIGINAL_ID)
            .userName(UPDATED_USER_NAME)
            .userRole(UPDATED_USER_ROLE)
            .totalOrders(UPDATED_TOTAL_ORDERS)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .firstInteractionAt(UPDATED_FIRST_INTERACTION_AT)
            .lastInteractionAt(UPDATED_LAST_INTERACTION_AT)
            .removedAt(UPDATED_REMOVED_AT);
        RemovedOrderSummaryDTO removedOrderSummaryDTO = removedOrderSummaryMapper.toDto(updatedRemovedOrderSummary);

        restRemovedOrderSummaryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, removedOrderSummaryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(removedOrderSummaryDTO))
            )
            .andExpect(status().isOk());

        // Validate the RemovedOrderSummary in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRemovedOrderSummaryToMatchAllProperties(updatedRemovedOrderSummary);
    }

    @Test
    @Transactional
    void putNonExistingRemovedOrderSummary() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        removedOrderSummary.setId(longCount.incrementAndGet());

        // Create the RemovedOrderSummary
        RemovedOrderSummaryDTO removedOrderSummaryDTO = removedOrderSummaryMapper.toDto(removedOrderSummary);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRemovedOrderSummaryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, removedOrderSummaryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(removedOrderSummaryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RemovedOrderSummary in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRemovedOrderSummary() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        removedOrderSummary.setId(longCount.incrementAndGet());

        // Create the RemovedOrderSummary
        RemovedOrderSummaryDTO removedOrderSummaryDTO = removedOrderSummaryMapper.toDto(removedOrderSummary);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRemovedOrderSummaryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(removedOrderSummaryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RemovedOrderSummary in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRemovedOrderSummary() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        removedOrderSummary.setId(longCount.incrementAndGet());

        // Create the RemovedOrderSummary
        RemovedOrderSummaryDTO removedOrderSummaryDTO = removedOrderSummaryMapper.toDto(removedOrderSummary);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRemovedOrderSummaryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(removedOrderSummaryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the RemovedOrderSummary in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRemovedOrderSummaryWithPatch() throws Exception {
        // Initialize the database
        insertedRemovedOrderSummary = removedOrderSummaryRepository.saveAndFlush(removedOrderSummary);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the removedOrderSummary using partial update
        RemovedOrderSummary partialUpdatedRemovedOrderSummary = new RemovedOrderSummary();
        partialUpdatedRemovedOrderSummary.setId(removedOrderSummary.getId());

        partialUpdatedRemovedOrderSummary.userRole(UPDATED_USER_ROLE).removedAt(UPDATED_REMOVED_AT);

        restRemovedOrderSummaryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRemovedOrderSummary.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRemovedOrderSummary))
            )
            .andExpect(status().isOk());

        // Validate the RemovedOrderSummary in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRemovedOrderSummaryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedRemovedOrderSummary, removedOrderSummary),
            getPersistedRemovedOrderSummary(removedOrderSummary)
        );
    }

    @Test
    @Transactional
    void fullUpdateRemovedOrderSummaryWithPatch() throws Exception {
        // Initialize the database
        insertedRemovedOrderSummary = removedOrderSummaryRepository.saveAndFlush(removedOrderSummary);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the removedOrderSummary using partial update
        RemovedOrderSummary partialUpdatedRemovedOrderSummary = new RemovedOrderSummary();
        partialUpdatedRemovedOrderSummary.setId(removedOrderSummary.getId());

        partialUpdatedRemovedOrderSummary
            .userOriginalId(UPDATED_USER_ORIGINAL_ID)
            .userName(UPDATED_USER_NAME)
            .userRole(UPDATED_USER_ROLE)
            .totalOrders(UPDATED_TOTAL_ORDERS)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .firstInteractionAt(UPDATED_FIRST_INTERACTION_AT)
            .lastInteractionAt(UPDATED_LAST_INTERACTION_AT)
            .removedAt(UPDATED_REMOVED_AT);

        restRemovedOrderSummaryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRemovedOrderSummary.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRemovedOrderSummary))
            )
            .andExpect(status().isOk());

        // Validate the RemovedOrderSummary in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRemovedOrderSummaryUpdatableFieldsEquals(
            partialUpdatedRemovedOrderSummary,
            getPersistedRemovedOrderSummary(partialUpdatedRemovedOrderSummary)
        );
    }

    @Test
    @Transactional
    void patchNonExistingRemovedOrderSummary() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        removedOrderSummary.setId(longCount.incrementAndGet());

        // Create the RemovedOrderSummary
        RemovedOrderSummaryDTO removedOrderSummaryDTO = removedOrderSummaryMapper.toDto(removedOrderSummary);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRemovedOrderSummaryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, removedOrderSummaryDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(removedOrderSummaryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RemovedOrderSummary in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRemovedOrderSummary() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        removedOrderSummary.setId(longCount.incrementAndGet());

        // Create the RemovedOrderSummary
        RemovedOrderSummaryDTO removedOrderSummaryDTO = removedOrderSummaryMapper.toDto(removedOrderSummary);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRemovedOrderSummaryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(removedOrderSummaryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RemovedOrderSummary in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRemovedOrderSummary() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        removedOrderSummary.setId(longCount.incrementAndGet());

        // Create the RemovedOrderSummary
        RemovedOrderSummaryDTO removedOrderSummaryDTO = removedOrderSummaryMapper.toDto(removedOrderSummary);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRemovedOrderSummaryMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(removedOrderSummaryDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the RemovedOrderSummary in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRemovedOrderSummary() throws Exception {
        // Initialize the database
        insertedRemovedOrderSummary = removedOrderSummaryRepository.saveAndFlush(removedOrderSummary);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the removedOrderSummary
        restRemovedOrderSummaryMockMvc
            .perform(delete(ENTITY_API_URL_ID, removedOrderSummary.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return removedOrderSummaryRepository.count();
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

    protected RemovedOrderSummary getPersistedRemovedOrderSummary(RemovedOrderSummary removedOrderSummary) {
        return removedOrderSummaryRepository.findById(removedOrderSummary.getId()).orElseThrow();
    }

    protected void assertPersistedRemovedOrderSummaryToMatchAllProperties(RemovedOrderSummary expectedRemovedOrderSummary) {
        assertRemovedOrderSummaryAllPropertiesEquals(
            expectedRemovedOrderSummary,
            getPersistedRemovedOrderSummary(expectedRemovedOrderSummary)
        );
    }

    protected void assertPersistedRemovedOrderSummaryToMatchUpdatableProperties(RemovedOrderSummary expectedRemovedOrderSummary) {
        assertRemovedOrderSummaryAllUpdatablePropertiesEquals(
            expectedRemovedOrderSummary,
            getPersistedRemovedOrderSummary(expectedRemovedOrderSummary)
        );
    }
}
