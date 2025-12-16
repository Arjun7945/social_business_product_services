package com.aps.web.rest;

import static com.aps.domain.ProductImageAsserts.*;
import static com.aps.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.aps.IntegrationTest;
import com.aps.domain.FishProduct;
import com.aps.domain.ProductImage;
import com.aps.repository.ProductImageRepository;
import com.aps.service.ProductImageService;
import com.aps.service.dto.ProductImageDTO;
import com.aps.service.mapper.ProductImageMapper;
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
 * Integration tests for the {@link ProductImageResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ProductImageResourceIT {

    private static final String DEFAULT_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_URL = "BBBBBBBBBB";

    private static final Integer DEFAULT_DISPLAY_ORDER = 1;
    private static final Integer UPDATED_DISPLAY_ORDER = 2;

    private static final String ENTITY_API_URL = "/api/product-images";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Mock
    private ProductImageRepository productImageRepositoryMock;

    @Autowired
    private ProductImageMapper productImageMapper;

    @Mock
    private ProductImageService productImageServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductImageMockMvc;

    private ProductImage productImage;

    private ProductImage insertedProductImage;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductImage createEntity(EntityManager em) {
        ProductImage productImage = new ProductImage().imageUrl(DEFAULT_IMAGE_URL).displayOrder(DEFAULT_DISPLAY_ORDER);
        // Add required entity
        FishProduct fishProduct;
        if (TestUtil.findAll(em, FishProduct.class).isEmpty()) {
            fishProduct = FishProductResourceIT.createEntity();
            em.persist(fishProduct);
            em.flush();
        } else {
            fishProduct = TestUtil.findAll(em, FishProduct.class).get(0);
        }
        productImage.setProduct(fishProduct);
        return productImage;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductImage createUpdatedEntity(EntityManager em) {
        ProductImage updatedProductImage = new ProductImage().imageUrl(UPDATED_IMAGE_URL).displayOrder(UPDATED_DISPLAY_ORDER);
        // Add required entity
        FishProduct fishProduct;
        if (TestUtil.findAll(em, FishProduct.class).isEmpty()) {
            fishProduct = FishProductResourceIT.createUpdatedEntity();
            em.persist(fishProduct);
            em.flush();
        } else {
            fishProduct = TestUtil.findAll(em, FishProduct.class).get(0);
        }
        updatedProductImage.setProduct(fishProduct);
        return updatedProductImage;
    }

    @BeforeEach
    public void initTest() {
        productImage = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedProductImage != null) {
            productImageRepository.delete(insertedProductImage);
            insertedProductImage = null;
        }
    }

    @Test
    @Transactional
    void createProductImage() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ProductImage
        ProductImageDTO productImageDTO = productImageMapper.toDto(productImage);
        var returnedProductImageDTO = om.readValue(
            restProductImageMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productImageDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ProductImageDTO.class
        );

        // Validate the ProductImage in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProductImage = productImageMapper.toEntity(returnedProductImageDTO);
        assertProductImageUpdatableFieldsEquals(returnedProductImage, getPersistedProductImage(returnedProductImage));

        insertedProductImage = returnedProductImage;
    }

    @Test
    @Transactional
    void createProductImageWithExistingId() throws Exception {
        // Create the ProductImage with an existing ID
        productImage.setId(1L);
        ProductImageDTO productImageDTO = productImageMapper.toDto(productImage);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductImageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productImageDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ProductImage in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkImageUrlIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        productImage.setImageUrl(null);

        // Create the ProductImage, which fails.
        ProductImageDTO productImageDTO = productImageMapper.toDto(productImage);

        restProductImageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productImageDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDisplayOrderIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        productImage.setDisplayOrder(null);

        // Create the ProductImage, which fails.
        ProductImageDTO productImageDTO = productImageMapper.toDto(productImage);

        restProductImageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productImageDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProductImages() throws Exception {
        // Initialize the database
        insertedProductImage = productImageRepository.saveAndFlush(productImage);

        // Get all the productImageList
        restProductImageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productImage.getId().intValue())))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].displayOrder").value(hasItem(DEFAULT_DISPLAY_ORDER)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProductImagesWithEagerRelationshipsIsEnabled() throws Exception {
        when(productImageServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProductImageMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(productImageServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProductImagesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(productImageServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProductImageMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(productImageRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getProductImage() throws Exception {
        // Initialize the database
        insertedProductImage = productImageRepository.saveAndFlush(productImage);

        // Get the productImage
        restProductImageMockMvc
            .perform(get(ENTITY_API_URL_ID, productImage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(productImage.getId().intValue()))
            .andExpect(jsonPath("$.imageUrl").value(DEFAULT_IMAGE_URL))
            .andExpect(jsonPath("$.displayOrder").value(DEFAULT_DISPLAY_ORDER));
    }

    @Test
    @Transactional
    void getNonExistingProductImage() throws Exception {
        // Get the productImage
        restProductImageMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProductImage() throws Exception {
        // Initialize the database
        insertedProductImage = productImageRepository.saveAndFlush(productImage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productImage
        ProductImage updatedProductImage = productImageRepository.findById(productImage.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProductImage are not directly saved in db
        em.detach(updatedProductImage);
        updatedProductImage.imageUrl(UPDATED_IMAGE_URL).displayOrder(UPDATED_DISPLAY_ORDER);
        ProductImageDTO productImageDTO = productImageMapper.toDto(updatedProductImage);

        restProductImageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productImageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productImageDTO))
            )
            .andExpect(status().isOk());

        // Validate the ProductImage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProductImageToMatchAllProperties(updatedProductImage);
    }

    @Test
    @Transactional
    void putNonExistingProductImage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productImage.setId(longCount.incrementAndGet());

        // Create the ProductImage
        ProductImageDTO productImageDTO = productImageMapper.toDto(productImage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductImageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productImageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productImageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductImage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProductImage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productImage.setId(longCount.incrementAndGet());

        // Create the ProductImage
        ProductImageDTO productImageDTO = productImageMapper.toDto(productImage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductImageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productImageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductImage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProductImage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productImage.setId(longCount.incrementAndGet());

        // Create the ProductImage
        ProductImageDTO productImageDTO = productImageMapper.toDto(productImage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductImageMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productImageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductImage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProductImageWithPatch() throws Exception {
        // Initialize the database
        insertedProductImage = productImageRepository.saveAndFlush(productImage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productImage using partial update
        ProductImage partialUpdatedProductImage = new ProductImage();
        partialUpdatedProductImage.setId(productImage.getId());

        partialUpdatedProductImage.imageUrl(UPDATED_IMAGE_URL);

        restProductImageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductImage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProductImage))
            )
            .andExpect(status().isOk());

        // Validate the ProductImage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductImageUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedProductImage, productImage),
            getPersistedProductImage(productImage)
        );
    }

    @Test
    @Transactional
    void fullUpdateProductImageWithPatch() throws Exception {
        // Initialize the database
        insertedProductImage = productImageRepository.saveAndFlush(productImage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productImage using partial update
        ProductImage partialUpdatedProductImage = new ProductImage();
        partialUpdatedProductImage.setId(productImage.getId());

        partialUpdatedProductImage.imageUrl(UPDATED_IMAGE_URL).displayOrder(UPDATED_DISPLAY_ORDER);

        restProductImageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductImage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProductImage))
            )
            .andExpect(status().isOk());

        // Validate the ProductImage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductImageUpdatableFieldsEquals(partialUpdatedProductImage, getPersistedProductImage(partialUpdatedProductImage));
    }

    @Test
    @Transactional
    void patchNonExistingProductImage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productImage.setId(longCount.incrementAndGet());

        // Create the ProductImage
        ProductImageDTO productImageDTO = productImageMapper.toDto(productImage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductImageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, productImageDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(productImageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductImage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProductImage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productImage.setId(longCount.incrementAndGet());

        // Create the ProductImage
        ProductImageDTO productImageDTO = productImageMapper.toDto(productImage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductImageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(productImageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductImage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProductImage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productImage.setId(longCount.incrementAndGet());

        // Create the ProductImage
        ProductImageDTO productImageDTO = productImageMapper.toDto(productImage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductImageMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(productImageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductImage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProductImage() throws Exception {
        // Initialize the database
        insertedProductImage = productImageRepository.saveAndFlush(productImage);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the productImage
        restProductImageMockMvc
            .perform(delete(ENTITY_API_URL_ID, productImage.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return productImageRepository.count();
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

    protected ProductImage getPersistedProductImage(ProductImage productImage) {
        return productImageRepository.findById(productImage.getId()).orElseThrow();
    }

    protected void assertPersistedProductImageToMatchAllProperties(ProductImage expectedProductImage) {
        assertProductImageAllPropertiesEquals(expectedProductImage, getPersistedProductImage(expectedProductImage));
    }

    protected void assertPersistedProductImageToMatchUpdatableProperties(ProductImage expectedProductImage) {
        assertProductImageAllUpdatablePropertiesEquals(expectedProductImage, getPersistedProductImage(expectedProductImage));
    }
}
