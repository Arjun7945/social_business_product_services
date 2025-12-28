package com.aps.service.mapper;

import com.aps.domain.DeliveryPerson;
import com.aps.domain.DeliveryZone;
import com.aps.domain.TeamMember;
import com.aps.service.dto.DeliveryPersonDTO;
import com.aps.service.dto.DeliveryZoneDTO;
import com.aps.service.dto.TeamMemberDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link DeliveryPerson} and its DTO {@link DeliveryPersonDTO}.
 */
@Mapper(componentModel = "spring")
public interface DeliveryPersonMapper extends EntityMapper<DeliveryPersonDTO, DeliveryPerson> {
    @Mapping(target = "addedBy", source = "addedBy", qualifiedByName = "teamMemberName")
    @Mapping(target = "zone", source = "zone", qualifiedByName = "deliveryZoneZoneName")
    DeliveryPersonDTO toDto(DeliveryPerson s);

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
