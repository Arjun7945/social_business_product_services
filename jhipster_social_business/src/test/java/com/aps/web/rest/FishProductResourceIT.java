package com.aps.web.rest;

import static com.aps.domain.FishProductAsserts.*;
import static com.aps.web.rest.TestUtil.createUpdateProxyForBean;
import static com.aps.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.aps.IntegrationTest;
import com.aps.domain.FishProduct;
import com.aps.repository.FishProductRepository;
import com.aps.service.dto.FishProductDTO;
import com.aps.service.mapper.FishProductMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link FishProductResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FishProductResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRICE_PER_KG = new BigDecimal(0);
    private static final BigDecimal UPDATED_PRICE_PER_KG = new BigDecimal(1);
    private static final BigDecimal SMALLER_PRICE_PER_KG = new BigDecimal(0 - 1);

    private static final String DEFAULT_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_URL = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_AVAILABLE = false;
    private static final Boolean UPDATED_IS_AVAILABLE = true;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/fish-products";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FishProductRepository fishProductRepository;

    @Autowired
    private FishProductMapper fishProductMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFishProductMockMvc;

    private FishProduct fishProduct;

    private FishProduct insertedFishProduct;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FishProduct createEntity() {
        return new FishProduct()
            .name(DEFAULT_NAME)
            .pricePerKg(DEFAULT_PRICE_PER_KG)
            .imageUrl(DEFAULT_IMAGE_URL)
            .description(DEFAULT_DESCRIPTION)
            .isAvailable(DEFAULT_IS_AVAILABLE)
            .createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FishProduct createUpdatedEntity() {
        return new FishProduct()
            .name(UPDATED_NAME)
            .pricePerKg(UPDATED_PRICE_PER_KG)
            .imageUrl(UPDATED_IMAGE_URL)
            .description(UPDATED_DESCRIPTION)
            .isAvailable(UPDATED_IS_AVAILABLE)
            .createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    public void initTest() {
        fishProduct = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedFishProduct != null) {
            fishProductRepository.delete(insertedFishProduct);
            insertedFishProduct = null;
        }
    }

    @Test
    @Transactional
    void createFishProduct() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the FishProduct
        FishProductDTO fishProductDTO = fishProductMapper.toDto(fishProduct);
        var returnedFishProductDTO = om.readValue(
            restFishProductMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(fishProductDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            FishProductDTO.class
        );

        // Validate the FishProduct in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedFishProduct = fishProductMapper.toEntity(returnedFishProductDTO);
        assertFishProductUpdatableFieldsEquals(returnedFishProduct, getPersistedFishProduct(returnedFishProduct));

        insertedFishProduct = returnedFishProduct;
    }

    @Test
    @Transactional
    void createFishProductWithExistingId() throws Exception {
        // Create the FishProduct with an existing ID
        fishProduct.setId(1L);
        FishProductDTO fishProductDTO = fishProductMapper.toDto(fishProduct);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFishProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(fishProductDTO)))
            .andExpect(status().isBadRequest());

        // Validate the FishProduct in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        fishProduct.setName(null);

        // Create the FishProduct, which fails.
        FishProductDTO fishProductDTO = fishProductMapper.toDto(fishProduct);

        restFishProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(fishProductDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPricePerKgIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        fishProduct.setPricePerKg(null);

        // Create the FishProduct, which fails.
        FishProductDTO fishProductDTO = fishProductMapper.toDto(fishProduct);

        restFishProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(fishProductDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsAvailableIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        fishProduct.setIsAvailable(null);

        // Create the FishProduct, which fails.
        FishProductDTO fishProductDTO = fishProductMapper.toDto(fishProduct);

        restFishProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(fishProductDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFishProducts() throws Exception {
        // Initialize the database
        insertedFishProduct = fishProductRepository.saveAndFlush(fishProduct);

        // Get all the fishProductList
        restFishProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(fishProduct.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].pricePerKg").value(hasItem(sameNumber(DEFAULT_PRICE_PER_KG))))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].isAvailable").value(hasItem(DEFAULT_IS_AVAILABLE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @Test
    @Transactional
    void getFishProduct() throws Exception {
        // Initialize the database
        insertedFishProduct = fishProductRepository.saveAndFlush(fishProduct);

        // Get the fishProduct
        restFishProductMockMvc
            .perform(get(ENTITY_API_URL_ID, fishProduct.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(fishProduct.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.pricePerKg").value(sameNumber(DEFAULT_PRICE_PER_KG)))
            .andExpect(jsonPath("$.imageUrl").value(DEFAULT_IMAGE_URL))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.isAvailable").value(DEFAULT_IS_AVAILABLE))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getFishProductsByIdFiltering() throws Exception {
        // Initialize the database
        insertedFishProduct = fishProductRepository.saveAndFlush(fishProduct);

        Long id = fishProduct.getId();

        defaultFishProductFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultFishProductFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultFishProductFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllFishProductsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedFishProduct = fishProductRepository.saveAndFlush(fishProduct);

        // Get all the fishProductList where name equals to
        defaultFishProductFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllFishProductsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedFishProduct = fishProductRepository.saveAndFlush(fishProduct);

        // Get all the fishProductList where name in
        defaultFishProductFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllFishProductsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedFishProduct = fishProductRepository.saveAndFlush(fishProduct);

        // Get all the fishProductList where name is not null
        defaultFishProductFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllFishProductsByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedFishProduct = fishProductRepository.saveAndFlush(fishProduct);

        // Get all the fishProductList where name contains
        defaultFishProductFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllFishProductsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedFishProduct = fishProductRepository.saveAndFlush(fishProduct);

        // Get all the fishProductList where name does not contain
        defaultFishProductFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllFishProductsByPricePerKgIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedFishProduct = fishProductRepository.saveAndFlush(fishProduct);

        // Get all the fishProductList where pricePerKg equals to
        defaultFishProductFiltering("pricePerKg.equals=" + DEFAULT_PRICE_PER_KG, "pricePerKg.equals=" + UPDATED_PRICE_PER_KG);
    }

    @Test
    @Transactional
    void getAllFishProductsByPricePerKgIsInShouldWork() throws Exception {
        // Initialize the database
        insertedFishProduct = fishProductRepository.saveAndFlush(fishProduct);

        // Get all the fishProductList where pricePerKg in
        defaultFishProductFiltering(
            "pricePerKg.in=" + DEFAULT_PRICE_PER_KG + "," + UPDATED_PRICE_PER_KG,
            "pricePerKg.in=" + UPDATED_PRICE_PER_KG
        );
    }

    @Test
    @Transactional
    void getAllFishProductsByPricePerKgIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedFishProduct = fishProductRepository.saveAndFlush(fishProduct);

        // Get all the fishProductList where pricePerKg is not null
        defaultFishProductFiltering("pricePerKg.specified=true", "pricePerKg.specified=false");
    }

    @Test
    @Transactional
    void getAllFishProductsByPricePerKgIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedFishProduct = fishProductRepository.saveAndFlush(fishProduct);

        // Get all the fishProductList where pricePerKg is greater than or equal to
        defaultFishProductFiltering(
            "pricePerKg.greaterThanOrEqual=" + DEFAULT_PRICE_PER_KG,
            "pricePerKg.greaterThanOrEqual=" + UPDATED_PRICE_PER_KG
        );
    }

    @Test
    @Transactional
    void getAllFishProductsByPricePerKgIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedFishProduct = fishProductRepository.saveAndFlush(fishProduct);

        // Get all the fishProductList where pricePerKg is less than or equal to
        defaultFishProductFiltering(
            "pricePerKg.lessThanOrEqual=" + DEFAULT_PRICE_PER_KG,
            "pricePerKg.lessThanOrEqual=" + SMALLER_PRICE_PER_KG
        );
    }

    @Test
    @Transactional
    void getAllFishProductsByPricePerKgIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedFishProduct = fishProductRepository.saveAndFlush(fishProduct);

        // Get all the fishProductList where pricePerKg is less than
        defaultFishProductFiltering("pricePerKg.lessThan=" + UPDATED_PRICE_PER_KG, "pricePerKg.lessThan=" + DEFAULT_PRICE_PER_KG);
    }

    @Test
    @Transactional
    void getAllFishProductsByPricePerKgIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedFishProduct = fishProductRepository.saveAndFlush(fishProduct);

        // Get all the fishProductList where pricePerKg is greater than
        defaultFishProductFiltering("pricePerKg.greaterThan=" + SMALLER_PRICE_PER_KG, "pricePerKg.greaterThan=" + DEFAULT_PRICE_PER_KG);
    }

    @Test
    @Transactional
    void getAllFishProductsByImageUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedFishProduct = fishProductRepository.saveAndFlush(fishProduct);

        // Get all the fishProductList where imageUrl equals to
        defaultFishProductFiltering("imageUrl.equals=" + DEFAULT_IMAGE_URL, "imageUrl.equals=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllFishProductsByImageUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedFishProduct = fishProductRepository.saveAndFlush(fishProduct);

        // Get all the fishProductList where imageUrl in
        defaultFishProductFiltering("imageUrl.in=" + DEFAULT_IMAGE_URL + "," + UPDATED_IMAGE_URL, "imageUrl.in=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllFishProductsByImageUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedFishProduct = fishProductRepository.saveAndFlush(fishProduct);

        // Get all the fishProductList where imageUrl is not null
        defaultFishProductFiltering("imageUrl.specified=true", "imageUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllFishProductsByImageUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedFishProduct = fishProductRepository.saveAndFlush(fishProduct);

        // Get all the fishProductList where imageUrl contains
        defaultFishProductFiltering("imageUrl.contains=" + DEFAULT_IMAGE_URL, "imageUrl.contains=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllFishProductsByImageUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedFishProduct = fishProductRepository.saveAndFlush(fishProduct);

        // Get all the fishProductList where imageUrl does not contain
        defaultFishProductFiltering("imageUrl.doesNotContain=" + UPDATED_IMAGE_URL, "imageUrl.doesNotContain=" + DEFAULT_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllFishProductsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedFishProduct = fishProductRepository.saveAndFlush(fishProduct);

        // Get all the fishProductList where description equals to
        defaultFishProductFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllFishProductsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedFishProduct = fishProductRepository.saveAndFlush(fishProduct);

        // Get all the fishProductList where description in
        defaultFishProductFiltering(
            "description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION,
            "description.in=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllFishProductsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedFishProduct = fishProductRepository.saveAndFlush(fishProduct);

        // Get all the fishProductList where description is not null
        defaultFishProductFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    @Transactional
    void getAllFishProductsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        insertedFishProduct = fishProductRepository.saveAndFlush(fishProduct);

        // Get all the fishProductList where description contains
        defaultFishProductFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllFishProductsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedFishProduct = fishProductRepository.saveAndFlush(fishProduct);

        // Get all the fishProductList where description does not contain
        defaultFishProductFiltering(
            "description.doesNotContain=" + UPDATED_DESCRIPTION,
            "description.doesNotContain=" + DEFAULT_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllFishProductsByIsAvailableIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedFishProduct = fishProductRepository.saveAndFlush(fishProduct);

        // Get all the fishProductList where isAvailable equals to
        defaultFishProductFiltering("isAvailable.equals=" + DEFAULT_IS_AVAILABLE, "isAvailable.equals=" + UPDATED_IS_AVAILABLE);
    }

    @Test
    @Transactional
    void getAllFishProductsByIsAvailableIsInShouldWork() throws Exception {
        // Initialize the database
        insertedFishProduct = fishProductRepository.saveAndFlush(fishProduct);

        // Get all the fishProductList where isAvailable in
        defaultFishProductFiltering(
            "isAvailable.in=" + DEFAULT_IS_AVAILABLE + "," + UPDATED_IS_AVAILABLE,
            "isAvailable.in=" + UPDATED_IS_AVAILABLE
        );
    }

    @Test
    @Transactional
    void getAllFishProductsByIsAvailableIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedFishProduct = fishProductRepository.saveAndFlush(fishProduct);

        // Get all the fishProductList where isAvailable is not null
        defaultFishProductFiltering("isAvailable.specified=true", "isAvailable.specified=false");
    }

    @Test
    @Transactional
    void getAllFishProductsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedFishProduct = fishProductRepository.saveAndFlush(fishProduct);

        // Get all the fishProductList where createdAt equals to
        defaultFishProductFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllFishProductsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedFishProduct = fishProductRepository.saveAndFlush(fishProduct);

        // Get all the fishProductList where createdAt in
        defaultFishProductFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllFishProductsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedFishProduct = fishProductRepository.saveAndFlush(fishProduct);

        // Get all the fishProductList where createdAt is not null
        defaultFishProductFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    private void defaultFishProductFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultFishProductShouldBeFound(shouldBeFound);
        defaultFishProductShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultFishProductShouldBeFound(String filter) throws Exception {
        restFishProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(fishProduct.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].pricePerKg").value(hasItem(sameNumber(DEFAULT_PRICE_PER_KG))))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].isAvailable").value(hasItem(DEFAULT_IS_AVAILABLE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));

        // Check, that the count call also returns 1
        restFishProductMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultFishProductShouldNotBeFound(String filter) throws Exception {
        restFishProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restFishProductMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingFishProduct() throws Exception {
        // Get the fishProduct
        restFishProductMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFishProduct() throws Exception {
        // Initialize the database
        insertedFishProduct = fishProductRepository.saveAndFlush(fishProduct);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the fishProduct
        FishProduct updatedFishProduct = fishProductRepository.findById(fishProduct.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedFishProduct are not directly saved in db
        em.detach(updatedFishProduct);
        updatedFishProduct
            .name(UPDATED_NAME)
            .pricePerKg(UPDATED_PRICE_PER_KG)
            .imageUrl(UPDATED_IMAGE_URL)
            .description(UPDATED_DESCRIPTION)
            .isAvailable(UPDATED_IS_AVAILABLE)
            .createdAt(UPDATED_CREATED_AT);
        FishProductDTO fishProductDTO = fishProductMapper.toDto(updatedFishProduct);

        restFishProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, fishProductDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(fishProductDTO))
            )
            .andExpect(status().isOk());

        // Validate the FishProduct in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedFishProductToMatchAllProperties(updatedFishProduct);
    }

    @Test
    @Transactional
    void putNonExistingFishProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fishProduct.setId(longCount.incrementAndGet());

        // Create the FishProduct
        FishProductDTO fishProductDTO = fishProductMapper.toDto(fishProduct);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFishProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, fishProductDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(fishProductDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FishProduct in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFishProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fishProduct.setId(longCount.incrementAndGet());

        // Create the FishProduct
        FishProductDTO fishProductDTO = fishProductMapper.toDto(fishProduct);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFishProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(fishProductDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FishProduct in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFishProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fishProduct.setId(longCount.incrementAndGet());

        // Create the FishProduct
        FishProductDTO fishProductDTO = fishProductMapper.toDto(fishProduct);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFishProductMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(fishProductDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FishProduct in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFishProductWithPatch() throws Exception {
        // Initialize the database
        insertedFishProduct = fishProductRepository.saveAndFlush(fishProduct);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the fishProduct using partial update
        FishProduct partialUpdatedFishProduct = new FishProduct();
        partialUpdatedFishProduct.setId(fishProduct.getId());

        partialUpdatedFishProduct.pricePerKg(UPDATED_PRICE_PER_KG).createdAt(UPDATED_CREATED_AT);

        restFishProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFishProduct.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFishProduct))
            )
            .andExpect(status().isOk());

        // Validate the FishProduct in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFishProductUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedFishProduct, fishProduct),
            getPersistedFishProduct(fishProduct)
        );
    }

    @Test
    @Transactional
    void fullUpdateFishProductWithPatch() throws Exception {
        // Initialize the database
        insertedFishProduct = fishProductRepository.saveAndFlush(fishProduct);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the fishProduct using partial update
        FishProduct partialUpdatedFishProduct = new FishProduct();
        partialUpdatedFishProduct.setId(fishProduct.getId());

        partialUpdatedFishProduct
            .name(UPDATED_NAME)
            .pricePerKg(UPDATED_PRICE_PER_KG)
            .imageUrl(UPDATED_IMAGE_URL)
            .description(UPDATED_DESCRIPTION)
            .isAvailable(UPDATED_IS_AVAILABLE)
            .createdAt(UPDATED_CREATED_AT);

        restFishProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFishProduct.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFishProduct))
            )
            .andExpect(status().isOk());

        // Validate the FishProduct in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFishProductUpdatableFieldsEquals(partialUpdatedFishProduct, getPersistedFishProduct(partialUpdatedFishProduct));
    }

    @Test
    @Transactional
    void patchNonExistingFishProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fishProduct.setId(longCount.incrementAndGet());

        // Create the FishProduct
        FishProductDTO fishProductDTO = fishProductMapper.toDto(fishProduct);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFishProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, fishProductDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(fishProductDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FishProduct in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFishProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fishProduct.setId(longCount.incrementAndGet());

        // Create the FishProduct
        FishProductDTO fishProductDTO = fishProductMapper.toDto(fishProduct);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFishProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(fishProductDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FishProduct in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFishProduct() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fishProduct.setId(longCount.incrementAndGet());

        // Create the FishProduct
        FishProductDTO fishProductDTO = fishProductMapper.toDto(fishProduct);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFishProductMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(fishProductDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FishProduct in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFishProduct() throws Exception {
        // Initialize the database
        insertedFishProduct = fishProductRepository.saveAndFlush(fishProduct);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the fishProduct
        restFishProductMockMvc
            .perform(delete(ENTITY_API_URL_ID, fishProduct.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return fishProductRepository.count();
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

    protected FishProduct getPersistedFishProduct(FishProduct fishProduct) {
        return fishProductRepository.findById(fishProduct.getId()).orElseThrow();
    }

    protected void assertPersistedFishProductToMatchAllProperties(FishProduct expectedFishProduct) {
        assertFishProductAllPropertiesEquals(expectedFishProduct, getPersistedFishProduct(expectedFishProduct));
    }

    protected void assertPersistedFishProductToMatchUpdatableProperties(FishProduct expectedFishProduct) {
        assertFishProductAllUpdatablePropertiesEquals(expectedFishProduct, getPersistedFishProduct(expectedFishProduct));
    }
}
