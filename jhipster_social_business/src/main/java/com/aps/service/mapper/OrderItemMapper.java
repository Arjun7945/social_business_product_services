package com.aps.service.mapper;

import com.aps.domain.CustomerOrder;
import com.aps.domain.FishProduct;
import com.aps.domain.OrderItem;
import com.aps.service.dto.CustomerOrderDTO;
import com.aps.service.dto.FishProductDTO;
import com.aps.service.dto.OrderItemDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link OrderItem} and its DTO {@link OrderItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface OrderItemMapper extends EntityMapper<OrderItemDTO, OrderItem> {
    @Mapping(target = "product", source = "product", qualifiedByName = "fishProductName")
    @Mapping(target = "order", source = "order", qualifiedByName = "customerOrderId")
    OrderItemDTO toDto(OrderItem s);

    @Named("fishProductName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    FishProductDTO toDtoFishProductName(FishProduct fishProduct);

    @Named("customerOrderId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CustomerOrderDTO toDtoCustomerOrderId(CustomerOrder customerOrder);
}
