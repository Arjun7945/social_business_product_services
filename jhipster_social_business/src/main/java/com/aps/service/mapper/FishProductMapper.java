package com.aps.service.mapper;

import com.aps.domain.FishProduct;
import com.aps.service.dto.FishProductDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link FishProduct} and its DTO {@link FishProductDTO}.
 */
@Mapper(componentModel = "spring", uses = { ProductImageMapper.class })
public interface FishProductMapper extends EntityMapper<FishProductDTO, FishProduct> {
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "removeImages", ignore = true)
    FishProduct toEntity(FishProductDTO fishProductDTO);

    @Mapping(target = "images", ignore = true)
    @Mapping(target = "removeImages", ignore = true)
    void partialUpdate(@MappingTarget FishProduct entity, FishProductDTO dto);
}
