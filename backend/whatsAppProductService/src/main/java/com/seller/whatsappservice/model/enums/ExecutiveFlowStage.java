package com.seller.whatsappservice.model.enums;

/**
 * Enum representing different stages in the executive conversation flow
 */
public enum ExecutiveFlowStage {
    IDLE, // Default state, showing main menu
    AWAITING_CUST_NAME, // Waiting for customer name input
    AWAITING_CUST_PHONE, // Waiting for customer phone number
    AWAITING_CUST_WAPHONE, // Waiting for customer WhatsApp number
    AWAITING_CUST_LOCATION // Waiting for customer location
}
