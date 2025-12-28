package com.aps.service.mapper;

import com.aps.domain.Customer;
import com.aps.domain.CustomerOrder;
import com.aps.domain.DeliveryPerson;
import com.aps.domain.OrderStatusHistory;
import com.aps.service.dto.CustomerDTO;
import com.aps.service.dto.CustomerOrderDTO;
import com.aps.service.dto.DeliveryPersonDTO;
import com.aps.service.dto.OrderStatusHistoryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CustomerOrder} and its DTO {@link CustomerOrderDTO}.
 */
@Mapper(componentModel = "spring")
public interface CustomerOrderMapper extends EntityMapper<CustomerOrderDTO, CustomerOrder> {
    @Mapping(target = "history", source = "history", qualifiedByName = "orderStatusHistoryId")
    @Mapping(target = "customer", source = "customer", qualifiedByName = "customerName")
    @Mapping(target = "deliveryPerson", source = "deliveryPerson", qualifiedByName = "deliveryPersonName")
    CustomerOrderDTO toDto(CustomerOrder s);

    @Named("orderStatusHistoryId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OrderStatusHistoryDTO toDtoOrderStatusHistoryId(OrderStatusHistory orderStatusHistory);

    @Named("customerName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    CustomerDTO toDtoCustomerName(Customer customer);

    @Named("deliveryPersonName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    DeliveryPersonDTO toDtoDeliveryPersonName(DeliveryPerson deliveryPerson);
}
