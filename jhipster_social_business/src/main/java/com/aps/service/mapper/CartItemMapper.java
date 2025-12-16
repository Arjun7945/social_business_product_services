package com.aps.service.mapper;

import com.aps.domain.CartItem;
import com.aps.domain.FishProduct;
import com.aps.domain.ShoppingCart;
import com.aps.service.dto.CartItemDTO;
import com.aps.service.dto.FishProductDTO;
import com.aps.service.dto.ShoppingCartDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CartItem} and its DTO {@link CartItemDTO}.
 */
@Mapper(componentModel = "spring", uses = { FishProductMapper.class, ShoppingCartMapper.class })
public interface CartItemMapper extends EntityMapper<CartItemDTO, CartItem> {
    @Mapping(target = "product", source = "product", qualifiedByName = "fishProductName")
    @Mapping(target = "cart", source = "cart", qualifiedByName = "shoppingCartId")
    CartItemDTO toDto(CartItem s);

    @Named("fishProductName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    FishProductDTO toDtoFishProductName(FishProduct fishProduct);

    @Named("shoppingCartId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ShoppingCartDTO toDtoShoppingCartId(ShoppingCart shoppingCart);
}
