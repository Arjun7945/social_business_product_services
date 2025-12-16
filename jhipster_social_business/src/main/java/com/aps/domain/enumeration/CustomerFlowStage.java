package com.aps.domain.enumeration;

/**
 * The CustomerFlowStage enumeration.
 */
public enum CustomerFlowStage {
    NEW,
    AWAITING_NAME,
    AWAITING_PHONE,
    AWAITING_LOCATION,
    REGISTERED,
    BROWSING,
    ADDING_TO_CART,
    AWAITING_QUANTITY,
    CHECKOUT,
    CONFIRMING_ORDER,
    EDITING_ORDER,
    EDITING_PRODUCT,
    EDITING_QUANTITY,
}
