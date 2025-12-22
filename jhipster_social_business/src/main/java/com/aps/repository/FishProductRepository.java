package com.aps.repository;

import com.aps.domain.FishProduct;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the FishProduct entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FishProductRepository extends JpaRepository<FishProduct, Long>, JpaSpecificationExecutor<FishProduct> {

    @EntityGraph(attributePaths = "images")
    @Cacheable(cacheNames = "productCatalog")
    java.util.List<FishProduct> findByIsAvailableTrue();

    @Override
    @CacheEvict(cacheNames = "productCatalog", allEntries = true)
    <S extends FishProduct> S save(S entity);

    @Override
    @CacheEvict(cacheNames = "productCatalog", allEntries = true)
    void delete(FishProduct entity);

    @Override
    @CacheEvict(cacheNames = "productCatalog", allEntries = true)
    void deleteById(Long id);
}
