package com.aps.service;

import org.springframework.stereotype.Service;

/**
 * Service for Accounts Team specific messages.
 */
@Service
public class AccountsMessageService {

    public String getAccountsWelcomeMessage(String name) {
        return "👋 Welcome Accounts Team Member *" + name + "*! 📊\n\n" + "Select a report to generate:";
    }

    public String getReportFormatSelectionMessage() {
        return "📄 Select output format for the report:";
    }

    public String getAccountsMenuTitle() {
        return "Accounts Dashboard";
    }

    public String getButtonTodaysOrders() {
        return "📅 Today's Orders";
    }

    public String getButtonCompletedOrders() {
        return "✅ Completed Orders";
    }

    public String getButtonUnpaidOrders() {
        return "💰 Unpaid Orders";
    }

    public String getButtonCreditReport() {
        return "💳 Credit Report";
    }

    public String getFeatureComingSoon() {
        return "ℹ️ This feature is coming in Phase 2!";
    }
}
