package com.aps.service.mapper;

import com.aps.domain.Customer;
import com.aps.service.dto.CustomerDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Customer} and its DTO {@link CustomerDTO}.
 */
@Mapper(componentModel = "spring", uses = { TeamMemberMapper.class })
public interface CustomerMapper extends EntityMapper<CustomerDTO, Customer> {
    @Mapping(target = "addedBy", source = "addedBy", qualifiedByName = "teamMemberName")
    CustomerDTO toDto(Customer s);

    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "removeOrders", ignore = true)
    @Mapping(target = "carts", ignore = true)
    @Mapping(target = "removeCart", ignore = true)
    Customer toEntity(CustomerDTO customerDTO);

    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "removeOrders", ignore = true)
    @Mapping(target = "carts", ignore = true)
    @Mapping(target = "removeCart", ignore = true)
    void partialUpdate(@MappingTarget Customer entity, CustomerDTO dto);

    @Named("customerName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    CustomerDTO toDtoCustomerName(Customer customer);
}
