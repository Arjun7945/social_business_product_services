package com.aps.service.mapper;

import com.aps.domain.CustomerOrder;
import com.aps.service.dto.CustomerOrderDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CustomerOrder} and its DTO
 * {@link CustomerOrderDTO}.
 */
@Mapper(componentModel = "spring", uses = { CustomerMapper.class, TeamMemberMapper.class })
public interface CustomerOrderMapper extends EntityMapper<CustomerOrderDTO, CustomerOrder> {
    @Mapping(target = "deliveryPerson", source = "deliveryPerson", qualifiedByName = "teamMemberName")
    @Mapping(target = "customer", source = "customer", qualifiedByName = "customerName")
    CustomerOrderDTO toDto(CustomerOrder s);

    @Mapping(target = "items", ignore = true)
    @Mapping(target = "removeItems", ignore = true)
    CustomerOrder toEntity(CustomerOrderDTO customerOrderDTO);

    @Mapping(target = "items", ignore = true)
    @Mapping(target = "removeItems", ignore = true)
    void partialUpdate(@MappingTarget CustomerOrder entity, CustomerOrderDTO dto);
}
