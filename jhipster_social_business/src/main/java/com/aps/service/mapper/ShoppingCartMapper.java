package com.aps.service.mapper;

import com.aps.domain.ShoppingCart;
import com.aps.service.dto.ShoppingCartDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ShoppingCart} and its DTO
 * {@link ShoppingCartDTO}.
 */
@Mapper(componentModel = "spring", uses = { CustomerMapper.class })
public interface ShoppingCartMapper extends EntityMapper<ShoppingCartDTO, ShoppingCart> {
    @Mapping(target = "customer", source = "customer", qualifiedByName = "customerName")
    ShoppingCartDTO toDto(ShoppingCart s);

    @Mapping(target = "items", ignore = true)
    @Mapping(target = "removeItems", ignore = true)
    ShoppingCart toEntity(ShoppingCartDTO shoppingCartDTO);

    @Mapping(target = "items", ignore = true)
    @Mapping(target = "removeItems", ignore = true)
    void partialUpdate(@MappingTarget ShoppingCart entity, ShoppingCartDTO dto);
}
