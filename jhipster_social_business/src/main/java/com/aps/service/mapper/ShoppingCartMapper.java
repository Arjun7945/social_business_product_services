package com.aps.service.mapper;

import com.aps.domain.Customer;
import com.aps.domain.ShoppingCart;
import com.aps.service.dto.CustomerDTO;
import com.aps.service.dto.ShoppingCartDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ShoppingCart} and its DTO {@link ShoppingCartDTO}.
 */
@Mapper(componentModel = "spring")
public interface ShoppingCartMapper extends EntityMapper<ShoppingCartDTO, ShoppingCart> {
    @Mapping(target = "customer", source = "customer", qualifiedByName = "customerName")
    ShoppingCartDTO toDto(ShoppingCart s);

    @Named("customerName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    CustomerDTO toDtoCustomerName(Customer customer);
}
