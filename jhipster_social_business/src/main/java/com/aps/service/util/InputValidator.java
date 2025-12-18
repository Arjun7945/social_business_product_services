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
        return true;
    }

    public boolean isValidPhoneNumber(String phone) {
        return phone != null && phone.matches("^[0-9+ ]{7,16}$");
    }
}
