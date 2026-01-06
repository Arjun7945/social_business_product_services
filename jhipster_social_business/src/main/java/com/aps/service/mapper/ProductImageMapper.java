package com.aps.service.mapper;

import com.aps.domain.ProductImage;
import com.aps.service.dto.ProductImageDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProductImage} and its DTO
 * {@link ProductImageDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductImageMapper extends EntityMapper<ProductImageDTO, ProductImage> {
    @Override
    @Mapping(target = "imageUrl", expression = "java(\"/api/product-images/public/\" + productImage.getId() + \"/content\")")
    ProductImageDTO toDto(ProductImage productImage);
}
