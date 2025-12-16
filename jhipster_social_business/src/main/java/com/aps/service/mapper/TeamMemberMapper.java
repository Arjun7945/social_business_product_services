package com.aps.service.mapper;

import com.aps.domain.TeamMember;
import com.aps.service.dto.TeamMemberDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TeamMember} and its DTO {@link TeamMemberDTO}.
 */
@Mapper(componentModel = "spring")
public interface TeamMemberMapper extends EntityMapper<TeamMemberDTO, TeamMember> {
    @Named("teamMemberName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    TeamMemberDTO toDtoTeamMemberName(TeamMember teamMember);
}
