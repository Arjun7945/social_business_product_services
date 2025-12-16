package com.aps.service.mapper;

import com.aps.domain.BotSession;
import com.aps.service.dto.BotSessionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link BotSession} and its DTO {@link BotSessionDTO}.
 */
@Mapper(componentModel = "spring")
public interface BotSessionMapper extends EntityMapper<BotSessionDTO, BotSession> {}
