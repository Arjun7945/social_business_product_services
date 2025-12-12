package com.seller.whatsappservice.model;

import com.seller.whatsappservice.model.enums.CustomerFlowStage;
import com.seller.whatsappservice.model.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String waPhoneNumber; // WhatsApp ID (e.g., 919876543210)

    private String name;

    private String phoneNumber; // Customer's actual phone number

    private Double locationLat;
    private Double locationLon;

    private Double distanceFromBusinessKm; // Calculated distance from business location

    @Column(nullable = false)
    @Builder.Default
    private Boolean isPincodeValid = false; // Whether the customer's pincode is valid for delivery

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CustomerFlowStage currentFlowStage = CustomerFlowStage.NEW;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private UserRole role = UserRole.CUSTOMER;

    private Long addedByExecutiveId; // Track which executive added this customer

    private LocalDateTime registeredAt;

    private LocalDateTime lastInteractionAt;

    // Temporary field to store selected product ID while waiting for quantity
    private Long tempSelectedProductId;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        lastInteractionAt = LocalDateTime.now();
    }
}
