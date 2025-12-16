package com.aps.web.rest;

import static com.aps.domain.CustomerAsserts.*;
import static com.aps.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.aps.IntegrationTest;
import com.aps.domain.Customer;
import com.aps.domain.TeamMember;
import com.aps.domain.enumeration.UserRole;
import com.aps.repository.CustomerRepository;
import com.aps.service.CustomerService;
import com.aps.service.dto.CustomerDTO;
import com.aps.service.mapper.CustomerMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CustomerResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CustomerResourceIT {

    private static final String DEFAULT_WA_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_WA_PHONE_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBB";

    private static final Double DEFAULT_LOCATION_LAT = 1D;
    private static final Double UPDATED_LOCATION_LAT = 2D;
    private static final Double SMALLER_LOCATION_LAT = 1D - 1D;

    private static final Double DEFAULT_LOCATION_LON = 1D;
    private static final Double UPDATED_LOCATION_LON = 2D;
    private static final Double SMALLER_LOCATION_LON = 1D - 1D;

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final Double DEFAULT_DISTANCE_FROM_BUSINESS_KM = 1D;
    private static final Double UPDATED_DISTANCE_FROM_BUSINESS_KM = 2D;
    private static final Double SMALLER_DISTANCE_FROM_BUSINESS_KM = 1D - 1D;

    private static final Boolean DEFAULT_IS_PINCODE_VALID = false;
    private static final Boolean UPDATED_IS_PINCODE_VALID = true;

    private static final UserRole DEFAULT_ROLE = UserRole.CUSTOMER;
    private static final UserRole UPDATED_ROLE = UserRole.EXECUTIVE;

    private static final Instant DEFAULT_JOINED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_JOINED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_LAST_INTERACTION_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_INTERACTION_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/customers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CustomerRepository customerRepository;

    @Mock
    private CustomerRepository customerRepositoryMock;

    @Autowired
    private CustomerMapper customerMapper;

    @Mock
    private CustomerService customerServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCustomerMockMvc;

    private Customer customer;

    private Customer insertedCustomer;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Customer createEntity() {
        return new Customer()
            .waPhoneNumber(DEFAULT_WA_PHONE_NUMBER)
            .name(DEFAULT_NAME)
            .phoneNumber(DEFAULT_PHONE_NUMBER)
            .locationLat(DEFAULT_LOCATION_LAT)
            .locationLon(DEFAULT_LOCATION_LON)
            .address(DEFAULT_ADDRESS)
            .distanceFromBusinessKm(DEFAULT_DISTANCE_FROM_BUSINESS_KM)
            .isPincodeValid(DEFAULT_IS_PINCODE_VALID)
            .role(DEFAULT_ROLE)
            .joinedAt(DEFAULT_JOINED_AT)
            .lastInteractionAt(DEFAULT_LAST_INTERACTION_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Customer createUpdatedEntity() {
        return new Customer()
            .waPhoneNumber(UPDATED_WA_PHONE_NUMBER)
            .name(UPDATED_NAME)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .locationLat(UPDATED_LOCATION_LAT)
            .locationLon(UPDATED_LOCATION_LON)
            .address(UPDATED_ADDRESS)
            .distanceFromBusinessKm(UPDATED_DISTANCE_FROM_BUSINESS_KM)
            .isPincodeValid(UPDATED_IS_PINCODE_VALID)
            .role(UPDATED_ROLE)
            .joinedAt(UPDATED_JOINED_AT)
            .lastInteractionAt(UPDATED_LAST_INTERACTION_AT);
    }

    @BeforeEach
    public void initTest() {
        customer = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedCustomer != null) {
            customerRepository.delete(insertedCustomer);
            insertedCustomer = null;
        }
    }

    @Test
    @Transactional
    void createCustomer() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Customer
        CustomerDTO customerDTO = customerMapper.toDto(customer);
        var returnedCustomerDTO = om.readValue(
            restCustomerMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(customerDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CustomerDTO.class
        );

        // Validate the Customer in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCustomer = customerMapper.toEntity(returnedCustomerDTO);
        assertCustomerUpdatableFieldsEquals(returnedCustomer, getPersistedCustomer(returnedCustomer));

        insertedCustomer = returnedCustomer;
    }

    @Test
    @Transactional
    void createCustomerWithExistingId() throws Exception {
        // Create the Customer with an existing ID
        customer.setId(1L);
        CustomerDTO customerDTO = customerMapper.toDto(customer);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCustomerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(customerDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Customer in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkWaPhoneNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        customer.setWaPhoneNumber(null);

        // Create the Customer, which fails.
        CustomerDTO customerDTO = customerMapper.toDto(customer);

        restCustomerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(customerDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsPincodeValidIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        customer.setIsPincodeValid(null);

        // Create the Customer, which fails.
        CustomerDTO customerDTO = customerMapper.toDto(customer);

        restCustomerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(customerDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRoleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        customer.setRole(null);

        // Create the Customer, which fails.
        CustomerDTO customerDTO = customerMapper.toDto(customer);

        restCustomerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(customerDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCustomers() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList
        restCustomerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(customer.getId().intValue())))
            .andExpect(jsonPath("$.[*].waPhoneNumber").value(hasItem(DEFAULT_WA_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].locationLat").value(hasItem(DEFAULT_LOCATION_LAT)))
            .andExpect(jsonPath("$.[*].locationLon").value(hasItem(DEFAULT_LOCATION_LON)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].distanceFromBusinessKm").value(hasItem(DEFAULT_DISTANCE_FROM_BUSINESS_KM)))
            .andExpect(jsonPath("$.[*].isPincodeValid").value(hasItem(DEFAULT_IS_PINCODE_VALID)))
            .andExpect(jsonPath("$.[*].role").value(hasItem(DEFAULT_ROLE.toString())))
            .andExpect(jsonPath("$.[*].joinedAt").value(hasItem(DEFAULT_JOINED_AT.toString())))
            .andExpect(jsonPath("$.[*].lastInteractionAt").value(hasItem(DEFAULT_LAST_INTERACTION_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCustomersWithEagerRelationshipsIsEnabled() throws Exception {
        when(customerServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCustomerMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(customerServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCustomersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(customerServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCustomerMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(customerRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getCustomer() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get the customer
        restCustomerMockMvc
            .perform(get(ENTITY_API_URL_ID, customer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(customer.getId().intValue()))
            .andExpect(jsonPath("$.waPhoneNumber").value(DEFAULT_WA_PHONE_NUMBER))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER))
            .andExpect(jsonPath("$.locationLat").value(DEFAULT_LOCATION_LAT))
            .andExpect(jsonPath("$.locationLon").value(DEFAULT_LOCATION_LON))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS))
            .andExpect(jsonPath("$.distanceFromBusinessKm").value(DEFAULT_DISTANCE_FROM_BUSINESS_KM))
            .andExpect(jsonPath("$.isPincodeValid").value(DEFAULT_IS_PINCODE_VALID))
            .andExpect(jsonPath("$.role").value(DEFAULT_ROLE.toString()))
            .andExpect(jsonPath("$.joinedAt").value(DEFAULT_JOINED_AT.toString()))
            .andExpect(jsonPath("$.lastInteractionAt").value(DEFAULT_LAST_INTERACTION_AT.toString()));
    }

    @Test
    @Transactional
    void getCustomersByIdFiltering() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        Long id = customer.getId();

        defaultCustomerFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCustomerFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCustomerFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCustomersByWaPhoneNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where waPhoneNumber equals to
        defaultCustomerFiltering("waPhoneNumber.equals=" + DEFAULT_WA_PHONE_NUMBER, "waPhoneNumber.equals=" + UPDATED_WA_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllCustomersByWaPhoneNumberIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where waPhoneNumber in
        defaultCustomerFiltering(
            "waPhoneNumber.in=" + DEFAULT_WA_PHONE_NUMBER + "," + UPDATED_WA_PHONE_NUMBER,
            "waPhoneNumber.in=" + UPDATED_WA_PHONE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllCustomersByWaPhoneNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where waPhoneNumber is not null
        defaultCustomerFiltering("waPhoneNumber.specified=true", "waPhoneNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllCustomersByWaPhoneNumberContainsSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where waPhoneNumber contains
        defaultCustomerFiltering("waPhoneNumber.contains=" + DEFAULT_WA_PHONE_NUMBER, "waPhoneNumber.contains=" + UPDATED_WA_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllCustomersByWaPhoneNumberNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where waPhoneNumber does not contain
        defaultCustomerFiltering(
            "waPhoneNumber.doesNotContain=" + UPDATED_WA_PHONE_NUMBER,
            "waPhoneNumber.doesNotContain=" + DEFAULT_WA_PHONE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllCustomersByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where name equals to
        defaultCustomerFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCustomersByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where name in
        defaultCustomerFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCustomersByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where name is not null
        defaultCustomerFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllCustomersByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where name contains
        defaultCustomerFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCustomersByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where name does not contain
        defaultCustomerFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllCustomersByPhoneNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where phoneNumber equals to
        defaultCustomerFiltering("phoneNumber.equals=" + DEFAULT_PHONE_NUMBER, "phoneNumber.equals=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllCustomersByPhoneNumberIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where phoneNumber in
        defaultCustomerFiltering(
            "phoneNumber.in=" + DEFAULT_PHONE_NUMBER + "," + UPDATED_PHONE_NUMBER,
            "phoneNumber.in=" + UPDATED_PHONE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllCustomersByPhoneNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where phoneNumber is not null
        defaultCustomerFiltering("phoneNumber.specified=true", "phoneNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllCustomersByPhoneNumberContainsSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where phoneNumber contains
        defaultCustomerFiltering("phoneNumber.contains=" + DEFAULT_PHONE_NUMBER, "phoneNumber.contains=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllCustomersByPhoneNumberNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where phoneNumber does not contain
        defaultCustomerFiltering(
            "phoneNumber.doesNotContain=" + UPDATED_PHONE_NUMBER,
            "phoneNumber.doesNotContain=" + DEFAULT_PHONE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllCustomersByLocationLatIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where locationLat equals to
        defaultCustomerFiltering("locationLat.equals=" + DEFAULT_LOCATION_LAT, "locationLat.equals=" + UPDATED_LOCATION_LAT);
    }

    @Test
    @Transactional
    void getAllCustomersByLocationLatIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where locationLat in
        defaultCustomerFiltering(
            "locationLat.in=" + DEFAULT_LOCATION_LAT + "," + UPDATED_LOCATION_LAT,
            "locationLat.in=" + UPDATED_LOCATION_LAT
        );
    }

    @Test
    @Transactional
    void getAllCustomersByLocationLatIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where locationLat is not null
        defaultCustomerFiltering("locationLat.specified=true", "locationLat.specified=false");
    }

    @Test
    @Transactional
    void getAllCustomersByLocationLatIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where locationLat is greater than or equal to
        defaultCustomerFiltering(
            "locationLat.greaterThanOrEqual=" + DEFAULT_LOCATION_LAT,
            "locationLat.greaterThanOrEqual=" + UPDATED_LOCATION_LAT
        );
    }

    @Test
    @Transactional
    void getAllCustomersByLocationLatIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where locationLat is less than or equal to
        defaultCustomerFiltering(
            "locationLat.lessThanOrEqual=" + DEFAULT_LOCATION_LAT,
            "locationLat.lessThanOrEqual=" + SMALLER_LOCATION_LAT
        );
    }

    @Test
    @Transactional
    void getAllCustomersByLocationLatIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where locationLat is less than
        defaultCustomerFiltering("locationLat.lessThan=" + UPDATED_LOCATION_LAT, "locationLat.lessThan=" + DEFAULT_LOCATION_LAT);
    }

    @Test
    @Transactional
    void getAllCustomersByLocationLatIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where locationLat is greater than
        defaultCustomerFiltering("locationLat.greaterThan=" + SMALLER_LOCATION_LAT, "locationLat.greaterThan=" + DEFAULT_LOCATION_LAT);
    }

    @Test
    @Transactional
    void getAllCustomersByLocationLonIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where locationLon equals to
        defaultCustomerFiltering("locationLon.equals=" + DEFAULT_LOCATION_LON, "locationLon.equals=" + UPDATED_LOCATION_LON);
    }

    @Test
    @Transactional
    void getAllCustomersByLocationLonIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where locationLon in
        defaultCustomerFiltering(
            "locationLon.in=" + DEFAULT_LOCATION_LON + "," + UPDATED_LOCATION_LON,
            "locationLon.in=" + UPDATED_LOCATION_LON
        );
    }

    @Test
    @Transactional
    void getAllCustomersByLocationLonIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where locationLon is not null
        defaultCustomerFiltering("locationLon.specified=true", "locationLon.specified=false");
    }

    @Test
    @Transactional
    void getAllCustomersByLocationLonIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where locationLon is greater than or equal to
        defaultCustomerFiltering(
            "locationLon.greaterThanOrEqual=" + DEFAULT_LOCATION_LON,
            "locationLon.greaterThanOrEqual=" + UPDATED_LOCATION_LON
        );
    }

    @Test
    @Transactional
    void getAllCustomersByLocationLonIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where locationLon is less than or equal to
        defaultCustomerFiltering(
            "locationLon.lessThanOrEqual=" + DEFAULT_LOCATION_LON,
            "locationLon.lessThanOrEqual=" + SMALLER_LOCATION_LON
        );
    }

    @Test
    @Transactional
    void getAllCustomersByLocationLonIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where locationLon is less than
        defaultCustomerFiltering("locationLon.lessThan=" + UPDATED_LOCATION_LON, "locationLon.lessThan=" + DEFAULT_LOCATION_LON);
    }

    @Test
    @Transactional
    void getAllCustomersByLocationLonIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where locationLon is greater than
        defaultCustomerFiltering("locationLon.greaterThan=" + SMALLER_LOCATION_LON, "locationLon.greaterThan=" + DEFAULT_LOCATION_LON);
    }

    @Test
    @Transactional
    void getAllCustomersByAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where address equals to
        defaultCustomerFiltering("address.equals=" + DEFAULT_ADDRESS, "address.equals=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllCustomersByAddressIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where address in
        defaultCustomerFiltering("address.in=" + DEFAULT_ADDRESS + "," + UPDATED_ADDRESS, "address.in=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllCustomersByAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where address is not null
        defaultCustomerFiltering("address.specified=true", "address.specified=false");
    }

    @Test
    @Transactional
    void getAllCustomersByAddressContainsSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where address contains
        defaultCustomerFiltering("address.contains=" + DEFAULT_ADDRESS, "address.contains=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllCustomersByAddressNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where address does not contain
        defaultCustomerFiltering("address.doesNotContain=" + UPDATED_ADDRESS, "address.doesNotContain=" + DEFAULT_ADDRESS);
    }

    @Test
    @Transactional
    void getAllCustomersByDistanceFromBusinessKmIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where distanceFromBusinessKm equals to
        defaultCustomerFiltering(
            "distanceFromBusinessKm.equals=" + DEFAULT_DISTANCE_FROM_BUSINESS_KM,
            "distanceFromBusinessKm.equals=" + UPDATED_DISTANCE_FROM_BUSINESS_KM
        );
    }

    @Test
    @Transactional
    void getAllCustomersByDistanceFromBusinessKmIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where distanceFromBusinessKm in
        defaultCustomerFiltering(
            "distanceFromBusinessKm.in=" + DEFAULT_DISTANCE_FROM_BUSINESS_KM + "," + UPDATED_DISTANCE_FROM_BUSINESS_KM,
            "distanceFromBusinessKm.in=" + UPDATED_DISTANCE_FROM_BUSINESS_KM
        );
    }

    @Test
    @Transactional
    void getAllCustomersByDistanceFromBusinessKmIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where distanceFromBusinessKm is not null
        defaultCustomerFiltering("distanceFromBusinessKm.specified=true", "distanceFromBusinessKm.specified=false");
    }

    @Test
    @Transactional
    void getAllCustomersByDistanceFromBusinessKmIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where distanceFromBusinessKm is greater than or equal to
        defaultCustomerFiltering(
            "distanceFromBusinessKm.greaterThanOrEqual=" + DEFAULT_DISTANCE_FROM_BUSINESS_KM,
            "distanceFromBusinessKm.greaterThanOrEqual=" + UPDATED_DISTANCE_FROM_BUSINESS_KM
        );
    }

    @Test
    @Transactional
    void getAllCustomersByDistanceFromBusinessKmIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where distanceFromBusinessKm is less than or equal to
        defaultCustomerFiltering(
            "distanceFromBusinessKm.lessThanOrEqual=" + DEFAULT_DISTANCE_FROM_BUSINESS_KM,
            "distanceFromBusinessKm.lessThanOrEqual=" + SMALLER_DISTANCE_FROM_BUSINESS_KM
        );
    }

    @Test
    @Transactional
    void getAllCustomersByDistanceFromBusinessKmIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where distanceFromBusinessKm is less than
        defaultCustomerFiltering(
            "distanceFromBusinessKm.lessThan=" + UPDATED_DISTANCE_FROM_BUSINESS_KM,
            "distanceFromBusinessKm.lessThan=" + DEFAULT_DISTANCE_FROM_BUSINESS_KM
        );
    }

    @Test
    @Transactional
    void getAllCustomersByDistanceFromBusinessKmIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where distanceFromBusinessKm is greater than
        defaultCustomerFiltering(
            "distanceFromBusinessKm.greaterThan=" + SMALLER_DISTANCE_FROM_BUSINESS_KM,
            "distanceFromBusinessKm.greaterThan=" + DEFAULT_DISTANCE_FROM_BUSINESS_KM
        );
    }

    @Test
    @Transactional
    void getAllCustomersByIsPincodeValidIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where isPincodeValid equals to
        defaultCustomerFiltering("isPincodeValid.equals=" + DEFAULT_IS_PINCODE_VALID, "isPincodeValid.equals=" + UPDATED_IS_PINCODE_VALID);
    }

    @Test
    @Transactional
    void getAllCustomersByIsPincodeValidIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where isPincodeValid in
        defaultCustomerFiltering(
            "isPincodeValid.in=" + DEFAULT_IS_PINCODE_VALID + "," + UPDATED_IS_PINCODE_VALID,
            "isPincodeValid.in=" + UPDATED_IS_PINCODE_VALID
        );
    }

    @Test
    @Transactional
    void getAllCustomersByIsPincodeValidIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where isPincodeValid is not null
        defaultCustomerFiltering("isPincodeValid.specified=true", "isPincodeValid.specified=false");
    }

    @Test
    @Transactional
    void getAllCustomersByRoleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where role equals to
        defaultCustomerFiltering("role.equals=" + DEFAULT_ROLE, "role.equals=" + UPDATED_ROLE);
    }

    @Test
    @Transactional
    void getAllCustomersByRoleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where role in
        defaultCustomerFiltering("role.in=" + DEFAULT_ROLE + "," + UPDATED_ROLE, "role.in=" + UPDATED_ROLE);
    }

    @Test
    @Transactional
    void getAllCustomersByRoleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where role is not null
        defaultCustomerFiltering("role.specified=true", "role.specified=false");
    }

    @Test
    @Transactional
    void getAllCustomersByJoinedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where joinedAt equals to
        defaultCustomerFiltering("joinedAt.equals=" + DEFAULT_JOINED_AT, "joinedAt.equals=" + UPDATED_JOINED_AT);
    }

    @Test
    @Transactional
    void getAllCustomersByJoinedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where joinedAt in
        defaultCustomerFiltering("joinedAt.in=" + DEFAULT_JOINED_AT + "," + UPDATED_JOINED_AT, "joinedAt.in=" + UPDATED_JOINED_AT);
    }

    @Test
    @Transactional
    void getAllCustomersByJoinedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where joinedAt is not null
        defaultCustomerFiltering("joinedAt.specified=true", "joinedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllCustomersByLastInteractionAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where lastInteractionAt equals to
        defaultCustomerFiltering(
            "lastInteractionAt.equals=" + DEFAULT_LAST_INTERACTION_AT,
            "lastInteractionAt.equals=" + UPDATED_LAST_INTERACTION_AT
        );
    }

    @Test
    @Transactional
    void getAllCustomersByLastInteractionAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where lastInteractionAt in
        defaultCustomerFiltering(
            "lastInteractionAt.in=" + DEFAULT_LAST_INTERACTION_AT + "," + UPDATED_LAST_INTERACTION_AT,
            "lastInteractionAt.in=" + UPDATED_LAST_INTERACTION_AT
        );
    }

    @Test
    @Transactional
    void getAllCustomersByLastInteractionAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        // Get all the customerList where lastInteractionAt is not null
        defaultCustomerFiltering("lastInteractionAt.specified=true", "lastInteractionAt.specified=false");
    }

    @Test
    @Transactional
    void getAllCustomersByAddedByIsEqualToSomething() throws Exception {
        TeamMember addedBy;
        if (TestUtil.findAll(em, TeamMember.class).isEmpty()) {
            customerRepository.saveAndFlush(customer);
            addedBy = TeamMemberResourceIT.createEntity();
        } else {
            addedBy = TestUtil.findAll(em, TeamMember.class).get(0);
        }
        em.persist(addedBy);
        em.flush();
        customer.setAddedBy(addedBy);
        customerRepository.saveAndFlush(customer);
        Long addedById = addedBy.getId();
        // Get all the customerList where addedBy equals to addedById
        defaultCustomerShouldBeFound("addedById.equals=" + addedById);

        // Get all the customerList where addedBy equals to (addedById + 1)
        defaultCustomerShouldNotBeFound("addedById.equals=" + (addedById + 1));
    }

    private void defaultCustomerFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCustomerShouldBeFound(shouldBeFound);
        defaultCustomerShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCustomerShouldBeFound(String filter) throws Exception {
        restCustomerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(customer.getId().intValue())))
            .andExpect(jsonPath("$.[*].waPhoneNumber").value(hasItem(DEFAULT_WA_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].locationLat").value(hasItem(DEFAULT_LOCATION_LAT)))
            .andExpect(jsonPath("$.[*].locationLon").value(hasItem(DEFAULT_LOCATION_LON)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].distanceFromBusinessKm").value(hasItem(DEFAULT_DISTANCE_FROM_BUSINESS_KM)))
            .andExpect(jsonPath("$.[*].isPincodeValid").value(hasItem(DEFAULT_IS_PINCODE_VALID)))
            .andExpect(jsonPath("$.[*].role").value(hasItem(DEFAULT_ROLE.toString())))
            .andExpect(jsonPath("$.[*].joinedAt").value(hasItem(DEFAULT_JOINED_AT.toString())))
            .andExpect(jsonPath("$.[*].lastInteractionAt").value(hasItem(DEFAULT_LAST_INTERACTION_AT.toString())));

        // Check, that the count call also returns 1
        restCustomerMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCustomerShouldNotBeFound(String filter) throws Exception {
        restCustomerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCustomerMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCustomer() throws Exception {
        // Get the customer
        restCustomerMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCustomer() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the customer
        Customer updatedCustomer = customerRepository.findById(customer.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCustomer are not directly saved in db
        em.detach(updatedCustomer);
        updatedCustomer
            .waPhoneNumber(UPDATED_WA_PHONE_NUMBER)
            .name(UPDATED_NAME)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .locationLat(UPDATED_LOCATION_LAT)
            .locationLon(UPDATED_LOCATION_LON)
            .address(UPDATED_ADDRESS)
            .distanceFromBusinessKm(UPDATED_DISTANCE_FROM_BUSINESS_KM)
            .isPincodeValid(UPDATED_IS_PINCODE_VALID)
            .role(UPDATED_ROLE)
            .joinedAt(UPDATED_JOINED_AT)
            .lastInteractionAt(UPDATED_LAST_INTERACTION_AT);
        CustomerDTO customerDTO = customerMapper.toDto(updatedCustomer);

        restCustomerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, customerDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(customerDTO))
            )
            .andExpect(status().isOk());

        // Validate the Customer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCustomerToMatchAllProperties(updatedCustomer);
    }

    @Test
    @Transactional
    void putNonExistingCustomer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customer.setId(longCount.incrementAndGet());

        // Create the Customer
        CustomerDTO customerDTO = customerMapper.toDto(customer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCustomerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, customerDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(customerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Customer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCustomer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customer.setId(longCount.incrementAndGet());

        // Create the Customer
        CustomerDTO customerDTO = customerMapper.toDto(customer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(customerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Customer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCustomer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customer.setId(longCount.incrementAndGet());

        // Create the Customer
        CustomerDTO customerDTO = customerMapper.toDto(customer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomerMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(customerDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Customer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCustomerWithPatch() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the customer using partial update
        Customer partialUpdatedCustomer = new Customer();
        partialUpdatedCustomer.setId(customer.getId());

        partialUpdatedCustomer
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .locationLon(UPDATED_LOCATION_LON)
            .distanceFromBusinessKm(UPDATED_DISTANCE_FROM_BUSINESS_KM)
            .role(UPDATED_ROLE)
            .lastInteractionAt(UPDATED_LAST_INTERACTION_AT);

        restCustomerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCustomer.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCustomer))
            )
            .andExpect(status().isOk());

        // Validate the Customer in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCustomerUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCustomer, customer), getPersistedCustomer(customer));
    }

    @Test
    @Transactional
    void fullUpdateCustomerWithPatch() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the customer using partial update
        Customer partialUpdatedCustomer = new Customer();
        partialUpdatedCustomer.setId(customer.getId());

        partialUpdatedCustomer
            .waPhoneNumber(UPDATED_WA_PHONE_NUMBER)
            .name(UPDATED_NAME)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .locationLat(UPDATED_LOCATION_LAT)
            .locationLon(UPDATED_LOCATION_LON)
            .address(UPDATED_ADDRESS)
            .distanceFromBusinessKm(UPDATED_DISTANCE_FROM_BUSINESS_KM)
            .isPincodeValid(UPDATED_IS_PINCODE_VALID)
            .role(UPDATED_ROLE)
            .joinedAt(UPDATED_JOINED_AT)
            .lastInteractionAt(UPDATED_LAST_INTERACTION_AT);

        restCustomerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCustomer.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCustomer))
            )
            .andExpect(status().isOk());

        // Validate the Customer in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCustomerUpdatableFieldsEquals(partialUpdatedCustomer, getPersistedCustomer(partialUpdatedCustomer));
    }

    @Test
    @Transactional
    void patchNonExistingCustomer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customer.setId(longCount.incrementAndGet());

        // Create the Customer
        CustomerDTO customerDTO = customerMapper.toDto(customer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCustomerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, customerDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(customerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Customer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCustomer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customer.setId(longCount.incrementAndGet());

        // Create the Customer
        CustomerDTO customerDTO = customerMapper.toDto(customer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(customerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Customer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCustomer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customer.setId(longCount.incrementAndGet());

        // Create the Customer
        CustomerDTO customerDTO = customerMapper.toDto(customer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomerMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(customerDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Customer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCustomer() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.saveAndFlush(customer);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the customer
        restCustomerMockMvc
            .perform(delete(ENTITY_API_URL_ID, customer.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return customerRepository.count();
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

    protected Customer getPersistedCustomer(Customer customer) {
        return customerRepository.findById(customer.getId()).orElseThrow();
    }

    protected void assertPersistedCustomerToMatchAllProperties(Customer expectedCustomer) {
        assertCustomerAllPropertiesEquals(expectedCustomer, getPersistedCustomer(expectedCustomer));
    }

    protected void assertPersistedCustomerToMatchUpdatableProperties(Customer expectedCustomer) {
        assertCustomerAllUpdatablePropertiesEquals(expectedCustomer, getPersistedCustomer(expectedCustomer));
    }
}
