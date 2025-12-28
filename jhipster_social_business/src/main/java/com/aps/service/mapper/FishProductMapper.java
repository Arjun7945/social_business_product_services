package com.aps.service.mapper;

import com.aps.domain.FishProduct;
import com.aps.domain.ProductImage;
import com.aps.service.dto.FishProductDTO;
import com.aps.service.dto.ProductImageDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link FishProduct} and its DTO {@link FishProductDTO}.
 */
@Mapper(componentModel = "spring")
public interface FishProductMapper extends EntityMapper<FishProductDTO, FishProduct> {
    @Mapping(target = "image", source = "image", qualifiedByName = "productImageId")
    FishProductDTO toDto(FishProduct s);

    @Named("productImageId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProductImageDTO toDtoProductImageId(ProductImage productImage);
}
