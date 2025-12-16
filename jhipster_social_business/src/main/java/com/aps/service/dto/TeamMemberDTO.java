package com.aps.service.dto;

import com.aps.domain.enumeration.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.aps.domain.TeamMember} entity.
 */
@Schema(description = "Internal Team Members (Admins, Executives, Delivery).\nRefactored: Removed all temp_ fields.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TeamMemberDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    private String waPhoneNumber;

    @NotNull
    private String phoneNumber;

    @NotNull
    private UserRole role;

    @NotNull
    private Boolean isActive;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWaPhoneNumber() {
        return waPhoneNumber;
    }

    public void setWaPhoneNumber(String waPhoneNumber) {
        this.waPhoneNumber = waPhoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TeamMemberDTO)) {
            return false;
        }

        TeamMemberDTO teamMemberDTO = (TeamMemberDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, teamMemberDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TeamMemberDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", waPhoneNumber='" + getWaPhoneNumber() + "'" +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", role='" + getRole() + "'" +
            ", isActive='" + getIsActive() + "'" +
            "}";
    }
}
