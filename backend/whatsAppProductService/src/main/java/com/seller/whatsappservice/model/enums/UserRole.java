package com.seller.whatsappservice.model.enums;

/**
 * Enum representing different user roles in the system
 */
public enum UserRole {
    CUSTOMER, // End users who order products
    EXECUTIVE, // Staff who add customers
    DELIVERY_PERSON, // Staff who deliver orders
    ASSISTANT_ADMIN, // Managers who oversee operations
    ADMIN, // System administrators
    DEVELOPER // Technical team members
}
