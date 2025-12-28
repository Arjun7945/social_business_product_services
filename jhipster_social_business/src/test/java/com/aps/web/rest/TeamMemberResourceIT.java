package com.aps.web.rest;

import static com.aps.domain.TeamMemberAsserts.*;
import static com.aps.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.aps.IntegrationTest;
import com.aps.domain.TeamMember;
import com.aps.domain.enumeration.UserRole;
import com.aps.repository.TeamMemberRepository;
import com.aps.service.dto.TeamMemberDTO;
import com.aps.service.mapper.TeamMemberMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link TeamMemberResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TeamMemberResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_WA_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_WA_PHONE_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBB";

    private static final UserRole DEFAULT_ROLE = UserRole.CUSTOMER;
    private static final UserRole UPDATED_ROLE = UserRole.EXECUTIVE;

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/team-members";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private TeamMemberMapper teamMemberMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTeamMemberMockMvc;

    private TeamMember teamMember;

    private TeamMember insertedTeamMember;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TeamMember createEntity() {
        return new TeamMember()
            .name(DEFAULT_NAME)
            .waPhoneNumber(DEFAULT_WA_PHONE_NUMBER)
            .phoneNumber(DEFAULT_PHONE_NUMBER)
            .role(DEFAULT_ROLE)
            .isActive(DEFAULT_IS_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TeamMember createUpdatedEntity() {
        return new TeamMember()
            .name(UPDATED_NAME)
            .waPhoneNumber(UPDATED_WA_PHONE_NUMBER)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .role(UPDATED_ROLE)
            .isActive(UPDATED_IS_ACTIVE);
    }

    @BeforeEach
    public void initTest() {
        teamMember = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedTeamMember != null) {
            teamMemberRepository.delete(insertedTeamMember);
            insertedTeamMember = null;
        }
    }

    @Test
    @Transactional
    void createTeamMember() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TeamMember
        TeamMemberDTO teamMemberDTO = teamMemberMapper.toDto(teamMember);
        var returnedTeamMemberDTO = om.readValue(
            restTeamMemberMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(teamMemberDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TeamMemberDTO.class
        );

        // Validate the TeamMember in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTeamMember = teamMemberMapper.toEntity(returnedTeamMemberDTO);
        assertTeamMemberUpdatableFieldsEquals(returnedTeamMember, getPersistedTeamMember(returnedTeamMember));

        insertedTeamMember = returnedTeamMember;
    }

    @Test
    @Transactional
    void createTeamMemberWithExistingId() throws Exception {
        // Create the TeamMember with an existing ID
        teamMember.setId(1L);
        TeamMemberDTO teamMemberDTO = teamMemberMapper.toDto(teamMember);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTeamMemberMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(teamMemberDTO)))
            .andExpect(status().isBadRequest());

        // Validate the TeamMember in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        teamMember.setName(null);

        // Create the TeamMember, which fails.
        TeamMemberDTO teamMemberDTO = teamMemberMapper.toDto(teamMember);

        restTeamMemberMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(teamMemberDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPhoneNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        teamMember.setPhoneNumber(null);

        // Create the TeamMember, which fails.
        TeamMemberDTO teamMemberDTO = teamMemberMapper.toDto(teamMember);

        restTeamMemberMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(teamMemberDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRoleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        teamMember.setRole(null);

        // Create the TeamMember, which fails.
        TeamMemberDTO teamMemberDTO = teamMemberMapper.toDto(teamMember);

        restTeamMemberMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(teamMemberDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        teamMember.setIsActive(null);

        // Create the TeamMember, which fails.
        TeamMemberDTO teamMemberDTO = teamMemberMapper.toDto(teamMember);

        restTeamMemberMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(teamMemberDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTeamMembers() throws Exception {
        // Initialize the database
        insertedTeamMember = teamMemberRepository.saveAndFlush(teamMember);

        // Get all the teamMemberList
        restTeamMemberMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(teamMember.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].waPhoneNumber").value(hasItem(DEFAULT_WA_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].role").value(hasItem(DEFAULT_ROLE.toString())))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)));
    }

    @Test
    @Transactional
    void getTeamMember() throws Exception {
        // Initialize the database
        insertedTeamMember = teamMemberRepository.saveAndFlush(teamMember);

        // Get the teamMember
        restTeamMemberMockMvc
            .perform(get(ENTITY_API_URL_ID, teamMember.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(teamMember.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.waPhoneNumber").value(DEFAULT_WA_PHONE_NUMBER))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER))
            .andExpect(jsonPath("$.role").value(DEFAULT_ROLE.toString()))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE));
    }

    @Test
    @Transactional
    void getTeamMembersByIdFiltering() throws Exception {
        // Initialize the database
        insertedTeamMember = teamMemberRepository.saveAndFlush(teamMember);

        Long id = teamMember.getId();

        defaultTeamMemberFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTeamMemberFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTeamMemberFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTeamMembersByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTeamMember = teamMemberRepository.saveAndFlush(teamMember);

        // Get all the teamMemberList where name equals to
        defaultTeamMemberFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTeamMembersByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTeamMember = teamMemberRepository.saveAndFlush(teamMember);

        // Get all the teamMemberList where name in
        defaultTeamMemberFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTeamMembersByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTeamMember = teamMemberRepository.saveAndFlush(teamMember);

        // Get all the teamMemberList where name is not null
        defaultTeamMemberFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllTeamMembersByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedTeamMember = teamMemberRepository.saveAndFlush(teamMember);

        // Get all the teamMemberList where name contains
        defaultTeamMemberFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTeamMembersByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTeamMember = teamMemberRepository.saveAndFlush(teamMember);

        // Get all the teamMemberList where name does not contain
        defaultTeamMemberFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllTeamMembersByWaPhoneNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTeamMember = teamMemberRepository.saveAndFlush(teamMember);

        // Get all the teamMemberList where waPhoneNumber equals to
        defaultTeamMemberFiltering("waPhoneNumber.equals=" + DEFAULT_WA_PHONE_NUMBER, "waPhoneNumber.equals=" + UPDATED_WA_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllTeamMembersByWaPhoneNumberIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTeamMember = teamMemberRepository.saveAndFlush(teamMember);

        // Get all the teamMemberList where waPhoneNumber in
        defaultTeamMemberFiltering(
            "waPhoneNumber.in=" + DEFAULT_WA_PHONE_NUMBER + "," + UPDATED_WA_PHONE_NUMBER,
            "waPhoneNumber.in=" + UPDATED_WA_PHONE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllTeamMembersByWaPhoneNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTeamMember = teamMemberRepository.saveAndFlush(teamMember);

        // Get all the teamMemberList where waPhoneNumber is not null
        defaultTeamMemberFiltering("waPhoneNumber.specified=true", "waPhoneNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllTeamMembersByWaPhoneNumberContainsSomething() throws Exception {
        // Initialize the database
        insertedTeamMember = teamMemberRepository.saveAndFlush(teamMember);

        // Get all the teamMemberList where waPhoneNumber contains
        defaultTeamMemberFiltering(
            "waPhoneNumber.contains=" + DEFAULT_WA_PHONE_NUMBER,
            "waPhoneNumber.contains=" + UPDATED_WA_PHONE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllTeamMembersByWaPhoneNumberNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTeamMember = teamMemberRepository.saveAndFlush(teamMember);

        // Get all the teamMemberList where waPhoneNumber does not contain
        defaultTeamMemberFiltering(
            "waPhoneNumber.doesNotContain=" + UPDATED_WA_PHONE_NUMBER,
            "waPhoneNumber.doesNotContain=" + DEFAULT_WA_PHONE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllTeamMembersByPhoneNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTeamMember = teamMemberRepository.saveAndFlush(teamMember);

        // Get all the teamMemberList where phoneNumber equals to
        defaultTeamMemberFiltering("phoneNumber.equals=" + DEFAULT_PHONE_NUMBER, "phoneNumber.equals=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllTeamMembersByPhoneNumberIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTeamMember = teamMemberRepository.saveAndFlush(teamMember);

        // Get all the teamMemberList where phoneNumber in
        defaultTeamMemberFiltering(
            "phoneNumber.in=" + DEFAULT_PHONE_NUMBER + "," + UPDATED_PHONE_NUMBER,
            "phoneNumber.in=" + UPDATED_PHONE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllTeamMembersByPhoneNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTeamMember = teamMemberRepository.saveAndFlush(teamMember);

        // Get all the teamMemberList where phoneNumber is not null
        defaultTeamMemberFiltering("phoneNumber.specified=true", "phoneNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllTeamMembersByPhoneNumberContainsSomething() throws Exception {
        // Initialize the database
        insertedTeamMember = teamMemberRepository.saveAndFlush(teamMember);

        // Get all the teamMemberList where phoneNumber contains
        defaultTeamMemberFiltering("phoneNumber.contains=" + DEFAULT_PHONE_NUMBER, "phoneNumber.contains=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllTeamMembersByPhoneNumberNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTeamMember = teamMemberRepository.saveAndFlush(teamMember);

        // Get all the teamMemberList where phoneNumber does not contain
        defaultTeamMemberFiltering(
            "phoneNumber.doesNotContain=" + UPDATED_PHONE_NUMBER,
            "phoneNumber.doesNotContain=" + DEFAULT_PHONE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllTeamMembersByRoleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTeamMember = teamMemberRepository.saveAndFlush(teamMember);

        // Get all the teamMemberList where role equals to
        defaultTeamMemberFiltering("role.equals=" + DEFAULT_ROLE, "role.equals=" + UPDATED_ROLE);
    }

    @Test
    @Transactional
    void getAllTeamMembersByRoleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTeamMember = teamMemberRepository.saveAndFlush(teamMember);

        // Get all the teamMemberList where role in
        defaultTeamMemberFiltering("role.in=" + DEFAULT_ROLE + "," + UPDATED_ROLE, "role.in=" + UPDATED_ROLE);
    }

    @Test
    @Transactional
    void getAllTeamMembersByRoleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTeamMember = teamMemberRepository.saveAndFlush(teamMember);

        // Get all the teamMemberList where role is not null
        defaultTeamMemberFiltering("role.specified=true", "role.specified=false");
    }

    @Test
    @Transactional
    void getAllTeamMembersByIsActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTeamMember = teamMemberRepository.saveAndFlush(teamMember);

        // Get all the teamMemberList where isActive equals to
        defaultTeamMemberFiltering("isActive.equals=" + DEFAULT_IS_ACTIVE, "isActive.equals=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllTeamMembersByIsActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTeamMember = teamMemberRepository.saveAndFlush(teamMember);

        // Get all the teamMemberList where isActive in
        defaultTeamMemberFiltering("isActive.in=" + DEFAULT_IS_ACTIVE + "," + UPDATED_IS_ACTIVE, "isActive.in=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllTeamMembersByIsActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTeamMember = teamMemberRepository.saveAndFlush(teamMember);

        // Get all the teamMemberList where isActive is not null
        defaultTeamMemberFiltering("isActive.specified=true", "isActive.specified=false");
    }

    private void defaultTeamMemberFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultTeamMemberShouldBeFound(shouldBeFound);
        defaultTeamMemberShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTeamMemberShouldBeFound(String filter) throws Exception {
        restTeamMemberMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(teamMember.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].waPhoneNumber").value(hasItem(DEFAULT_WA_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].role").value(hasItem(DEFAULT_ROLE.toString())))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)));

        // Check, that the count call also returns 1
        restTeamMemberMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTeamMemberShouldNotBeFound(String filter) throws Exception {
        restTeamMemberMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTeamMemberMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTeamMember() throws Exception {
        // Get the teamMember
        restTeamMemberMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTeamMember() throws Exception {
        // Initialize the database
        insertedTeamMember = teamMemberRepository.saveAndFlush(teamMember);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the teamMember
        TeamMember updatedTeamMember = teamMemberRepository.findById(teamMember.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTeamMember are not directly saved in db
        em.detach(updatedTeamMember);
        updatedTeamMember
            .name(UPDATED_NAME)
            .waPhoneNumber(UPDATED_WA_PHONE_NUMBER)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .role(UPDATED_ROLE)
            .isActive(UPDATED_IS_ACTIVE);
        TeamMemberDTO teamMemberDTO = teamMemberMapper.toDto(updatedTeamMember);

        restTeamMemberMockMvc
            .perform(
                put(ENTITY_API_URL_ID, teamMemberDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(teamMemberDTO))
            )
            .andExpect(status().isOk());

        // Validate the TeamMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTeamMemberToMatchAllProperties(updatedTeamMember);
    }

    @Test
    @Transactional
    void putNonExistingTeamMember() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        teamMember.setId(longCount.incrementAndGet());

        // Create the TeamMember
        TeamMemberDTO teamMemberDTO = teamMemberMapper.toDto(teamMember);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTeamMemberMockMvc
            .perform(
                put(ENTITY_API_URL_ID, teamMemberDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(teamMemberDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TeamMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTeamMember() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        teamMember.setId(longCount.incrementAndGet());

        // Create the TeamMember
        TeamMemberDTO teamMemberDTO = teamMemberMapper.toDto(teamMember);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeamMemberMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(teamMemberDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TeamMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTeamMember() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        teamMember.setId(longCount.incrementAndGet());

        // Create the TeamMember
        TeamMemberDTO teamMemberDTO = teamMemberMapper.toDto(teamMember);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeamMemberMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(teamMemberDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TeamMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTeamMemberWithPatch() throws Exception {
        // Initialize the database
        insertedTeamMember = teamMemberRepository.saveAndFlush(teamMember);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the teamMember using partial update
        TeamMember partialUpdatedTeamMember = new TeamMember();
        partialUpdatedTeamMember.setId(teamMember.getId());

        partialUpdatedTeamMember.name(UPDATED_NAME).waPhoneNumber(UPDATED_WA_PHONE_NUMBER);

        restTeamMemberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTeamMember.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTeamMember))
            )
            .andExpect(status().isOk());

        // Validate the TeamMember in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTeamMemberUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTeamMember, teamMember),
            getPersistedTeamMember(teamMember)
        );
    }

    @Test
    @Transactional
    void fullUpdateTeamMemberWithPatch() throws Exception {
        // Initialize the database
        insertedTeamMember = teamMemberRepository.saveAndFlush(teamMember);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the teamMember using partial update
        TeamMember partialUpdatedTeamMember = new TeamMember();
        partialUpdatedTeamMember.setId(teamMember.getId());

        partialUpdatedTeamMember
            .name(UPDATED_NAME)
            .waPhoneNumber(UPDATED_WA_PHONE_NUMBER)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .role(UPDATED_ROLE)
            .isActive(UPDATED_IS_ACTIVE);

        restTeamMemberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTeamMember.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTeamMember))
            )
            .andExpect(status().isOk());

        // Validate the TeamMember in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTeamMemberUpdatableFieldsEquals(partialUpdatedTeamMember, getPersistedTeamMember(partialUpdatedTeamMember));
    }

    @Test
    @Transactional
    void patchNonExistingTeamMember() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        teamMember.setId(longCount.incrementAndGet());

        // Create the TeamMember
        TeamMemberDTO teamMemberDTO = teamMemberMapper.toDto(teamMember);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTeamMemberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, teamMemberDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(teamMemberDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TeamMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTeamMember() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        teamMember.setId(longCount.incrementAndGet());

        // Create the TeamMember
        TeamMemberDTO teamMemberDTO = teamMemberMapper.toDto(teamMember);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeamMemberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(teamMemberDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TeamMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTeamMember() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        teamMember.setId(longCount.incrementAndGet());

        // Create the TeamMember
        TeamMemberDTO teamMemberDTO = teamMemberMapper.toDto(teamMember);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeamMemberMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(teamMemberDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TeamMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTeamMember() throws Exception {
        // Initialize the database
        insertedTeamMember = teamMemberRepository.saveAndFlush(teamMember);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the teamMember
        restTeamMemberMockMvc
            .perform(delete(ENTITY_API_URL_ID, teamMember.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return teamMemberRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected TeamMember getPersistedTeamMember(TeamMember teamMember) {
        return teamMemberRepository.findById(teamMember.getId()).orElseThrow();
    }

    protected void assertPersistedTeamMemberToMatchAllProperties(TeamMember expectedTeamMember) {
        assertTeamMemberAllPropertiesEquals(expectedTeamMember, getPersistedTeamMember(expectedTeamMember));
    }

    protected void assertPersistedTeamMemberToMatchUpdatableProperties(TeamMember expectedTeamMember) {
        assertTeamMemberAllUpdatablePropertiesEquals(expectedTeamMember, getPersistedTeamMember(expectedTeamMember));
    }
}
