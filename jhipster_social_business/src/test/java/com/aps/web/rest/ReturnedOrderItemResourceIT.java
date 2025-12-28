package com.aps.web.rest;

import static com.aps.domain.ReturnedOrderItemAsserts.*;
import static com.aps.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.aps.IntegrationTest;
import com.aps.domain.FishProduct;
import com.aps.domain.ReturnedOrder;
import com.aps.domain.ReturnedOrderItem;
import com.aps.repository.ReturnedOrderItemRepository;
import com.aps.service.ReturnedOrderItemService;
import com.aps.service.dto.ReturnedOrderItemDTO;
import com.aps.service.mapper.ReturnedOrderItemMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link ReturnedOrderItemResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ReturnedOrderItemResourceIT {

    private static final Double DEFAULT_QUANTITY = 1D;
    private static final Double UPDATED_QUANTITY = 2D;
    private static final Double SMALLER_QUANTITY = 1D - 1D;

    private static final String DEFAULT_PRODUCT_COMMENT = "AAAAAAAAAA";
    private static final String UPDATED_PRODUCT_COMMENT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/returned-order-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ReturnedOrderItemRepository returnedOrderItemRepository;

    @Mock
    private ReturnedOrderItemRepository returnedOrderItemRepositoryMock;

    @Autowired
    private ReturnedOrderItemMapper returnedOrderItemMapper;

    @Mock
    private ReturnedOrderItemService returnedOrderItemServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restReturnedOrderItemMockMvc;

    private ReturnedOrderItem returnedOrderItem;

    private ReturnedOrderItem insertedReturnedOrderItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ReturnedOrderItem createEntity(EntityManager em) {
        ReturnedOrderItem returnedOrderItem = new ReturnedOrderItem().quantity(DEFAULT_QUANTITY).productComment(DEFAULT_PRODUCT_COMMENT);
        // Add required entity
        FishProduct fishProduct;
        if (TestUtil.findAll(em, FishProduct.class).isEmpty()) {
            fishProduct = FishProductResourceIT.createEntity();
            em.persist(fishProduct);
            em.flush();
        } else {
            fishProduct = TestUtil.findAll(em, FishProduct.class).get(0);
        }
        returnedOrderItem.setProduct(fishProduct);
        // Add required entity
        ReturnedOrder returnedOrder;
        if (TestUtil.findAll(em, ReturnedOrder.class).isEmpty()) {
            returnedOrder = ReturnedOrderResourceIT.createEntity(em);
            em.persist(returnedOrder);
            em.flush();
        } else {
            returnedOrder = TestUtil.findAll(em, ReturnedOrder.class).get(0);
        }
        returnedOrderItem.setReturnedOrder(returnedOrder);
        return returnedOrderItem;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ReturnedOrderItem createUpdatedEntity(EntityManager em) {
        ReturnedOrderItem updatedReturnedOrderItem = new ReturnedOrderItem()
            .quantity(UPDATED_QUANTITY)
            .productComment(UPDATED_PRODUCT_COMMENT);
        // Add required entity
        FishProduct fishProduct;
        if (TestUtil.findAll(em, FishProduct.class).isEmpty()) {
            fishProduct = FishProductResourceIT.createUpdatedEntity();
            em.persist(fishProduct);
            em.flush();
        } else {
            fishProduct = TestUtil.findAll(em, FishProduct.class).get(0);
        }
        updatedReturnedOrderItem.setProduct(fishProduct);
        // Add required entity
        ReturnedOrder returnedOrder;
        if (TestUtil.findAll(em, ReturnedOrder.class).isEmpty()) {
            returnedOrder = ReturnedOrderResourceIT.createUpdatedEntity(em);
            em.persist(returnedOrder);
            em.flush();
        } else {
            returnedOrder = TestUtil.findAll(em, ReturnedOrder.class).get(0);
        }
        updatedReturnedOrderItem.setReturnedOrder(returnedOrder);
        return updatedReturnedOrderItem;
    }

    @BeforeEach
    public void initTest() {
        returnedOrderItem = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedReturnedOrderItem != null) {
            returnedOrderItemRepository.delete(insertedReturnedOrderItem);
            insertedReturnedOrderItem = null;
        }
    }

    @Test
    @Transactional
    void createReturnedOrderItem() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ReturnedOrderItem
        ReturnedOrderItemDTO returnedOrderItemDTO = returnedOrderItemMapper.toDto(returnedOrderItem);
        var returnedReturnedOrderItemDTO = om.readValue(
            restReturnedOrderItemMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(returnedOrderItemDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ReturnedOrderItemDTO.class
        );

        // Validate the ReturnedOrderItem in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedReturnedOrderItem = returnedOrderItemMapper.toEntity(returnedReturnedOrderItemDTO);
        assertReturnedOrderItemUpdatableFieldsEquals(returnedReturnedOrderItem, getPersistedReturnedOrderItem(returnedReturnedOrderItem));

        insertedReturnedOrderItem = returnedReturnedOrderItem;
    }

    @Test
    @Transactional
    void createReturnedOrderItemWithExistingId() throws Exception {
        // Create the ReturnedOrderItem with an existing ID
        returnedOrderItem.setId(1L);
        ReturnedOrderItemDTO returnedOrderItemDTO = returnedOrderItemMapper.toDto(returnedOrderItem);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restReturnedOrderItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(returnedOrderItemDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ReturnedOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkQuantityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        returnedOrderItem.setQuantity(null);

        // Create the ReturnedOrderItem, which fails.
        ReturnedOrderItemDTO returnedOrderItemDTO = returnedOrderItemMapper.toDto(returnedOrderItem);

        restReturnedOrderItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(returnedOrderItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllReturnedOrderItems() throws Exception {
        // Initialize the database
        insertedReturnedOrderItem = returnedOrderItemRepository.saveAndFlush(returnedOrderItem);

        // Get all the returnedOrderItemList
        restReturnedOrderItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(returnedOrderItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].productComment").value(hasItem(DEFAULT_PRODUCT_COMMENT)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllReturnedOrderItemsWithEagerRelationshipsIsEnabled() throws Exception {
        when(returnedOrderItemServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restReturnedOrderItemMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(returnedOrderItemServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllReturnedOrderItemsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(returnedOrderItemServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restReturnedOrderItemMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(returnedOrderItemRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getReturnedOrderItem() throws Exception {
        // Initialize the database
        insertedReturnedOrderItem = returnedOrderItemRepository.saveAndFlush(returnedOrderItem);

        // Get the returnedOrderItem
        restReturnedOrderItemMockMvc
            .perform(get(ENTITY_API_URL_ID, returnedOrderItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(returnedOrderItem.getId().intValue()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.productComment").value(DEFAULT_PRODUCT_COMMENT));
    }

    @Test
    @Transactional
    void getReturnedOrderItemsByIdFiltering() throws Exception {
        // Initialize the database
        insertedReturnedOrderItem = returnedOrderItemRepository.saveAndFlush(returnedOrderItem);

        Long id = returnedOrderItem.getId();

        defaultReturnedOrderItemFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultReturnedOrderItemFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultReturnedOrderItemFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllReturnedOrderItemsByQuantityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReturnedOrderItem = returnedOrderItemRepository.saveAndFlush(returnedOrderItem);

        // Get all the returnedOrderItemList where quantity equals to
        defaultReturnedOrderItemFiltering("quantity.equals=" + DEFAULT_QUANTITY, "quantity.equals=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllReturnedOrderItemsByQuantityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReturnedOrderItem = returnedOrderItemRepository.saveAndFlush(returnedOrderItem);

        // Get all the returnedOrderItemList where quantity in
        defaultReturnedOrderItemFiltering("quantity.in=" + DEFAULT_QUANTITY + "," + UPDATED_QUANTITY, "quantity.in=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllReturnedOrderItemsByQuantityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReturnedOrderItem = returnedOrderItemRepository.saveAndFlush(returnedOrderItem);

        // Get all the returnedOrderItemList where quantity is not null
        defaultReturnedOrderItemFiltering("quantity.specified=true", "quantity.specified=false");
    }

    @Test
    @Transactional
    void getAllReturnedOrderItemsByQuantityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedReturnedOrderItem = returnedOrderItemRepository.saveAndFlush(returnedOrderItem);

        // Get all the returnedOrderItemList where quantity is greater than or equal to
        defaultReturnedOrderItemFiltering(
            "quantity.greaterThanOrEqual=" + DEFAULT_QUANTITY,
            "quantity.greaterThanOrEqual=" + UPDATED_QUANTITY
        );
    }

    @Test
    @Transactional
    void getAllReturnedOrderItemsByQuantityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedReturnedOrderItem = returnedOrderItemRepository.saveAndFlush(returnedOrderItem);

        // Get all the returnedOrderItemList where quantity is less than or equal to
        defaultReturnedOrderItemFiltering("quantity.lessThanOrEqual=" + DEFAULT_QUANTITY, "quantity.lessThanOrEqual=" + SMALLER_QUANTITY);
    }

    @Test
    @Transactional
    void getAllReturnedOrderItemsByQuantityIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedReturnedOrderItem = returnedOrderItemRepository.saveAndFlush(returnedOrderItem);

        // Get all the returnedOrderItemList where quantity is less than
        defaultReturnedOrderItemFiltering("quantity.lessThan=" + UPDATED_QUANTITY, "quantity.lessThan=" + DEFAULT_QUANTITY);
    }

    @Test
    @Transactional
    void getAllReturnedOrderItemsByQuantityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedReturnedOrderItem = returnedOrderItemRepository.saveAndFlush(returnedOrderItem);

        // Get all the returnedOrderItemList where quantity is greater than
        defaultReturnedOrderItemFiltering("quantity.greaterThan=" + SMALLER_QUANTITY, "quantity.greaterThan=" + DEFAULT_QUANTITY);
    }

    @Test
    @Transactional
    void getAllReturnedOrderItemsByProductCommentIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReturnedOrderItem = returnedOrderItemRepository.saveAndFlush(returnedOrderItem);

        // Get all the returnedOrderItemList where productComment equals to
        defaultReturnedOrderItemFiltering(
            "productComment.equals=" + DEFAULT_PRODUCT_COMMENT,
            "productComment.equals=" + UPDATED_PRODUCT_COMMENT
        );
    }

    @Test
    @Transactional
    void getAllReturnedOrderItemsByProductCommentIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReturnedOrderItem = returnedOrderItemRepository.saveAndFlush(returnedOrderItem);

        // Get all the returnedOrderItemList where productComment in
        defaultReturnedOrderItemFiltering(
            "productComment.in=" + DEFAULT_PRODUCT_COMMENT + "," + UPDATED_PRODUCT_COMMENT,
            "productComment.in=" + UPDATED_PRODUCT_COMMENT
        );
    }

    @Test
    @Transactional
    void getAllReturnedOrderItemsByProductCommentIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReturnedOrderItem = returnedOrderItemRepository.saveAndFlush(returnedOrderItem);

        // Get all the returnedOrderItemList where productComment is not null
        defaultReturnedOrderItemFiltering("productComment.specified=true", "productComment.specified=false");
    }

    @Test
    @Transactional
    void getAllReturnedOrderItemsByProductCommentContainsSomething() throws Exception {
        // Initialize the database
        insertedReturnedOrderItem = returnedOrderItemRepository.saveAndFlush(returnedOrderItem);

        // Get all the returnedOrderItemList where productComment contains
        defaultReturnedOrderItemFiltering(
            "productComment.contains=" + DEFAULT_PRODUCT_COMMENT,
            "productComment.contains=" + UPDATED_PRODUCT_COMMENT
        );
    }

    @Test
    @Transactional
    void getAllReturnedOrderItemsByProductCommentNotContainsSomething() throws Exception {
        // Initialize the database
        insertedReturnedOrderItem = returnedOrderItemRepository.saveAndFlush(returnedOrderItem);

        // Get all the returnedOrderItemList where productComment does not contain
        defaultReturnedOrderItemFiltering(
            "productComment.doesNotContain=" + UPDATED_PRODUCT_COMMENT,
            "productComment.doesNotContain=" + DEFAULT_PRODUCT_COMMENT
        );
    }

    @Test
    @Transactional
    void getAllReturnedOrderItemsByProductIsEqualToSomething() throws Exception {
        FishProduct product;
        if (TestUtil.findAll(em, FishProduct.class).isEmpty()) {
            returnedOrderItemRepository.saveAndFlush(returnedOrderItem);
            product = FishProductResourceIT.createEntity();
        } else {
            product = TestUtil.findAll(em, FishProduct.class).get(0);
        }
        em.persist(product);
        em.flush();
        returnedOrderItem.setProduct(product);
        returnedOrderItemRepository.saveAndFlush(returnedOrderItem);
        Long productId = product.getId();
        // Get all the returnedOrderItemList where product equals to productId
        defaultReturnedOrderItemShouldBeFound("productId.equals=" + productId);

        // Get all the returnedOrderItemList where product equals to (productId + 1)
        defaultReturnedOrderItemShouldNotBeFound("productId.equals=" + (productId + 1));
    }

    @Test
    @Transactional
    void getAllReturnedOrderItemsByReturnedOrderIsEqualToSomething() throws Exception {
        ReturnedOrder returnedOrder;
        if (TestUtil.findAll(em, ReturnedOrder.class).isEmpty()) {
            returnedOrderItemRepository.saveAndFlush(returnedOrderItem);
            returnedOrder = ReturnedOrderResourceIT.createEntity(em);
        } else {
            returnedOrder = TestUtil.findAll(em, ReturnedOrder.class).get(0);
        }
        em.persist(returnedOrder);
        em.flush();
        returnedOrderItem.setReturnedOrder(returnedOrder);
        returnedOrderItemRepository.saveAndFlush(returnedOrderItem);
        Long returnedOrderId = returnedOrder.getId();
        // Get all the returnedOrderItemList where returnedOrder equals to returnedOrderId
        defaultReturnedOrderItemShouldBeFound("returnedOrderId.equals=" + returnedOrderId);

        // Get all the returnedOrderItemList where returnedOrder equals to (returnedOrderId + 1)
        defaultReturnedOrderItemShouldNotBeFound("returnedOrderId.equals=" + (returnedOrderId + 1));
    }

    private void defaultReturnedOrderItemFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultReturnedOrderItemShouldBeFound(shouldBeFound);
        defaultReturnedOrderItemShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultReturnedOrderItemShouldBeFound(String filter) throws Exception {
        restReturnedOrderItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(returnedOrderItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].productComment").value(hasItem(DEFAULT_PRODUCT_COMMENT)));

        // Check, that the count call also returns 1
        restReturnedOrderItemMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultReturnedOrderItemShouldNotBeFound(String filter) throws Exception {
        restReturnedOrderItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restReturnedOrderItemMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingReturnedOrderItem() throws Exception {
        // Get the returnedOrderItem
        restReturnedOrderItemMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingReturnedOrderItem() throws Exception {
        // Initialize the database
        insertedReturnedOrderItem = returnedOrderItemRepository.saveAndFlush(returnedOrderItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the returnedOrderItem
        ReturnedOrderItem updatedReturnedOrderItem = returnedOrderItemRepository.findById(returnedOrderItem.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedReturnedOrderItem are not directly saved in db
        em.detach(updatedReturnedOrderItem);
        updatedReturnedOrderItem.quantity(UPDATED_QUANTITY).productComment(UPDATED_PRODUCT_COMMENT);
        ReturnedOrderItemDTO returnedOrderItemDTO = returnedOrderItemMapper.toDto(updatedReturnedOrderItem);

        restReturnedOrderItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, returnedOrderItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(returnedOrderItemDTO))
            )
            .andExpect(status().isOk());

        // Validate the ReturnedOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedReturnedOrderItemToMatchAllProperties(updatedReturnedOrderItem);
    }

    @Test
    @Transactional
    void putNonExistingReturnedOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        returnedOrderItem.setId(longCount.incrementAndGet());

        // Create the ReturnedOrderItem
        ReturnedOrderItemDTO returnedOrderItemDTO = returnedOrderItemMapper.toDto(returnedOrderItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReturnedOrderItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, returnedOrderItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(returnedOrderItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReturnedOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchReturnedOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        returnedOrderItem.setId(longCount.incrementAndGet());

        // Create the ReturnedOrderItem
        ReturnedOrderItemDTO returnedOrderItemDTO = returnedOrderItemMapper.toDto(returnedOrderItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReturnedOrderItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(returnedOrderItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReturnedOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamReturnedOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        returnedOrderItem.setId(longCount.incrementAndGet());

        // Create the ReturnedOrderItem
        ReturnedOrderItemDTO returnedOrderItemDTO = returnedOrderItemMapper.toDto(returnedOrderItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReturnedOrderItemMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(returnedOrderItemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ReturnedOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateReturnedOrderItemWithPatch() throws Exception {
        // Initialize the database
        insertedReturnedOrderItem = returnedOrderItemRepository.saveAndFlush(returnedOrderItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the returnedOrderItem using partial update
        ReturnedOrderItem partialUpdatedReturnedOrderItem = new ReturnedOrderItem();
        partialUpdatedReturnedOrderItem.setId(returnedOrderItem.getId());

        partialUpdatedReturnedOrderItem.quantity(UPDATED_QUANTITY).productComment(UPDATED_PRODUCT_COMMENT);

        restReturnedOrderItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReturnedOrderItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReturnedOrderItem))
            )
            .andExpect(status().isOk());

        // Validate the ReturnedOrderItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReturnedOrderItemUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedReturnedOrderItem, returnedOrderItem),
            getPersistedReturnedOrderItem(returnedOrderItem)
        );
    }

    @Test
    @Transactional
    void fullUpdateReturnedOrderItemWithPatch() throws Exception {
        // Initialize the database
        insertedReturnedOrderItem = returnedOrderItemRepository.saveAndFlush(returnedOrderItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the returnedOrderItem using partial update
        ReturnedOrderItem partialUpdatedReturnedOrderItem = new ReturnedOrderItem();
        partialUpdatedReturnedOrderItem.setId(returnedOrderItem.getId());

        partialUpdatedReturnedOrderItem.quantity(UPDATED_QUANTITY).productComment(UPDATED_PRODUCT_COMMENT);

        restReturnedOrderItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReturnedOrderItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReturnedOrderItem))
            )
            .andExpect(status().isOk());

        // Validate the ReturnedOrderItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReturnedOrderItemUpdatableFieldsEquals(
            partialUpdatedReturnedOrderItem,
            getPersistedReturnedOrderItem(partialUpdatedReturnedOrderItem)
        );
    }

    @Test
    @Transactional
    void patchNonExistingReturnedOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        returnedOrderItem.setId(longCount.incrementAndGet());

        // Create the ReturnedOrderItem
        ReturnedOrderItemDTO returnedOrderItemDTO = returnedOrderItemMapper.toDto(returnedOrderItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReturnedOrderItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, returnedOrderItemDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(returnedOrderItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReturnedOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchReturnedOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        returnedOrderItem.setId(longCount.incrementAndGet());

        // Create the ReturnedOrderItem
        ReturnedOrderItemDTO returnedOrderItemDTO = returnedOrderItemMapper.toDto(returnedOrderItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReturnedOrderItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(returnedOrderItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReturnedOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamReturnedOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        returnedOrderItem.setId(longCount.incrementAndGet());

        // Create the ReturnedOrderItem
        ReturnedOrderItemDTO returnedOrderItemDTO = returnedOrderItemMapper.toDto(returnedOrderItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReturnedOrderItemMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(returnedOrderItemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ReturnedOrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteReturnedOrderItem() throws Exception {
        // Initialize the database
        insertedReturnedOrderItem = returnedOrderItemRepository.saveAndFlush(returnedOrderItem);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the returnedOrderItem
        restReturnedOrderItemMockMvc
            .perform(delete(ENTITY_API_URL_ID, returnedOrderItem.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return returnedOrderItemRepository.count();
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

    protected ReturnedOrderItem getPersistedReturnedOrderItem(ReturnedOrderItem returnedOrderItem) {
        return returnedOrderItemRepository.findById(returnedOrderItem.getId()).orElseThrow();
    }

    protected void assertPersistedReturnedOrderItemToMatchAllProperties(ReturnedOrderItem expectedReturnedOrderItem) {
        assertReturnedOrderItemAllPropertiesEquals(expectedReturnedOrderItem, getPersistedReturnedOrderItem(expectedReturnedOrderItem));
    }

    protected void assertPersistedReturnedOrderItemToMatchUpdatableProperties(ReturnedOrderItem expectedReturnedOrderItem) {
        assertReturnedOrderItemAllUpdatablePropertiesEquals(
            expectedReturnedOrderItem,
            getPersistedReturnedOrderItem(expectedReturnedOrderItem)
        );
    }
}
