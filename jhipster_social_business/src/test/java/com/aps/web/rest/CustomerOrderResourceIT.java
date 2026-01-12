package com.aps.web.rest;

import static com.aps.domain.CustomerOrderAsserts.*;
import static com.aps.web.rest.TestUtil.createUpdateProxyForBean;
import static com.aps.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.aps.IntegrationTest;
import com.aps.domain.Customer;
import com.aps.domain.CustomerOrder;
import com.aps.domain.DeliveryPerson;
import com.aps.domain.OrderStatusHistory;
import com.aps.domain.enumeration.OrderStatus;
import com.aps.repository.CustomerOrderRepository;
import com.aps.service.CustomerOrderService;
import com.aps.service.dto.CustomerOrderDTO;
import com.aps.service.mapper.CustomerOrderMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link CustomerOrderResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CustomerOrderResourceIT {

    private static final Instant DEFAULT_ORDER_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ORDER_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final BigDecimal DEFAULT_TOTAL_AMOUNT = new BigDecimal(0);
    private static final BigDecimal UPDATED_TOTAL_AMOUNT = new BigDecimal(1);
    private static final BigDecimal SMALLER_TOTAL_AMOUNT = new BigDecimal(0 - 1);

    private static final OrderStatus DEFAULT_STATUS = OrderStatus.ORDER_NOT_TAKEN;
    private static final OrderStatus UPDATED_STATUS = OrderStatus.DELIVERY_ONWAY;

    private static final com.aps.domain.enumeration.PaymentMode DEFAULT_PAYMENT_METHOD = com.aps.domain.enumeration.PaymentMode.COD;
    private static final com.aps.domain.enumeration.PaymentMode UPDATED_PAYMENT_METHOD = com.aps.domain.enumeration.PaymentMode.LINK;

    private static final Instant DEFAULT_CONFIRMED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CONFIRMED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Long DEFAULT_REMOVED_CUSTOMER_ID = 1L;
    private static final Long UPDATED_REMOVED_CUSTOMER_ID = 2L;
    private static final Long SMALLER_REMOVED_CUSTOMER_ID = 1L - 1L;

    private static final Long DEFAULT_REMOVED_DELIVERY_PERSON_ID = 1L;
    private static final Long UPDATED_REMOVED_DELIVERY_PERSON_ID = 2L;
    private static final Long SMALLER_REMOVED_DELIVERY_PERSON_ID = 1L - 1L;

    private static final String DEFAULT_TRANSACTION_ID = "AAAAAAAAAA";
    private static final String UPDATED_TRANSACTION_ID = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/customer-orders";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    // Constants for DeliveryPerson creation
    private static final String DP_DEFAULT_NAME = "AAAAAAAAAA";
    private static final String DP_DEFAULT_WA_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String DP_DEFAULT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final com.aps.domain.enumeration.DeliveryStatus DP_DEFAULT_STATUS = com.aps.domain.enumeration.DeliveryStatus.FREE;
    private static final Boolean DP_DEFAULT_IS_ACTIVE = false;

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CustomerOrderRepository customerOrderRepository;

    @Mock
    private CustomerOrderRepository customerOrderRepositoryMock;

    @Autowired
    private CustomerOrderMapper customerOrderMapper;

    @Mock
    private CustomerOrderService customerOrderServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCustomerOrderMockMvc;

    private CustomerOrder customerOrder;

    private CustomerOrder insertedCustomerOrder;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CustomerOrder createEntity() {
        return new CustomerOrder()
                .orderTime(DEFAULT_ORDER_TIME)
                .totalAmount(DEFAULT_TOTAL_AMOUNT)
                .status(DEFAULT_STATUS)
                .paymentMethod(DEFAULT_PAYMENT_METHOD)
                .confirmedAt(DEFAULT_CONFIRMED_AT)
                .removedCustomerId(DEFAULT_REMOVED_CUSTOMER_ID)
                .removedDeliveryPersonId(DEFAULT_REMOVED_DELIVERY_PERSON_ID)
                .transactionId(DEFAULT_TRANSACTION_ID);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CustomerOrder createUpdatedEntity() {
        return new CustomerOrder()
                .orderTime(UPDATED_ORDER_TIME)
                .totalAmount(UPDATED_TOTAL_AMOUNT)
                .status(UPDATED_STATUS)
                .paymentMethod(UPDATED_PAYMENT_METHOD)
                .confirmedAt(UPDATED_CONFIRMED_AT)
                .removedCustomerId(UPDATED_REMOVED_CUSTOMER_ID)
                .removedDeliveryPersonId(UPDATED_REMOVED_DELIVERY_PERSON_ID)
                .transactionId(UPDATED_TRANSACTION_ID);
    }

    public static DeliveryPerson createDeliveryPersonEntity(EntityManager em) {
        DeliveryPerson deliveryPerson = new DeliveryPerson()
                .name(DP_DEFAULT_NAME)
                .waPhoneNumber(DP_DEFAULT_WA_PHONE_NUMBER)
                .phoneNumber(DP_DEFAULT_PHONE_NUMBER)
                .status(DP_DEFAULT_STATUS)
                .isActive(DP_DEFAULT_IS_ACTIVE);
        // Add required entity
        com.aps.domain.DeliveryZone deliveryZone;
        if (TestUtil.findAll(em, com.aps.domain.DeliveryZone.class).isEmpty()) {
            // Placeholder: Assume DeliveryZoneResourceIT.createEntity exists or create
            // manually
            // Using reflection or assuming DeliveryZoneResourceIT is available.
            // Safe fallback: Create manual zone if resourceIT fails
            deliveryZone = new com.aps.domain.DeliveryZone().zoneName("Zone A").pincode("123456");
            em.persist(deliveryZone);
            em.flush();
        } else {
            deliveryZone = TestUtil.findAll(em, com.aps.domain.DeliveryZone.class).get(0);
        }
        deliveryPerson.setZone(deliveryZone);
        return deliveryPerson;
    }

    @BeforeEach
    public void initTest() {
        customerOrder = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedCustomerOrder != null) {
            customerOrderRepository.delete(insertedCustomerOrder);
            insertedCustomerOrder = null;
        }
    }

    @Test
    @Transactional
    void createCustomerOrder() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CustomerOrder
        CustomerOrderDTO customerOrderDTO = customerOrderMapper.toDto(customerOrder);
        var returnedCustomerOrderDTO = om.readValue(
                restCustomerOrderMockMvc
                        .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsBytes(customerOrderDTO)))
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(),
                CustomerOrderDTO.class);

        // Validate the CustomerOrder in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCustomerOrder = customerOrderMapper.toEntity(returnedCustomerOrderDTO);
        assertCustomerOrderUpdatableFieldsEquals(returnedCustomerOrder,
                getPersistedCustomerOrder(returnedCustomerOrder));

        insertedCustomerOrder = returnedCustomerOrder;
    }

    @Test
    @Transactional
    void createCustomerOrderWithExistingId() throws Exception {
        // Create the CustomerOrder with an existing ID
        customerOrder.setId(1L);
        CustomerOrderDTO customerOrderDTO = customerOrderMapper.toDto(customerOrder);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCustomerOrderMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(customerOrderDTO)))
                .andExpect(status().isBadRequest());

        // Validate the CustomerOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkOrderTimeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        customerOrder.setOrderTime(null);

        // Create the CustomerOrder, which fails.
        CustomerOrderDTO customerOrderDTO = customerOrderMapper.toDto(customerOrder);

        restCustomerOrderMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(customerOrderDTO)))
                .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTotalAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        customerOrder.setTotalAmount(null);

        // Create the CustomerOrder, which fails.
        CustomerOrderDTO customerOrderDTO = customerOrderMapper.toDto(customerOrder);

        restCustomerOrderMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(customerOrderDTO)))
                .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        customerOrder.setStatus(null);

        // Create the CustomerOrder, which fails.
        CustomerOrderDTO customerOrderDTO = customerOrderMapper.toDto(customerOrder);

        restCustomerOrderMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(customerOrderDTO)))
                .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPaymentMethodIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        customerOrder.setPaymentMethod(null);

        // Create the CustomerOrder, which fails.
        CustomerOrderDTO customerOrderDTO = customerOrderMapper.toDto(customerOrder);

        restCustomerOrderMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(customerOrderDTO)))
                .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCustomerOrders() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList
        restCustomerOrderMockMvc
                .perform(get(ENTITY_API_URL + "?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(customerOrder.getId().intValue())))
                .andExpect(jsonPath("$.[*].orderTime").value(hasItem(DEFAULT_ORDER_TIME.toString())))
                .andExpect(jsonPath("$.[*].totalAmount").value(hasItem(sameNumber(DEFAULT_TOTAL_AMOUNT))))
                .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
                .andExpect(jsonPath("$.[*].paymentMethod").value(hasItem(DEFAULT_PAYMENT_METHOD.toString())))
                .andExpect(jsonPath("$.[*].confirmedAt").value(hasItem(DEFAULT_CONFIRMED_AT.toString())))
                .andExpect(jsonPath("$.[*].removedCustomerId").value(hasItem(DEFAULT_REMOVED_CUSTOMER_ID.intValue())))
                .andExpect(jsonPath("$.[*].removedDeliveryPersonId")
                        .value(hasItem(DEFAULT_REMOVED_DELIVERY_PERSON_ID.intValue())))
                .andExpect(jsonPath("$.[*].transactionId").value(hasItem(DEFAULT_TRANSACTION_ID)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCustomerOrdersWithEagerRelationshipsIsEnabled() throws Exception {
        when(customerOrderServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCustomerOrderMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(customerOrderServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCustomerOrdersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(customerOrderServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCustomerOrderMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(customerOrderRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getCustomerOrder() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.saveAndFlush(customerOrder);

        // Get the customerOrder
        restCustomerOrderMockMvc
                .perform(get(ENTITY_API_URL_ID, customerOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(customerOrder.getId().intValue()))
                .andExpect(jsonPath("$.orderTime").value(DEFAULT_ORDER_TIME.toString()))
                .andExpect(jsonPath("$.totalAmount").value(sameNumber(DEFAULT_TOTAL_AMOUNT)))
                .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
                .andExpect(jsonPath("$.paymentMethod").value(DEFAULT_PAYMENT_METHOD.toString()))
                .andExpect(jsonPath("$.confirmedAt").value(DEFAULT_CONFIRMED_AT.toString()))
                .andExpect(jsonPath("$.removedCustomerId").value(DEFAULT_REMOVED_CUSTOMER_ID.intValue()))
                .andExpect(jsonPath("$.removedDeliveryPersonId").value(DEFAULT_REMOVED_DELIVERY_PERSON_ID.intValue()))
                .andExpect(jsonPath("$.transactionId").value(DEFAULT_TRANSACTION_ID));
    }

    @Test
    @Transactional
    void getCustomerOrdersByIdFiltering() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.saveAndFlush(customerOrder);

        Long id = customerOrder.getId();

        defaultCustomerOrderFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCustomerOrderFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCustomerOrderFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCustomerOrdersByOrderTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where orderTime equals to
        defaultCustomerOrderFiltering("orderTime.equals=" + DEFAULT_ORDER_TIME,
                "orderTime.equals=" + UPDATED_ORDER_TIME);
    }

    @Test
    @Transactional
    void getAllCustomerOrdersByOrderTimeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where orderTime in
        defaultCustomerOrderFiltering(
                "orderTime.in=" + DEFAULT_ORDER_TIME + "," + UPDATED_ORDER_TIME,
                "orderTime.in=" + UPDATED_ORDER_TIME);
    }

    @Test
    @Transactional
    void getAllCustomerOrdersByOrderTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where orderTime is not null
        defaultCustomerOrderFiltering("orderTime.specified=true", "orderTime.specified=false");
    }

    @Test
    @Transactional
    void getAllCustomerOrdersByTotalAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where totalAmount equals to
        defaultCustomerOrderFiltering("totalAmount.equals=" + DEFAULT_TOTAL_AMOUNT,
                "totalAmount.equals=" + UPDATED_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllCustomerOrdersByTotalAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where totalAmount in
        defaultCustomerOrderFiltering(
                "totalAmount.in=" + DEFAULT_TOTAL_AMOUNT + "," + UPDATED_TOTAL_AMOUNT,
                "totalAmount.in=" + UPDATED_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllCustomerOrdersByTotalAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where totalAmount is not null
        defaultCustomerOrderFiltering("totalAmount.specified=true", "totalAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllCustomerOrdersByTotalAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where totalAmount is greater than or equal to
        defaultCustomerOrderFiltering(
                "totalAmount.greaterThanOrEqual=" + DEFAULT_TOTAL_AMOUNT,
                "totalAmount.greaterThanOrEqual=" + UPDATED_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllCustomerOrdersByTotalAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where totalAmount is less than or equal to
        defaultCustomerOrderFiltering(
                "totalAmount.lessThanOrEqual=" + DEFAULT_TOTAL_AMOUNT,
                "totalAmount.lessThanOrEqual=" + SMALLER_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllCustomerOrdersByTotalAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where totalAmount is less than
        defaultCustomerOrderFiltering("totalAmount.lessThan=" + UPDATED_TOTAL_AMOUNT,
                "totalAmount.lessThan=" + DEFAULT_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllCustomerOrdersByTotalAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where totalAmount is greater than
        defaultCustomerOrderFiltering("totalAmount.greaterThan=" + SMALLER_TOTAL_AMOUNT,
                "totalAmount.greaterThan=" + DEFAULT_TOTAL_AMOUNT);
    }

    @Test
    @Transactional
    void getAllCustomerOrdersByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where status equals to
        defaultCustomerOrderFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllCustomerOrdersByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where status in
        defaultCustomerOrderFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS,
                "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllCustomerOrdersByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where status is not null
        defaultCustomerOrderFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllCustomerOrdersByPaymentMethodIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where paymentMethod equals to
        defaultCustomerOrderFiltering("paymentMethod.equals=" + DEFAULT_PAYMENT_METHOD,
                "paymentMethod.equals=" + UPDATED_PAYMENT_METHOD);
    }

    @Test
    @Transactional
    void getAllCustomerOrdersByPaymentMethodIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where paymentMethod in
        defaultCustomerOrderFiltering(
                "paymentMethod.in=" + DEFAULT_PAYMENT_METHOD + "," + UPDATED_PAYMENT_METHOD,
                "paymentMethod.in=" + UPDATED_PAYMENT_METHOD);
    }

    @Test
    @Transactional
    void getAllCustomerOrdersByPaymentMethodIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where paymentMethod is not null
        defaultCustomerOrderFiltering("paymentMethod.specified=true", "paymentMethod.specified=false");
    }

    @Test
    @Transactional
    void getAllCustomerOrdersByPaymentMethodContainsSomething() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where paymentMethod contains
        defaultCustomerOrderFiltering(
                "paymentMethod.contains=" + DEFAULT_PAYMENT_METHOD,
                "paymentMethod.contains=" + UPDATED_PAYMENT_METHOD);
    }

    @Test
    @Transactional
    void getAllCustomerOrdersByPaymentMethodNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where paymentMethod does not contain
        defaultCustomerOrderFiltering(
                "paymentMethod.doesNotContain=" + UPDATED_PAYMENT_METHOD,
                "paymentMethod.doesNotContain=" + DEFAULT_PAYMENT_METHOD);
    }

    @Test
    @Transactional
    void getAllCustomerOrdersByConfirmedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where confirmedAt equals to
        defaultCustomerOrderFiltering("confirmedAt.equals=" + DEFAULT_CONFIRMED_AT,
                "confirmedAt.equals=" + UPDATED_CONFIRMED_AT);
    }

    @Test
    @Transactional
    void getAllCustomerOrdersByConfirmedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where confirmedAt in
        defaultCustomerOrderFiltering(
                "confirmedAt.in=" + DEFAULT_CONFIRMED_AT + "," + UPDATED_CONFIRMED_AT,
                "confirmedAt.in=" + UPDATED_CONFIRMED_AT);
    }

    @Test
    @Transactional
    void getAllCustomerOrdersByConfirmedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where confirmedAt is not null
        defaultCustomerOrderFiltering("confirmedAt.specified=true", "confirmedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllCustomerOrdersByRemovedCustomerIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where removedCustomerId equals to
        defaultCustomerOrderFiltering(
                "removedCustomerId.equals=" + DEFAULT_REMOVED_CUSTOMER_ID,
                "removedCustomerId.equals=" + UPDATED_REMOVED_CUSTOMER_ID);
    }

    @Test
    @Transactional
    void getAllCustomerOrdersByRemovedCustomerIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where removedCustomerId in
        defaultCustomerOrderFiltering(
                "removedCustomerId.in=" + DEFAULT_REMOVED_CUSTOMER_ID + "," + UPDATED_REMOVED_CUSTOMER_ID,
                "removedCustomerId.in=" + UPDATED_REMOVED_CUSTOMER_ID);
    }

    @Test
    @Transactional
    void getAllCustomerOrdersByRemovedCustomerIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where removedCustomerId is not null
        defaultCustomerOrderFiltering("removedCustomerId.specified=true", "removedCustomerId.specified=false");
    }

    @Test
    @Transactional
    void getAllCustomerOrdersByRemovedCustomerIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where removedCustomerId is greater than or
        // equal to
        defaultCustomerOrderFiltering(
                "removedCustomerId.greaterThanOrEqual=" + DEFAULT_REMOVED_CUSTOMER_ID,
                "removedCustomerId.greaterThanOrEqual=" + UPDATED_REMOVED_CUSTOMER_ID);
    }

    @Test
    @Transactional
    void getAllCustomerOrdersByRemovedCustomerIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where removedCustomerId is less than or equal
        // to
        defaultCustomerOrderFiltering(
                "removedCustomerId.lessThanOrEqual=" + DEFAULT_REMOVED_CUSTOMER_ID,
                "removedCustomerId.lessThanOrEqual=" + SMALLER_REMOVED_CUSTOMER_ID);
    }

    @Test
    @Transactional
    void getAllCustomerOrdersByRemovedCustomerIdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where removedCustomerId is less than
        defaultCustomerOrderFiltering(
                "removedCustomerId.lessThan=" + UPDATED_REMOVED_CUSTOMER_ID,
                "removedCustomerId.lessThan=" + DEFAULT_REMOVED_CUSTOMER_ID);
    }

    @Test
    @Transactional
    void getAllCustomerOrdersByRemovedCustomerIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where removedCustomerId is greater than
        defaultCustomerOrderFiltering(
                "removedCustomerId.greaterThan=" + SMALLER_REMOVED_CUSTOMER_ID,
                "removedCustomerId.greaterThan=" + DEFAULT_REMOVED_CUSTOMER_ID);
    }

    @Test
    @Transactional
    void getAllCustomerOrdersByRemovedDeliveryPersonIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where removedDeliveryPersonId equals to
        defaultCustomerOrderFiltering(
                "removedDeliveryPersonId.equals=" + DEFAULT_REMOVED_DELIVERY_PERSON_ID,
                "removedDeliveryPersonId.equals=" + UPDATED_REMOVED_DELIVERY_PERSON_ID);
    }

    @Test
    @Transactional
    void getAllCustomerOrdersByRemovedDeliveryPersonIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where removedDeliveryPersonId in
        defaultCustomerOrderFiltering(
                "removedDeliveryPersonId.in=" + DEFAULT_REMOVED_DELIVERY_PERSON_ID + ","
                        + UPDATED_REMOVED_DELIVERY_PERSON_ID,
                "removedDeliveryPersonId.in=" + UPDATED_REMOVED_DELIVERY_PERSON_ID);
    }

    @Test
    @Transactional
    void getAllCustomerOrdersByRemovedDeliveryPersonIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where removedDeliveryPersonId is not null
        defaultCustomerOrderFiltering("removedDeliveryPersonId.specified=true",
                "removedDeliveryPersonId.specified=false");
    }

    @Test
    @Transactional
    void getAllCustomerOrdersByRemovedDeliveryPersonIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where removedDeliveryPersonId is greater than
        // or equal to
        defaultCustomerOrderFiltering(
                "removedDeliveryPersonId.greaterThanOrEqual=" + DEFAULT_REMOVED_DELIVERY_PERSON_ID,
                "removedDeliveryPersonId.greaterThanOrEqual=" + UPDATED_REMOVED_DELIVERY_PERSON_ID);
    }

    @Test
    @Transactional
    void getAllCustomerOrdersByRemovedDeliveryPersonIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where removedDeliveryPersonId is less than or
        // equal to
        defaultCustomerOrderFiltering(
                "removedDeliveryPersonId.lessThanOrEqual=" + DEFAULT_REMOVED_DELIVERY_PERSON_ID,
                "removedDeliveryPersonId.lessThanOrEqual=" + SMALLER_REMOVED_DELIVERY_PERSON_ID);
    }

    @Test
    @Transactional
    void getAllCustomerOrdersByRemovedDeliveryPersonIdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where removedDeliveryPersonId is less than
        defaultCustomerOrderFiltering(
                "removedDeliveryPersonId.lessThan=" + UPDATED_REMOVED_DELIVERY_PERSON_ID,
                "removedDeliveryPersonId.lessThan=" + DEFAULT_REMOVED_DELIVERY_PERSON_ID);
    }

    @Test
    @Transactional
    void getAllCustomerOrdersByRemovedDeliveryPersonIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where removedDeliveryPersonId is greater than
        defaultCustomerOrderFiltering(
                "removedDeliveryPersonId.greaterThan=" + SMALLER_REMOVED_DELIVERY_PERSON_ID,
                "removedDeliveryPersonId.greaterThan=" + DEFAULT_REMOVED_DELIVERY_PERSON_ID);
    }

    @Test
    @Transactional
    void getAllCustomerOrdersByTransactionIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where transactionId equals to
        defaultCustomerOrderFiltering("transactionId.equals=" + DEFAULT_TRANSACTION_ID,
                "transactionId.equals=" + UPDATED_TRANSACTION_ID);
    }

    @Test
    @Transactional
    void getAllCustomerOrdersByTransactionIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where transactionId in
        defaultCustomerOrderFiltering(
                "transactionId.in=" + DEFAULT_TRANSACTION_ID + "," + UPDATED_TRANSACTION_ID,
                "transactionId.in=" + UPDATED_TRANSACTION_ID);
    }

    @Test
    @Transactional
    void getAllCustomerOrdersByTransactionIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where transactionId is not null
        defaultCustomerOrderFiltering("transactionId.specified=true", "transactionId.specified=false");
    }

    @Test
    @Transactional
    void getAllCustomerOrdersByTransactionIdContainsSomething() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where transactionId contains
        defaultCustomerOrderFiltering(
                "transactionId.contains=" + DEFAULT_TRANSACTION_ID,
                "transactionId.contains=" + UPDATED_TRANSACTION_ID);
    }

    @Test
    @Transactional
    void getAllCustomerOrdersByTransactionIdNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where transactionId does not contain
        defaultCustomerOrderFiltering(
                "transactionId.doesNotContain=" + UPDATED_TRANSACTION_ID,
                "transactionId.doesNotContain=" + DEFAULT_TRANSACTION_ID);
    }

    @Test
    @Transactional
    void getAllCustomerOrdersByHistoryIsEqualToSomething() throws Exception {
        OrderStatusHistory history;
        if (TestUtil.findAll(em, OrderStatusHistory.class).isEmpty()) {
            customerOrderRepository.saveAndFlush(customerOrder);
            history = OrderStatusHistoryResourceIT.createEntity();
        } else {
            history = TestUtil.findAll(em, OrderStatusHistory.class).get(0);
        }
        em.persist(history);
        em.flush();
        customerOrder.setHistory(history);
        customerOrderRepository.saveAndFlush(customerOrder);
        Long historyId = history.getId();
        // Get all the customerOrderList where history equals to historyId
        defaultCustomerOrderShouldBeFound("historyId.equals=" + historyId);

        // Get all the customerOrderList where history equals to (historyId + 1)
        defaultCustomerOrderShouldNotBeFound("historyId.equals=" + (historyId + 1));
    }

    @Test
    @Transactional
    void getAllCustomerOrdersByCustomerIsEqualToSomething() throws Exception {
        Customer customer;
        if (TestUtil.findAll(em, Customer.class).isEmpty()) {
            customerOrderRepository.saveAndFlush(customerOrder);
            customer = CustomerResourceIT.createEntity();
        } else {
            customer = TestUtil.findAll(em, Customer.class).get(0);
        }
        em.persist(customer);
        em.flush();
        customerOrder.setCustomer(customer);
        customerOrderRepository.saveAndFlush(customerOrder);
        Long customerId = customer.getId();
        // Get all the customerOrderList where customer equals to customerId
        defaultCustomerOrderShouldBeFound("customerId.equals=" + customerId);

        // Get all the customerOrderList where customer equals to (customerId + 1)
        defaultCustomerOrderShouldNotBeFound("customerId.equals=" + (customerId + 1));
    }

    @Test
    @Transactional
    void getAllCustomerOrdersByDeliveryPersonIsEqualToSomething() throws Exception {
        DeliveryPerson deliveryPerson;
        if (TestUtil.findAll(em, DeliveryPerson.class).isEmpty()) {
            customerOrderRepository.saveAndFlush(customerOrder);
            deliveryPerson = createDeliveryPersonEntity(em);
        } else {
            deliveryPerson = TestUtil.findAll(em, DeliveryPerson.class).get(0);
        }
        em.persist(deliveryPerson);
        em.flush();
        customerOrder.setDeliveryPerson(deliveryPerson);
        customerOrderRepository.saveAndFlush(customerOrder);
        Long deliveryPersonId = deliveryPerson.getId();
        // Get all the customerOrderList where deliveryPerson equals to deliveryPersonId
        defaultCustomerOrderShouldBeFound("deliveryPersonId.equals=" + deliveryPersonId);

        // Get all the customerOrderList where deliveryPerson equals to
        // (deliveryPersonId + 1)
        defaultCustomerOrderShouldNotBeFound("deliveryPersonId.equals=" + (deliveryPersonId + 1));
    }

    private void defaultCustomerOrderFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCustomerOrderShouldBeFound(shouldBeFound);
        defaultCustomerOrderShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCustomerOrderShouldBeFound(String filter) throws Exception {
        restCustomerOrderMockMvc
                .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(customerOrder.getId().intValue())))
                .andExpect(jsonPath("$.[*].orderTime").value(hasItem(DEFAULT_ORDER_TIME.toString())))
                .andExpect(jsonPath("$.[*].totalAmount").value(hasItem(sameNumber(DEFAULT_TOTAL_AMOUNT))))
                .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
                .andExpect(jsonPath("$.[*].paymentMethod").value(hasItem(DEFAULT_PAYMENT_METHOD)))
                .andExpect(jsonPath("$.[*].confirmedAt").value(hasItem(DEFAULT_CONFIRMED_AT.toString())))
                .andExpect(jsonPath("$.[*].removedCustomerId").value(hasItem(DEFAULT_REMOVED_CUSTOMER_ID.intValue())))
                .andExpect(jsonPath("$.[*].removedDeliveryPersonId")
                        .value(hasItem(DEFAULT_REMOVED_DELIVERY_PERSON_ID.intValue())))
                .andExpect(jsonPath("$.[*].transactionId").value(hasItem(DEFAULT_TRANSACTION_ID)));

        // Check, that the count call also returns 1
        restCustomerOrderMockMvc
                .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCustomerOrderShouldNotBeFound(String filter) throws Exception {
        restCustomerOrderMockMvc
                .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCustomerOrderMockMvc
                .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCustomerOrder() throws Exception {
        // Get the customerOrder
        restCustomerOrderMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCustomerOrder() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.saveAndFlush(customerOrder);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the customerOrder
        CustomerOrder updatedCustomerOrder = customerOrderRepository.findById(customerOrder.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCustomerOrder are not
        // directly saved in db
        em.detach(updatedCustomerOrder);
        updatedCustomerOrder
                .orderTime(UPDATED_ORDER_TIME)
                .totalAmount(UPDATED_TOTAL_AMOUNT)
                .status(UPDATED_STATUS)
                .paymentMethod(UPDATED_PAYMENT_METHOD)
                .confirmedAt(UPDATED_CONFIRMED_AT)
                .removedCustomerId(UPDATED_REMOVED_CUSTOMER_ID)
                .removedDeliveryPersonId(UPDATED_REMOVED_DELIVERY_PERSON_ID)
                .transactionId(UPDATED_TRANSACTION_ID);
        CustomerOrderDTO customerOrderDTO = customerOrderMapper.toDto(updatedCustomerOrder);

        restCustomerOrderMockMvc
                .perform(
                        put(ENTITY_API_URL_ID, customerOrderDTO.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsBytes(customerOrderDTO)))
                .andExpect(status().isOk());

        // Validate the CustomerOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCustomerOrderToMatchAllProperties(updatedCustomerOrder);
    }

    @Test
    @Transactional
    void putNonExistingCustomerOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customerOrder.setId(longCount.incrementAndGet());

        // Create the CustomerOrder
        CustomerOrderDTO customerOrderDTO = customerOrderMapper.toDto(customerOrder);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCustomerOrderMockMvc
                .perform(
                        put(ENTITY_API_URL_ID, customerOrderDTO.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsBytes(customerOrderDTO)))
                .andExpect(status().isBadRequest());

        // Validate the CustomerOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCustomerOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customerOrder.setId(longCount.incrementAndGet());

        // Create the CustomerOrder
        CustomerOrderDTO customerOrderDTO = customerOrderMapper.toDto(customerOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomerOrderMockMvc
                .perform(
                        put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsBytes(customerOrderDTO)))
                .andExpect(status().isBadRequest());

        // Validate the CustomerOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCustomerOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customerOrder.setId(longCount.incrementAndGet());

        // Create the CustomerOrder
        CustomerOrderDTO customerOrderDTO = customerOrderMapper.toDto(customerOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomerOrderMockMvc
                .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(customerOrderDTO)))
                .andExpect(status().isMethodNotAllowed());

        // Validate the CustomerOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCustomerOrderWithPatch() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.saveAndFlush(customerOrder);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the customerOrder using partial update
        CustomerOrder partialUpdatedCustomerOrder = new CustomerOrder();
        partialUpdatedCustomerOrder.setId(customerOrder.getId());

        partialUpdatedCustomerOrder
                .orderTime(UPDATED_ORDER_TIME)
                .status(UPDATED_STATUS)
                .paymentMethod(UPDATED_PAYMENT_METHOD)
                .confirmedAt(UPDATED_CONFIRMED_AT)
                .transactionId(UPDATED_TRANSACTION_ID);

        restCustomerOrderMockMvc
                .perform(
                        patch(ENTITY_API_URL_ID, partialUpdatedCustomerOrder.getId())
                                .contentType("application/merge-patch+json")
                                .content(om.writeValueAsBytes(partialUpdatedCustomerOrder)))
                .andExpect(status().isOk());

        // Validate the CustomerOrder in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCustomerOrderUpdatableFieldsEquals(
                createUpdateProxyForBean(partialUpdatedCustomerOrder, customerOrder),
                getPersistedCustomerOrder(customerOrder));
    }

    @Test
    @Transactional
    void fullUpdateCustomerOrderWithPatch() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.saveAndFlush(customerOrder);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the customerOrder using partial update
        CustomerOrder partialUpdatedCustomerOrder = new CustomerOrder();
        partialUpdatedCustomerOrder.setId(customerOrder.getId());

        partialUpdatedCustomerOrder
                .orderTime(UPDATED_ORDER_TIME)
                .totalAmount(UPDATED_TOTAL_AMOUNT)
                .status(UPDATED_STATUS)
                .paymentMethod(UPDATED_PAYMENT_METHOD)
                .confirmedAt(UPDATED_CONFIRMED_AT)
                .removedCustomerId(UPDATED_REMOVED_CUSTOMER_ID)
                .removedDeliveryPersonId(UPDATED_REMOVED_DELIVERY_PERSON_ID)
                .transactionId(UPDATED_TRANSACTION_ID);

        restCustomerOrderMockMvc
                .perform(
                        patch(ENTITY_API_URL_ID, partialUpdatedCustomerOrder.getId())
                                .contentType("application/merge-patch+json")
                                .content(om.writeValueAsBytes(partialUpdatedCustomerOrder)))
                .andExpect(status().isOk());

        // Validate the CustomerOrder in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCustomerOrderUpdatableFieldsEquals(partialUpdatedCustomerOrder,
                getPersistedCustomerOrder(partialUpdatedCustomerOrder));
    }

    @Test
    @Transactional
    void patchNonExistingCustomerOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customerOrder.setId(longCount.incrementAndGet());

        // Create the CustomerOrder
        CustomerOrderDTO customerOrderDTO = customerOrderMapper.toDto(customerOrder);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCustomerOrderMockMvc
                .perform(
                        patch(ENTITY_API_URL_ID, customerOrderDTO.getId())
                                .contentType("application/merge-patch+json")
                                .content(om.writeValueAsBytes(customerOrderDTO)))
                .andExpect(status().isBadRequest());

        // Validate the CustomerOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCustomerOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customerOrder.setId(longCount.incrementAndGet());

        // Create the CustomerOrder
        CustomerOrderDTO customerOrderDTO = customerOrderMapper.toDto(customerOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomerOrderMockMvc
                .perform(
                        patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                                .contentType("application/merge-patch+json")
                                .content(om.writeValueAsBytes(customerOrderDTO)))
                .andExpect(status().isBadRequest());

        // Validate the CustomerOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCustomerOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customerOrder.setId(longCount.incrementAndGet());

        // Create the CustomerOrder
        CustomerOrderDTO customerOrderDTO = customerOrderMapper.toDto(customerOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomerOrderMockMvc
                .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json")
                        .content(om.writeValueAsBytes(customerOrderDTO)))
                .andExpect(status().isMethodNotAllowed());

        // Validate the CustomerOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCustomerOrder() throws Exception {
        // Initialize the database
        insertedCustomerOrder = customerOrderRepository.saveAndFlush(customerOrder);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the customerOrder
        restCustomerOrderMockMvc
                .perform(delete(ENTITY_API_URL_ID, customerOrder.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return customerOrderRepository.count();
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

    protected CustomerOrder getPersistedCustomerOrder(CustomerOrder customerOrder) {
        return customerOrderRepository.findById(customerOrder.getId()).orElseThrow();
    }

    protected void assertPersistedCustomerOrderToMatchAllProperties(CustomerOrder expectedCustomerOrder) {
        assertCustomerOrderAllPropertiesEquals(expectedCustomerOrder, getPersistedCustomerOrder(expectedCustomerOrder));
    }

    protected void assertPersistedCustomerOrderToMatchUpdatableProperties(CustomerOrder expectedCustomerOrder) {
        assertCustomerOrderAllUpdatablePropertiesEquals(expectedCustomerOrder,
                getPersistedCustomerOrder(expectedCustomerOrder));
    }
}
