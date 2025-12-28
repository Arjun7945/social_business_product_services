package com.aps.service.mapper;

import com.aps.domain.Customer;
import com.aps.domain.CustomerOrder;
import com.aps.domain.ReturnedOrder;
import com.aps.service.dto.CustomerDTO;
import com.aps.service.dto.CustomerOrderDTO;
import com.aps.service.dto.ReturnedOrderDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ReturnedOrder} and its DTO {@link ReturnedOrderDTO}.
 */
@Mapper(componentModel = "spring")
public interface ReturnedOrderMapper extends EntityMapper<ReturnedOrderDTO, ReturnedOrder> {
    @Mapping(target = "order", source = "order", qualifiedByName = "customerOrderId")
    @Mapping(target = "customer", source = "customer", qualifiedByName = "customerName")
    ReturnedOrderDTO toDto(ReturnedOrder s);

    @Named("customerOrderId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CustomerOrderDTO toDtoCustomerOrderId(CustomerOrder customerOrder);

    @Named("customerName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    CustomerDTO toDtoCustomerName(Customer customer);
}
