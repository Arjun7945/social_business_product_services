package com.seller.whatsappservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for cart item with product details
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {
    private Long fishProductId;
    private String fishName;
    private Double quantityKg;
    private Double pricePerKg;
    private Double subtotal;
    private String imageUrl;
}
