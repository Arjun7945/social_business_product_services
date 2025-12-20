package com.aps.service.util;

import org.springframework.stereotype.Component;
import java.util.Set;

@Component
public class InputValidator {

    private static final Set<String> INVALID_NAMES = Set.of(
            "Hi", "Hello", "Start", "Test", "Guest");

    public boolean isValidName(String name) {
        if (name == null || name.trim().length() < 2) {
            return false;
        }
        String trimmed = name.trim();
        for (String invalid : INVALID_NAMES) {
            if (trimmed.equalsIgnoreCase(invalid)) {
                return false;
            }
        }
        // Allow Unicode letters (for Malayalam/English), spaces, and dots.
        // Reject numbers and other special characters.
        return trimmed.matches("^[\\p{L} .]+$");
    }

    public boolean isValidPhoneNumber(String phone) {
        // Regex matches:
        // Optional '+' at start
        // Followed by 10 to 12 digits (e.g. 9876543210 or 919876543210)
        // No spaces allowed to ensure clean data
        return phone != null && phone.matches("^\\+?[0-9]{10,12}$");
    }

    public boolean isValidQuantity(Double quantity) {
        // Enforce reasonable limits: 0.1kg to 100kg per item/action
        return quantity != null && quantity >= 0.1 && quantity <= 100.0;
    }

    public Double cleanQuantityInput(String input) {
        if (input == null) {
            return null;
        }
        // Remove "kg", "grams", spaces, etc.
        // Replace comma with dot for international formats if needed (keeping simple
        // for now)
        String cleaned = input.toLowerCase()
                .replace("kg", "")
                .replace("kgs", "")
                .replace("kilograms", "")
                .replace("kilo", "")
                .trim();
        try {
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            return null; // Return null to indicate invalid format
        }
    }
}
