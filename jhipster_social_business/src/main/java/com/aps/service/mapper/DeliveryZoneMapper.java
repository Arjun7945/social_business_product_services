package com.aps.service.mapper;

import com.aps.domain.DeliveryZone;
import com.aps.service.dto.DeliveryZoneDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link DeliveryZone} and its DTO {@link DeliveryZoneDTO}.
 */
@Mapper(componentModel = "spring")
public interface DeliveryZoneMapper extends EntityMapper<DeliveryZoneDTO, DeliveryZone> {}
