package com.aps.web.rest;

import static com.aps.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.aps.IntegrationTest;
import com.aps.domain.Customer;
import com.aps.domain.CustomerOrder;
import com.aps.domain.ReturnedOrder;
import com.aps.domain.enumeration.ReturnProductStatus;
import com.aps.repository.ReturnedOrderRepository;
import com.aps.service.ReturnedOrderService;
import com.aps.service.dto.ReturnedOrderDTO;
import com.aps.service.mapper.ReturnedOrderMapper;
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
 * Integration tests for the {@link ReturnedOrderResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ReturnedOrderResourceIT {

    private static final Instant DEFAULT_RETURN_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_RETURN_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_PAYMENT_RECEIVED_MODE = "AAAAAAAAAA";
    private static final String UPDATED_PAYMENT_RECEIVED_MODE = "BBBBBBBBBB";

    private static final String DEFAULT_PAYMENT_RETURNED_MODE = "AAAAAAAAAA";
    private static final String UPDATED_PAYMENT_RETURNED_MODE = "BBBBBBBBBB";

    private static final ReturnProductStatus DEFAULT_PRODUCT_CLAIM_STATUS = ReturnProductStatus.PENDING_RECEIPT;
    private static final ReturnProductStatus UPDATED_PRODUCT_CLAIM_STATUS = ReturnProductStatus.RECEIVED_DAMAGED;

    private static final BigDecimal DEFAULT_REFUND_AMOUNT = new BigDecimal(0);
    private static final BigDecimal UPDATED_REFUND_AMOUNT = new BigDecimal(1);
    private static final BigDecimal SMALLER_REFUND_AMOUNT = new BigDecimal(0 - 1);

    private static final String ENTITY_API_URL = "/api/returned-orders";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ReturnedOrderRepository returnedOrderRepository;

    @Mock
    private ReturnedOrderRepository returnedOrderRepositoryMock;

    @Autowired
    private ReturnedOrderMapper returnedOrderMapper;

    @Mock
    private ReturnedOrderService returnedOrderServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restReturnedOrderMockMvc;

    private ReturnedOrder returnedOrder;

    private ReturnedOrder insertedReturnedOrder;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ReturnedOrder createEntity(EntityManager em) {
        ReturnedOrder returnedOrder = new ReturnedOrder()
                .returnDate(DEFAULT_RETURN_DATE)
                .paymentReceivedMode(DEFAULT_PAYMENT_RECEIVED_MODE)
                .paymentReturnedMode(DEFAULT_PAYMENT_RETURNED_MODE)
                .productClaimStatus(DEFAULT_PRODUCT_CLAIM_STATUS)
                .refundAmount(DEFAULT_REFUND_AMOUNT);
        // Add required entity
        CustomerOrder customerOrder;
        if (TestUtil.findAll(em, CustomerOrder.class).isEmpty()) {
            customerOrder = CustomerOrderResourceIT.createEntity();
            em.persist(customerOrder);
            em.flush();
        } else {
            customerOrder = TestUtil.findAll(em, CustomerOrder.class).get(0);
        }
        returnedOrder.setOrder(customerOrder);
        // Add required entity
        Customer customer;
        if (TestUtil.findAll(em, Customer.class).isEmpty()) {
            customer = CustomerResourceIT.createEntity();
            em.persist(customer);
            em.flush();
        } else {
            customer = TestUtil.findAll(em, Customer.class).get(0);
        }
        returnedOrder.setCustomer(customer);
        return returnedOrder;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ReturnedOrder createUpdatedEntity(EntityManager em) {
        ReturnedOrder returnedOrder = new ReturnedOrder()
                .returnDate(UPDATED_RETURN_DATE)
                .paymentReceivedMode(UPDATED_PAYMENT_RECEIVED_MODE)
                .paymentReturnedMode(UPDATED_PAYMENT_RETURNED_MODE)
                .productClaimStatus(UPDATED_PRODUCT_CLAIM_STATUS)
                .refundAmount(UPDATED_REFUND_AMOUNT);
        // Add required entity
        CustomerOrder customerOrder;
        if (TestUtil.findAll(em, CustomerOrder.class).isEmpty()) {
            customerOrder = CustomerOrderResourceIT.createUpdatedEntity();
            em.persist(customerOrder);
            em.flush();
        } else {
            customerOrder = TestUtil.findAll(em, CustomerOrder.class).get(0);
        }
        returnedOrder.setOrder(customerOrder);
        // Add required entity
        Customer customer;
        if (TestUtil.findAll(em, Customer.class).isEmpty()) {
            customer = CustomerResourceIT.createUpdatedEntity();
            em.persist(customer);
            em.flush();
        } else {
            customer = TestUtil.findAll(em, Customer.class).get(0);
        }
        returnedOrder.setCustomer(customer);
        return returnedOrder;
    }

    @BeforeEach
    public void initTest() {
        returnedOrder = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedReturnedOrder != null) {
            returnedOrderRepository.delete(insertedReturnedOrder);
            insertedReturnedOrder = null;
        }
    }

    @Test
    @Transactional
    void createReturnedOrder() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ReturnedOrder
        ReturnedOrderDTO returnedOrderDTO = returnedOrderMapper.toDto(returnedOrder);
        var returnedReturnedOrderDTO = om.readValue(
                restReturnedOrderMockMvc
                        .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsBytes(returnedOrderDTO)))
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(),
                ReturnedOrderDTO.class);

        // Validate the ReturnedOrder in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedReturnedOrder = returnedOrderMapper.toEntity(returnedReturnedOrderDTO);
        assertReturnedOrderUpdatableFieldsEquals(returnedReturnedOrder,
                getPersistedReturnedOrder(returnedReturnedOrder));

        insertedReturnedOrder = returnedReturnedOrder;
    }

    @Test
    @Transactional
    void createReturnedOrderWithExistingId() throws Exception {
        // Create the ReturnedOrder with an existing ID
        returnedOrder.setId(1L);
        ReturnedOrderDTO returnedOrderDTO = returnedOrderMapper.toDto(returnedOrder);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restReturnedOrderMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(returnedOrderDTO)))
                .andExpect(status().isBadRequest());

        // Validate the ReturnedOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllReturnedOrders() throws Exception {
        // Initialize the database
        insertedReturnedOrder = returnedOrderRepository.saveAndFlush(returnedOrder);

        // Get all the returnedOrderList
        restReturnedOrderMockMvc
                .perform(get(ENTITY_API_URL + "?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(returnedOrder.getId().intValue())))
                .andExpect(jsonPath("$.[*].returnDate").value(hasItem(DEFAULT_RETURN_DATE.toString())))
                .andExpect(jsonPath("$.[*].paymentReceivedMode").value(hasItem(DEFAULT_PAYMENT_RECEIVED_MODE)))
                .andExpect(jsonPath("$.[*].paymentReturnedMode").value(hasItem(DEFAULT_PAYMENT_RETURNED_MODE)))
                .andExpect(jsonPath("$.[*].productClaimStatus").value(hasItem(DEFAULT_PRODUCT_CLAIM_STATUS.toString())))
                .andExpect(jsonPath("$.[*].refundAmount").value(hasItem(DEFAULT_REFUND_AMOUNT.intValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllReturnedOrdersWithEagerRelationshipsIsEnabled() throws Exception {
        when(returnedOrderServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restReturnedOrderMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(returnedOrderServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllReturnedOrdersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(returnedOrderServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restReturnedOrderMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(returnedOrderRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getReturnedOrder() throws Exception {
        // Initialize the database
        insertedReturnedOrder = returnedOrderRepository.saveAndFlush(returnedOrder);

        // Get the returnedOrder
        restReturnedOrderMockMvc
                .perform(get(ENTITY_API_URL_ID, returnedOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(returnedOrder.getId().intValue()))
                .andExpect(jsonPath("$.returnDate").value(DEFAULT_RETURN_DATE.toString()))
                .andExpect(jsonPath("$.paymentReceivedMode").value(DEFAULT_PAYMENT_RECEIVED_MODE))
                .andExpect(jsonPath("$.paymentReturnedMode").value(DEFAULT_PAYMENT_RETURNED_MODE))
                .andExpect(jsonPath("$.productClaimStatus").value(DEFAULT_PRODUCT_CLAIM_STATUS.toString()))
                .andExpect(jsonPath("$.refundAmount").value(DEFAULT_REFUND_AMOUNT.intValue()));
    }

    @Test
    @Transactional
    void getNonExistingReturnedOrder() throws Exception {
        // Get the returnedOrder
        restReturnedOrderMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingReturnedOrder() throws Exception {
        // Initialize the database
        insertedReturnedOrder = returnedOrderRepository.saveAndFlush(returnedOrder);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the returnedOrder
        ReturnedOrder updatedReturnedOrder = returnedOrderRepository.findById(returnedOrder.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedReturnedOrder are not
        // directly saved in db
        em.detach(updatedReturnedOrder);
        updatedReturnedOrder
                .returnDate(UPDATED_RETURN_DATE)
                .paymentReceivedMode(UPDATED_PAYMENT_RECEIVED_MODE)
                .paymentReturnedMode(UPDATED_PAYMENT_RETURNED_MODE)
                .productClaimStatus(UPDATED_PRODUCT_CLAIM_STATUS)
                .refundAmount(UPDATED_REFUND_AMOUNT);
        ReturnedOrderDTO returnedOrderDTO = returnedOrderMapper.toDto(updatedReturnedOrder);

        restReturnedOrderMockMvc
                .perform(
                        put(ENTITY_API_URL_ID, returnedOrderDTO.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsBytes(returnedOrderDTO)))
                .andExpect(status().isOk());

        // Validate the ReturnedOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedReturnedOrderToMatchAllProperties(updatedReturnedOrder);
    }

    @Test
    @Transactional
    void putNonExistingReturnedOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        returnedOrder.setId(longCount.incrementAndGet());

        // Create the ReturnedOrder
        ReturnedOrderDTO returnedOrderDTO = returnedOrderMapper.toDto(returnedOrder);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReturnedOrderMockMvc
                .perform(
                        put(ENTITY_API_URL_ID, returnedOrderDTO.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsBytes(returnedOrderDTO)))
                .andExpect(status().isBadRequest());

        // Validate the ReturnedOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteReturnedOrder() throws Exception {
        // Initialize the database
        insertedReturnedOrder = returnedOrderRepository.saveAndFlush(returnedOrder);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the returnedOrder
        restReturnedOrderMockMvc
                .perform(delete(ENTITY_API_URL_ID, returnedOrder.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return returnedOrderRepository.count();
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

    protected ReturnedOrder getPersistedReturnedOrder(ReturnedOrder returnedOrder) {
        return returnedOrderRepository.findById(returnedOrder.getId()).orElseThrow();
    }

    protected void assertPersistedReturnedOrderToMatchAllProperties(ReturnedOrder expectedReturnedOrder) {
        assertReturnedOrderAllPropertiesEquals(expectedReturnedOrder, getPersistedReturnedOrder(expectedReturnedOrder));
    }

    protected void assertReturnedOrderAllPropertiesEquals(ReturnedOrder expected, ReturnedOrder actual) {
        assertThat(actual)
                .extracting("returnDate", "paymentReceivedMode", "paymentReturnedMode", "productClaimStatus")
                .contains(expected.getReturnDate(), expected.getPaymentReceivedMode(),
                        expected.getPaymentReturnedMode(), expected.getProductClaimStatus());
        assertThat(actual.getRefundAmount()).isEqualByComparingTo(expected.getRefundAmount());
    }

    protected void assertReturnedOrderUpdatableFieldsEquals(ReturnedOrder expected, ReturnedOrder actual) {
        assertThat(actual)
                .extracting("returnDate", "paymentReceivedMode", "paymentReturnedMode", "productClaimStatus")
                .contains(expected.getReturnDate(), expected.getPaymentReceivedMode(),
                        expected.getPaymentReturnedMode(), expected.getProductClaimStatus());
        assertThat(actual.getRefundAmount()).isEqualByComparingTo(expected.getRefundAmount());
    }
}
