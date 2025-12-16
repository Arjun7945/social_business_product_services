package com.aps.service;

import com.aps.domain.ProductImage;
import com.aps.repository.ProductImageRepository;
import com.aps.service.dto.ProductImageDTO;
import com.aps.service.mapper.ProductImageMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.aps.domain.ProductImage}.
 */
@Service
@Transactional
public class ProductImageService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductImageService.class);

    private final ProductImageRepository productImageRepository;

    private final ProductImageMapper productImageMapper;

    public ProductImageService(ProductImageRepository productImageRepository, ProductImageMapper productImageMapper) {
        this.productImageRepository = productImageRepository;
        this.productImageMapper = productImageMapper;
    }

    /**
     * Save a productImage.
     *
     * @param productImageDTO the entity to save.
     * @return the persisted entity.
     */
    public ProductImageDTO save(ProductImageDTO productImageDTO) {
        LOG.debug("Request to save ProductImage : {}", productImageDTO);
        ProductImage productImage = productImageMapper.toEntity(productImageDTO);
        productImage = productImageRepository.save(productImage);
        return productImageMapper.toDto(productImage);
    }

    /**
     * Update a productImage.
     *
     * @param productImageDTO the entity to save.
     * @return the persisted entity.
     */
    public ProductImageDTO update(ProductImageDTO productImageDTO) {
        LOG.debug("Request to update ProductImage : {}", productImageDTO);
        ProductImage productImage = productImageMapper.toEntity(productImageDTO);
        productImage = productImageRepository.save(productImage);
        return productImageMapper.toDto(productImage);
    }

    /**
     * Partially update a productImage.
     *
     * @param productImageDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ProductImageDTO> partialUpdate(ProductImageDTO productImageDTO) {
        LOG.debug("Request to partially update ProductImage : {}", productImageDTO);

        return productImageRepository
            .findById(productImageDTO.getId())
            .map(existingProductImage -> {
                productImageMapper.partialUpdate(existingProductImage, productImageDTO);

                return existingProductImage;
            })
            .map(productImageRepository::save)
            .map(productImageMapper::toDto);
    }

    /**
     * Get all the productImages.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ProductImageDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ProductImages");
        return productImageRepository.findAll(pageable).map(productImageMapper::toDto);
    }

    /**
     * Get all the productImages with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ProductImageDTO> findAllWithEagerRelationships(Pageable pageable) {
        return productImageRepository.findAllWithEagerRelationships(pageable).map(productImageMapper::toDto);
    }

    /**
     * Get one productImage by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ProductImageDTO> findOne(Long id) {
        LOG.debug("Request to get ProductImage : {}", id);
        return productImageRepository.findOneWithEagerRelationships(id).map(productImageMapper::toDto);
    }

    /**
     * Delete the productImage by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ProductImage : {}", id);
        productImageRepository.deleteById(id);
    }
}
