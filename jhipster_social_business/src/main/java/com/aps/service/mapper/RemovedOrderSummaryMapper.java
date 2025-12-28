package com.aps.service.mapper;

import com.aps.domain.RemovedOrderSummary;
import com.aps.service.dto.RemovedOrderSummaryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link RemovedOrderSummary} and its DTO {@link RemovedOrderSummaryDTO}.
 */
@Mapper(componentModel = "spring")
public interface RemovedOrderSummaryMapper extends EntityMapper<RemovedOrderSummaryDTO, RemovedOrderSummary> {}
