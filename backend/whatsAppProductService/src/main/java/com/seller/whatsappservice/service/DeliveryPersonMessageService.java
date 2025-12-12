package com.seller.whatsappservice.service;

import org.springframework.stereotype.Service;

/**
 * Centralized service for all delivery person-facing messages in Malayalam
 * 
 * This service provides all delivery person messages in Malayalam language,
 * replacing hardcoded English messages throughout the application.
 * 
 * Benefits:
 * - Better experience for Malayalam-speaking delivery persons
 * - Centralized message management
 * - Easy to maintain and update messages
 * - Scalable for future multi-language support
 * 
 * @author WhatsApp Product Service Team
 * @version 1.0
 * @since 2025-12-04
 */
@Service
public class DeliveryPersonMessageService {

    // ========================================
    // CATEGORY 1: ORDER NOTIFICATION MESSAGES (5 messages)
    // ========================================

    /**
     * Order notification header
     * 
     * @param orderId Order ID
     * @return Order notification header in Malayalam
     */
    public String getOrderNotificationHeader(Long orderId) {
        return String.format("🔔 *പുതിയ ഓർഡർ #%d*\n\n", orderId);
    }

    /**
     * Customer details section
     * 
     * @param name  Customer name
     * @param phone Customer phone
     * @return Customer details in Malayalam
     */
    public String getCustomerDetails(String name, String phone) {
        return String.format("👤 *കസ്റ്റമർ:* %s\n📞 *ഫോൺ:* %s\n", name, phone);
    }

    /**
     * Location details
     * 
     * @param lat        Latitude (as String to support "null" value)
     * @param lon        Longitude (as String to support "null" value)
     * @param distanceKm Distance in kilometers (as String to support "null" value)
     * @return Location details in Malayalam
     */
    public String getLocationDetails(String lat, String lon, String distanceKm) {
        return String.format("📍 *ലൊക്കേഷൻ:* %s, %s\n📏 *ദൂരം:* %s km\n\n",
                lat, lon, distanceKm);
    }

    /**
     * Items header
     * 
     * @return Items header in Malayalam
     */
    public String getItemsHeader() {
        return "🐟 *ഇനങ്ങൾ:*\n";
    }

    /**
     * Order footer with total and payment
     * 
     * @param total     Total amount
     * @param orderTime Order time
     * @return Order footer in Malayalam
     */
    public String getOrderFooter(double total, String orderTime) {
        return String.format("\n💰 *ആകെ:* ₹%.2f\n💵 *പേയ്മെന്റ്:* COD\n⏰ *സമയം:* %s\n\n" +
                "ഈ ഓർഡർ എടുക്കാൻ ആരാണ് തയ്യാറുള്ളത്? 🚀", total, orderTime);
    }

    // ========================================
    // CATEGORY 2: ORDER CONFIRMATION MESSAGES (2 messages)
    // ========================================

    /**
     * Order already taken message
     * 
     * @param deliveryPersonName Name of delivery person who took the order
     * @return Order already taken message in Malayalam
     */
    public String getOrderAlreadyTaken(String deliveryPersonName) {
        return String.format("⚠️ *ഓർഡർ എടുത്തു കഴിഞ്ഞു!*\n\n" +
                "ഈ ഓർഡർ *%s* ഇതിനകം എടുത്തു കഴിഞ്ഞു. അടുത്ത തവണ ശ്രമിക്കൂ! ⚡",
                deliveryPersonName);
    }

    /**
     * Order confirmation success
     * 
     * @param orderId       Order ID
     * @param customerName  Customer name
     * @param customerPhone Customer phone
     * @return Order confirmation success message in Malayalam
     */
    public String getOrderConfirmationSuccess(Long orderId, String customerName, String customerPhone) {
        return String.format("✅ *ഓർഡർ സ്ഥിരീകരിച്ചു!* 🎉\n\n" +
                "നിങ്ങൾ ഓർഡർ #%d വിജയകരമായി എടുത്തു.\n" +
                "കസ്റ്റമർ: %s\n" +
                "ഫോൺ: %s\n\n" +
                "ഡെലിവറിക്ക് ഭാഗ്യം നേരുന്നു! 🚀",
                orderId, customerName, customerPhone);
    }

    // ========================================
    // CATEGORY 3: ERROR MESSAGES (1 message)
    // ========================================

    /**
     * Unauthorized delivery person message
     * 
     * @return Unauthorized access message in Malayalam
     */
    public String getUnauthorizedDeliveryMessage() {
        return "❌ *അനധികൃത ആക്സസ്*\n\n" +
                "നിങ്ങൾ ഡെലിവറി വ്യക്തിയായി രജിസ്റ്റർ ചെയ്തിട്ടില്ല. 🚫\n" +
                "അധികൃത ഡെലിവറി ഉദ്യോഗസ്ഥർക്ക് മാത്രമേ ഓർഡറുകൾ സ്ഥിരീകരിക്കാൻ കഴിയൂ.\n\n" +
                "ഇത് പിശകാണെന്ന് നിങ്ങൾ വിശ്വസിക്കുന്നുവെങ്കിൽ അഡ്മിനെ ബന്ധപ്പെടുക. 📞";
    }

    // ========================================
    // CATEGORY 4: GROUP NOTIFICATION MESSAGES (1 message)
    // ========================================

    /**
     * Delivery confirmation to group
     * 
     * @param orderId             Order ID
     * @param deliveryPersonName  Delivery person name
     * @param deliveryPersonPhone Delivery person phone
     * @param confirmedTime       Confirmation time
     * @return Group notification message in Malayalam
     */
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
    // CATEGORY 5: ACKNOWLEDGMENT MESSAGES (1 message)
    // ========================================

    /**
     * Delivery person acknowledgment (for non-button messages)
     * 
     * @return Acknowledgment message in Malayalam
     */
    public String getDeliveryPersonAcknowledgment() {
        return "നന്ദി! ഗ്രൂപ്പിലെ ഓർഡർ സ്ഥിരീകരണ ബട്ടണുകൾ ഉപയോഗിക്കുക.";
    }

    // ========================================
    // CATEGORY 6: WELCOME MESSAGES (1 message)
    // ========================================

    /**
     * Welcome message for new team members (Delivery/Executive)
     * 
     * @param name         Name of the member
     * @param phone        Phone number
     * @param role         Role assigned
     * @param addedBy      Name of admin who added them
     * @param addedByPhone Phone of admin
     * @return Welcome message in Malayalam
     */
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
}
