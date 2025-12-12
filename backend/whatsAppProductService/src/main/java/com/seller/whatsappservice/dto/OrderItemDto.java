package com.seller.whatsappservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemDto {
    private Long id;
    private Long fishProductId;
    private String fishProductName;
    private Double quantity;
    private Double pricePerKg;
    private Double totalPrice;
}
