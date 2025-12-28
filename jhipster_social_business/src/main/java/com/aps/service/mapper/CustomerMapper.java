package com.aps.service.mapper;

import com.aps.domain.Customer;
import com.aps.domain.DeliveryZone;
import com.aps.domain.TeamMember;
import com.aps.service.dto.CustomerDTO;
import com.aps.service.dto.DeliveryZoneDTO;
import com.aps.service.dto.TeamMemberDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Customer} and its DTO {@link CustomerDTO}.
 */
@Mapper(componentModel = "spring")
public interface CustomerMapper extends EntityMapper<CustomerDTO, Customer> {
    @Mapping(target = "addedBy", source = "addedBy", qualifiedByName = "teamMemberName")
    @Mapping(target = "zone", source = "zone", qualifiedByName = "deliveryZoneZoneName")
    CustomerDTO toDto(Customer s);

    @Named("teamMemberName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    TeamMemberDTO toDtoTeamMemberName(TeamMember teamMember);

    @Named("deliveryZoneZoneName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "zoneName", source = "zoneName")
    DeliveryZoneDTO toDtoDeliveryZoneZoneName(DeliveryZone deliveryZone);
}
