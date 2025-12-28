package com.aps.service.mapper;

import com.aps.domain.ReturnStatusHistory;
import com.aps.domain.ReturnedOrder;
import com.aps.service.dto.ReturnStatusHistoryDTO;
import com.aps.service.dto.ReturnedOrderDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ReturnStatusHistory} and its DTO {@link ReturnStatusHistoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface ReturnStatusHistoryMapper extends EntityMapper<ReturnStatusHistoryDTO, ReturnStatusHistory> {
    @Mapping(target = "returnedOrder", source = "returnedOrder", qualifiedByName = "returnedOrderId")
    ReturnStatusHistoryDTO toDto(ReturnStatusHistory s);

    @Named("returnedOrderId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ReturnedOrderDTO toDtoReturnedOrderId(ReturnedOrder returnedOrder);
}
