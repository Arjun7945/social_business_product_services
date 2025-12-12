package com.seller.whatsappservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CustomerOrderDto {
    private Long id;
    private Long customerId;
    private String customerName;
    private Double totalAmount;
    private String status;
    private Long teamMemberId;
    private List<OrderItemDto> items;
}
