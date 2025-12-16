package com.aps.service;

import com.aps.domain.FishProduct;
import com.aps.repository.FishProductRepository;
import com.aps.service.dto.FishProductDTO;
import com.aps.service.mapper.FishProductMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.aps.domain.FishProduct}.
 */
@Service
@Transactional
public class FishProductService {

    private static final Logger LOG = LoggerFactory.getLogger(FishProductService.class);

    private final FishProductRepository fishProductRepository;

    private final FishProductMapper fishProductMapper;

    public FishProductService(FishProductRepository fishProductRepository, FishProductMapper fishProductMapper) {
        this.fishProductRepository = fishProductRepository;
        this.fishProductMapper = fishProductMapper;
    }

    /**
     * Save a fishProduct.
     *
     * @param fishProductDTO the entity to save.
     * @return the persisted entity.
     */
    public FishProductDTO save(FishProductDTO fishProductDTO) {
        LOG.debug("Request to save FishProduct : {}", fishProductDTO);
        FishProduct fishProduct = fishProductMapper.toEntity(fishProductDTO);
        fishProduct = fishProductRepository.save(fishProduct);
        return fishProductMapper.toDto(fishProduct);
    }

    /**
     * Update a fishProduct.
     *
     * @param fishProductDTO the entity to save.
     * @return the persisted entity.
     */
    public FishProductDTO update(FishProductDTO fishProductDTO) {
        LOG.debug("Request to update FishProduct : {}", fishProductDTO);
        FishProduct fishProduct = fishProductMapper.toEntity(fishProductDTO);
        fishProduct = fishProductRepository.save(fishProduct);
        return fishProductMapper.toDto(fishProduct);
    }

    /**
     * Partially update a fishProduct.
     *
     * @param fishProductDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<FishProductDTO> partialUpdate(FishProductDTO fishProductDTO) {
        LOG.debug("Request to partially update FishProduct : {}", fishProductDTO);

        return fishProductRepository
            .findById(fishProductDTO.getId())
            .map(existingFishProduct -> {
                fishProductMapper.partialUpdate(existingFishProduct, fishProductDTO);

                return existingFishProduct;
            })
            .map(fishProductRepository::save)
            .map(fishProductMapper::toDto);
    }

    /**
     * Get one fishProduct by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<FishProductDTO> findOne(Long id) {
        LOG.debug("Request to get FishProduct : {}", id);
        return fishProductRepository.findById(id).map(fishProductMapper::toDto);
    }

    /**
     * Delete the fishProduct by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete FishProduct : {}", id);
        fishProductRepository.deleteById(id);
    }
}
