package com.aps.service;

import com.aps.domain.TeamMember;
import com.aps.repository.TeamMemberRepository;
import com.aps.service.dto.TeamMemberDTO;
import com.aps.service.mapper.TeamMemberMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.aps.domain.TeamMember}.
 */
@Service
@Transactional
public class TeamMemberService {

    private static final Logger LOG = LoggerFactory.getLogger(TeamMemberService.class);

    private final TeamMemberRepository teamMemberRepository;

    private final TeamMemberMapper teamMemberMapper;

    public TeamMemberService(TeamMemberRepository teamMemberRepository, TeamMemberMapper teamMemberMapper) {
        this.teamMemberRepository = teamMemberRepository;
        this.teamMemberMapper = teamMemberMapper;
    }

    /**
     * Save a teamMember.
     *
     * @param teamMemberDTO the entity to save.
     * @return the persisted entity.
     */
    public TeamMemberDTO save(TeamMemberDTO teamMemberDTO) {
        LOG.debug("Request to save TeamMember : {}", teamMemberDTO);
        TeamMember teamMember = teamMemberMapper.toEntity(teamMemberDTO);
        teamMember = teamMemberRepository.save(teamMember);
        return teamMemberMapper.toDto(teamMember);
    }

    /**
     * Update a teamMember.
     *
     * @param teamMemberDTO the entity to save.
     * @return the persisted entity.
     */
    public TeamMemberDTO update(TeamMemberDTO teamMemberDTO) {
        LOG.debug("Request to update TeamMember : {}", teamMemberDTO);
        TeamMember teamMember = teamMemberMapper.toEntity(teamMemberDTO);
        teamMember = teamMemberRepository.save(teamMember);
        return teamMemberMapper.toDto(teamMember);
    }

    /**
     * Partially update a teamMember.
     *
     * @param teamMemberDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TeamMemberDTO> partialUpdate(TeamMemberDTO teamMemberDTO) {
        LOG.debug("Request to partially update TeamMember : {}", teamMemberDTO);

        return teamMemberRepository
            .findById(teamMemberDTO.getId())
            .map(existingTeamMember -> {
                teamMemberMapper.partialUpdate(existingTeamMember, teamMemberDTO);

                return existingTeamMember;
            })
            .map(teamMemberRepository::save)
            .map(teamMemberMapper::toDto);
    }

    /**
     * Get one teamMember by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TeamMemberDTO> findOne(Long id) {
        LOG.debug("Request to get TeamMember : {}", id);
        return teamMemberRepository.findById(id).map(teamMemberMapper::toDto);
    }

    /**
     * Delete the teamMember by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete TeamMember : {}", id);
        teamMemberRepository.deleteById(id);
    }
}
