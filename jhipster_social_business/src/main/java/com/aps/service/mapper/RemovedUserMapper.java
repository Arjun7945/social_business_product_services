package com.aps.service.mapper;

import com.aps.domain.RemovedUser;
import com.aps.service.dto.RemovedUserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link RemovedUser} and its DTO {@link RemovedUserDTO}.
 */
@Mapper(componentModel = "spring")
public interface RemovedUserMapper extends EntityMapper<RemovedUserDTO, RemovedUser> {}
