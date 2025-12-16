package com.aps.service.mapper;

import com.aps.domain.FishProduct;
import com.aps.domain.ProductImage;
import com.aps.service.dto.FishProductDTO;
import com.aps.service.dto.ProductImageDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProductImage} and its DTO {@link ProductImageDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductImageMapper extends EntityMapper<ProductImageDTO, ProductImage> {
    @Mapping(target = "product", source = "product", qualifiedByName = "fishProductName")
    ProductImageDTO toDto(ProductImage s);

    @Named("fishProductName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    FishProductDTO toDtoFishProductName(FishProduct fishProduct);
}
