package com.aps.service.mapper;

import com.aps.domain.OrderStatusHistory;
import com.aps.service.dto.OrderStatusHistoryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link OrderStatusHistory} and its DTO
 * {@link OrderStatusHistoryDTO}.
 */
@Mapper(componentModel = "spring", uses = { CustomerOrderMapper.class })
public interface OrderStatusHistoryMapper extends EntityMapper<OrderStatusHistoryDTO, OrderStatusHistory> {
    @Mapping(target = "customerOrder", source = "customerOrder")
    OrderStatusHistoryDTO toDto(OrderStatusHistory s);
}
