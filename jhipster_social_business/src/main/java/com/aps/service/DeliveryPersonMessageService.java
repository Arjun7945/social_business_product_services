package com.aps.service;

import org.springframework.stereotype.Service;

/**
 * Centralized service for all delivery person-facing messages in Malayalam
 */
@Service
public class DeliveryPersonMessageService {

    // ========================================
    // CATEGORY 1: ORDER NOTIFICATION MESSAGES
    // ========================================

    public String getOrderNotificationHeader(Long orderId) {
        return String.format("🔔 *പുതിയ ഓർഡർ #%d*\n\n", orderId);
    }

    public String getCustomerDetails(String name, String phone) {
        return String.format("👤 *കസ്റ്റമർ:* %s\n📞 *ഫോൺ:* %s\n", name, phone);
    }

    public String getLocationDetails(String lat, String lon, String distanceKm) {
        return String.format("📍 *ലൊക്കേഷൻ:* %s, %s\n📏 *ദൂരം:* %s km\n\n",
                lat, lon, distanceKm);
    }

    public String getItemsHeader() {
        return "🐟 *ഇനങ്ങൾ:*\n";
    }

    public String getOrderFooter(double total, String orderTime) {
        return String.format("\n💰 *ആകെ:* ₹%.2f\n💵 *പേയ്മെന്റ്:* COD\n⏰ *സമയം:* %s\n\n" +
                "ഈ ഓർഡർ എടുക്കാൻ ആരാണ് തയ്യാറുള്ളത്? 🚀", total, orderTime);
    }

    // ========================================
    // CATEGORY 2: ORDER CONFIRMATION MESSAGES
    // ========================================

    public String getOrderAlreadyTaken(String deliveryPersonName) {
        return String.format("⚠️ *ഓർഡർ എടുത്തു കഴിഞ്ഞു!*\n\n" +
                "ഈ ഓർഡർ *%s* ഇതിനകം എടുത്തു കഴിഞ്ഞു. അടുത്ത തവണ ശ്രമിക്കൂ! ⚡",
                deliveryPersonName);
    }

    public String getOrderConfirmationSuccess(Long orderId, String customerName, String customerPhone) {
        return String.format("✅ *ഓർഡർ സ്ഥിരീകരിച്ചു!* 🎉\n\n" +
                "നിങ്ങൾ ഓർഡർ #%d വിജയകരമായി എടുത്തു.\n" +
                "കസ്റ്റമർ: %s\n" +
                "ഫോൺ: %s\n\n" +
                "ഡെലിവറിക്ക് ഭാഗ്യം നേരുന്നു! 🚀",
                orderId, customerName, customerPhone);
    }

    // ========================================
    // CATEGORY 3: ERROR MESSAGES
    // ========================================

    public String getUnauthorizedDeliveryMessage() {
        return "❌ *അനധികൃത ആക്സസ്*\n\n" +
                "നിങ്ങൾ ഡെലിവറി വ്യക്തിയായി രജിസ്റ്റർ ചെയ്തിട്ടില്ല. 🚫\n" +
                "അധികൃത ഡെലിവറി ഉദ്യോഗസ്ഥർക്ക് മാത്രമേ ഓർഡറുകൾ സ്ഥിരീകരിക്കാൻ കഴിയൂ.\n\n" +
                "ഇത് പിശകാണെന്ന് നിങ്ങൾ വിശ്വസിക്കുന്നുവെങ്കിൽ അഡ്മിനെ ബന്ധപ്പെടുക. 📞";
    }

    // ========================================
    // CATEGORY 4: GROUP NOTIFICATION MESSAGES
    // ========================================

    public String getDeliveryConfirmationToGroup(Long orderId, String deliveryPersonName,
            String deliveryPersonPhone, String confirmedTime) {
        return String.format("✅ *ഓർഡർ അസൈൻ ചെയ്തു* 🚀\n\n" +
                "📦 *ഓർഡർ #%d* എടുത്തു കഴിഞ്ഞു!\n\n" +
                "🚴 *ഡെലിവറി വ്യക്തി:*\n" +
                "   👤 പേര്: *%s*\n" +
                "   📞 ഫോൺ: %s\n\n" +
                "⏰ സ്ഥിരീകരിച്ച സമയം: %s\n\n" +
                "കസ്റ്റമറെ ഉടൻ അറിയിക്കും. മികച്ച ജോലി! 👏",
                orderId, deliveryPersonName, deliveryPersonPhone, confirmedTime);
    }

    // ========================================
    // CATEGORY 5: ACKNOWLEDGMENT MESSAGES
    // ========================================

    public String getDeliveryPersonAcknowledgment() {
        return "നന്ദി! ഗ്രൂപ്പിലെ ഓർഡർ സ്ഥിരീകരണ ബട്ടണുകൾ ഉപയോഗിക്കുക.";
    }

    // ========================================
    // CATEGORY 6: WELCOME MESSAGES
    // ========================================

    public String getTeamMemberWelcomeMessage(String name, String phone, String role, String addedBy,
            String addedByPhone) {
        return String.format("🎉 *ടീമിലേക്ക് സ്വാഗതം!* 🙌\n\n" +
                "ഹലോ *%s*! 👋\n" +
                "ഫോൺ: %s\n\n" +
                "*%s* (📞 %s) നിങ്ങളെ *%s* ആയി നിയമിച്ചിരിക്കുന്നു.\n\n" +
                "ജോലി ആരംഭിക്കാൻ തയ്യാറാകൂ! 🚀\n\n" +
                "ആരംഭിക്കാൻ 'hi' എന്ന് അയക്കുക.",
                name, phone, addedBy, addedByPhone, role);
    }

    public String getDeliveryPersonWelcomeMessage(String name) {
        return String.format("👋 *സ്വാഗതം %s!* 🚚\n\n" +
                "ഓർഡറുകൾ ലഭിക്കാൻ തയ്യാറായിരിക്കുക. 🚀", name);
    }
}
