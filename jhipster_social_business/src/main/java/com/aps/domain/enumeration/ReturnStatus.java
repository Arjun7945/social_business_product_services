package com.aps.domain.enumeration;

/**
 * Status for the Return Request Lifecycle
 */
public enum ReturnStatus {
    RETURN_REQUESTED,
    RETURN_APPROVED,
    PICKUP_SCHEDULED,
    PICKUP_COMPLETED,
    RECEIVED_AT_FACILITY,
    REFUND_INITIATED,
    REFUND_COMPLETED,
    RETURN_REJECTED,
}
