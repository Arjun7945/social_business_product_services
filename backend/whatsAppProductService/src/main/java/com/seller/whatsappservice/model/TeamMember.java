package com.seller.whatsappservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.seller.whatsappservice.model.enums.AdminFlowStage;
import com.seller.whatsappservice.model.enums.ExecutiveFlowStage;
import com.seller.whatsappservice.model.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "team_members")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String waPhoneNumber; // WhatsApp ID (e.g., 919876543210)

    @Column(unique = true, nullable = false)
    private String phoneNumber; // Regular phone number

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role; // EXECUTIVE, DELIVERY_PERSON, ASSISTANT_ADMIN, ADMIN, or DEVELOPER

    @Column(nullable = false)
    @Builder.Default
    @JsonProperty("isActive")
    private boolean isActive = true;

    // Executive-specific fields
    @Enumerated(EnumType.STRING)
    private ExecutiveFlowStage currentFlowStage;

    // Admin-specific fields
    @Enumerated(EnumType.STRING)
    private AdminFlowStage currentAdminFlowStage;

    // All temp fields removed. Use BotSessionService instead.
    /*
     * private Long tempCustomerId;
     * private String tempCustomerName;
     * private String tempCustomerPhone;
     * private String tempCustomerWaPhone;
     * private String tempEntityType;
     * private Long tempEntityId;
     * private String tempFieldName;
     * private String tempFieldValue;
     * private String tempTeamMemberName;
     * private String tempTeamMemberPhone;
     * private String tempTeamMemberWaPhone;
     * private Boolean tempTeamMemberIsActive;
     */
}
