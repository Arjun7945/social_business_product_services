package com.aps.service;

import org.springframework.stereotype.Service;

/**
 * Service to handle all Admin-facing messages.
 * Centralizes strings to avoid hardcoding in business logic.
 */
@Service
public class AdminMessageService {

    // ========================================
    // CATEGORY 1: CREDIT CUSTOMER FLOW MESSAGES
    // ========================================

    public String getNoCreditOrdersFound() {
        return "No credit customer orders found till now.";
    }

    public String getCreditGrantedAlwaysMessage(String customerName, Long orderId) {
        return "✅ Approved: Always Grant for " + customerName;
    }

    public String getCreditOneTimeApprovedMessage(Long orderId) {
        return "✅ Approved: Only for this Order" + orderId;
    }

    public String getCreditDeniedMessage(Long orderId) {
        return "❌ Denied Credit for Order " + orderId;
    }

    public String getItemsHeader() {
        return "📦 *Order Details*\n";
    }

    public String getOrderDetails(Long orderId, String customerName, double amount, String status) {
        return String.format(
                "📦 *Order Details*\n" +
                        "ID: %d\n" +
                        "Name: %s\n" +
                        "Amount: ₹%.2f\n" +
                        "Status: %s\n",
                orderId, customerName, amount, status);
    }

    public String getPaymentLinkGeneratedSuccess() {
        return "✅ Payment link generated and sent.";
    }

    public String getQrCodeGeneratedSuccess() {
        return "✅ QR Code generated and sent.";
    }

    public String getMarkedAsCodSuccess() {
        return "✅ Marked as COD Delivered.";
    }

    public String getRazorpayLinkError() {
        return "❌ Error: Failed to generate Razorpay link.";
    }

    public String getQrCodeError() {
        return "❌ Error: Failed to generate QR Code.";
    }

    public String getOrderOrDpNotFoundError() {
        return "❌ Error: Order or Delivery Person not found.";
    }

    // Interactive Button/List Titles & Descriptions

    public String getMenuTitle() {
        return "💳 *Credit Customer Management*";
    }

    public String getOrderListTitle() {
        return "📦 *Credit Customer Orders*";
    }

    public String getBackToCreditMenuTitle() {
        return "⬅️ Back";
    }

    public String getBackToCreditMenuDesc() {
        return "Credit Menu";
    }

    public String getLinkButtonTitle() {
        return "🔗 Send Link";
    }

    public String getQrButtonTitle() {
        return "📷 Send QR";
    }

    public String getCodButtonTitle() {
        return "💵 Marked COD";
    }

    public String getLinkButtonDesc() {
        return "Send Razorpay Link";
    }

    public String getQrButtonDesc() {
        return "Send QR Code";
    }

    public String getCodButtonDesc() {
        return "Mark as Paid (COD)";
    }

    public String getListOptionsButtonText() {
        return "Options";
    }

    public String getListActionSectionTitle() {
        return "Actions";
    }

    // ===========================================================
    // CATEGORY 12: ADMIN RECIEVING MESSAGES FROM DELIVERY PERSON
    // ===========================================================

    public String getPaymentResistedAdminInfo(String dpName, String customerName, String customerRole, double amount,
            Long orderId) {
        return String.format(
                "Hello Admin, this is *%s*. I am currently attempting to deliver Order *#%d* to *%s* (%s). " +
                        "The customer is requesting to pay the bill amount of *₹%.2f* later rather than right now.\n\n"
                        +
                        "How would you like me to proceed? 👇",
                dpName, orderId, customerName, customerRole, amount);
    }

    public String getButtonAllowCreditOnce() {
        return "Allow for this Order";
    }

    public String getButtonGrantAlways() {
        return "Always Allow";
    }

    public String getButtonDenyCredit() {
        return "Deny Request";
    }
}
