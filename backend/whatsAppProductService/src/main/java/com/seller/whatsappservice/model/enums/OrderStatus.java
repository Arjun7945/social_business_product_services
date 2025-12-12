package com.seller.whatsappservice.model.enums;

/**
 * Represents the status of a customer order
 */
public enum OrderStatus {
    PENDING, // Order created, waiting for delivery person assignment
    CONFIRMED, // Delivery person assigned and confirmed
    DELIVERED, // Order has been delivered to customer
    CANCELLED // Order was cancelled
}
