package com.aps.service.mapper;

import com.aps.domain.FishProduct;
import com.aps.domain.ReturnedOrder;
import com.aps.domain.ReturnedOrderItem;
import com.aps.service.dto.FishProductDTO;
import com.aps.service.dto.ReturnedOrderDTO;
import com.aps.service.dto.ReturnedOrderItemDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ReturnedOrderItem} and its DTO {@link ReturnedOrderItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface ReturnedOrderItemMapper extends EntityMapper<ReturnedOrderItemDTO, ReturnedOrderItem> {
    @Mapping(target = "product", source = "product", qualifiedByName = "fishProductName")
    @Mapping(target = "returnedOrder", source = "returnedOrder", qualifiedByName = "returnedOrderId")
    ReturnedOrderItemDTO toDto(ReturnedOrderItem s);

    @Named("fishProductName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    FishProductDTO toDtoFishProductName(FishProduct fishProduct);

    @Named("returnedOrderId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ReturnedOrderDTO toDtoReturnedOrderId(ReturnedOrder returnedOrder);
}
