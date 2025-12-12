package com.seller.whatsappservice.model.enums;

/**
 * Represents the current stage of customer interaction in the WhatsApp flow
 */
public enum CustomerFlowStage {
    NEW, // First time user, not yet greeted
    AWAITING_NAME, // Asked for name
    AWAITING_PHONE, // Asked for phone number
    AWAITING_LOCATION, // Asked for location
    REGISTERED, // Successfully registered and within delivery area
    BROWSING, // Viewing product catalog
    ADDING_TO_CART, // Adding products to cart
    AWAITING_QUANTITY, // Asked for quantity of selected product
    CHECKOUT, // Reviewing cart before order
    CONFIRMING_ORDER, // Final confirmation stage
    EDITING_ORDER, // User selected "Edit Order" - showing edit options
    EDITING_PRODUCT, // User is editing/removing products from cart
    EDITING_QUANTITY // User is editing quantities of cart items
}
