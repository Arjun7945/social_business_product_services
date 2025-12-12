package com.seller.whatsappservice.model;

import com.seller.whatsappservice.model.enums.FlowType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "bot_sessions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BotSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String waPhoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FlowType flowType;

    private String currentStage;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "session_attributes", joinColumns = @JoinColumn(name = "session_id"))
    @MapKeyColumn(name = "attribute_key")
    @Column(name = "attribute_value")
    @Builder.Default
    private Map<String, String> attributes = new HashMap<>();

    private LocalDateTime lastUpdatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        lastUpdatedAt = LocalDateTime.now();
    }
}
