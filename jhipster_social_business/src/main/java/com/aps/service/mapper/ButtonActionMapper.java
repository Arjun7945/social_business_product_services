package com.aps.service.mapper;

import com.aps.domain.ButtonAction;
import com.aps.service.dto.ButtonActionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ButtonAction} and its DTO {@link ButtonActionDTO}.
 */
@Mapper(componentModel = "spring")
public interface ButtonActionMapper extends EntityMapper<ButtonActionDTO, ButtonAction> {}
