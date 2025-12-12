package com.seller.whatsappservice.model;

import com.seller.whatsappservice.model.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "customer_orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long customerId; // Foreign key to Customer

    @Column(nullable = false)
    private LocalDateTime orderTime;

    @Column(nullable = false)
    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    @Column(nullable = false)
    @Builder.Default
    private String paymentMethod = "COD"; // Default to Cash on Delivery

    private Long teamMemberId; // Assigned team member ID (delivery person or executive)

    private String teamMemberWaId; // WhatsApp ID of team member

    private String teamMemberName; // Name of team member

    private LocalDateTime confirmedAt; // When delivery person confirmed

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> items;
}
