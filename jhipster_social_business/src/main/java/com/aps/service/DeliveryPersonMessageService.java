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

    public String getOrderAlreadyTaken(Long orderId, String status) {
        return String.format("⚠️ *ഓർഡർ എടുത്തു കഴിഞ്ഞു!*\n\n" +
                "ഓർഡർ #%d ഇതിനകം *%s* നിലയിലാണ്.\n" +
                "മറ്റാരോ ഇത് എടുത്തു കഴിഞ്ഞു. അടുത്ത തവണ ശ്രമിക്കൂ! ⚡",
                orderId, status);
    }

    public String getOrderConfirmationSuccess(Long orderId, String customerName, String customerPhone) {
        return String.format("✅ *ഓർഡർ സ്ഥിരീകരിച്ചു!* 🎉\n\n" +
                "നിങ്ങൾ ഓർഡർ #%d വിജയകരമായി എടുത്തു.\n" +
                "കസ്റ്റമർ: %s\n" +
                "ഫോൺ: %s\n\n" +
                "ഡെലിവറിക്ക് ഭാഗ്യം നേരുന്നു! 🚀",
                orderId, customerName, customerPhone);
    }

    public String getPaymentModeSelectionHeader(Long orderId, double amount) {
        return String.format("💰 *പേയ്മെന്റ് മോഡ് തിരഞ്ഞെടുക്കുക*\n\n" +
                "ഓർഡർ #%d\n" +
                "തുക: ₹%.2f\n\n" +
                "കസ്റ്റമർ എങ്ങനെ പണമടയ്ക്കും? 👇", orderId, amount);
    }

    public String getButtonCod() {
        return "💵 COD (കൈപ്പറ്റുമ്പോൾ)";
    }

    public String getButtonQr() {
        return "📷 QR Scan";
    }

    public String getButtonLink() {
        return "🔗 Payment Link";
    }

    public String getPaymentModeCodSelected() {
        return "✅ പേയ്മെന്റ് രീതി: COD തിരഞ്ഞെടുത്തു";
    }

    public String getDeliveryDashboardHeader(Long orderId, String customerName) {
        return String.format("🚀 *ഡെലിവറി നടപടികൾ*\n\n" +
                "ഓർഡർ #%d\n" +
                "കസ്റ്റമർ: %s\n\n" +
                "സ്റ്റാറ്റസ് അപ്ഡേറ്റ് ചെയ്യുക: 👇", orderId, customerName);
    }

    public String getButtonMarkShipped() {
        return "ഷിപ്പ് ചെയ്തു";
    }

    public String getButtonMarkDelivered() {
        return "ഡെലിവർ ചെയ്തു";
    }

    public String getOrderDeliveredSuccess() {
        return "✅ ഓർഡർ ഡെലിവർ ചെയ്തതായി അടയാളപ്പെടുത്തി! മികച്ച ജോലി! 👏";
    }

    public String getOrderShippedSuccess() {
        return "✅ കസ്റ്റമറെ അറിയിച്ചു: ഓർഡർ വഴിയിലാണ്! 🚚";
    }

    public String getOrderNotAssignedWarning() {
        return "⚠️ നിങ്ങൾ ഈ ഓർഡർക്കായി നിയോഗിക്കപ്പെട്ടിട്ടില്ല.";
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

    // ========================================
    // CATEGORY 9: PAYMENT STATUS MESSAGES
    // ========================================

    public String getPaymentReceivedMessage(String paymentId, double amount, Long orderId) {
        return String.format("✅ *പേയ്മെന്റ് ലഭിച്ചു!* 💰\n\n" +
                "ഓർഡർ #%d\n" +
                "തുക: \u20B9%.2f\n" +
                "Ref: *%s*\n\n" +
                "ഇനി ഡെലിവറി തുടരാം! 🚀", orderId, amount, paymentId);
    }

    public String getPaymentFailedMessage(String paymentId, Long orderId) {
        return String.format("❌ *പേയ്മെന്റ് പരാജയപ്പെട്ടു!* ⚠️\n\n" +
                "ഓർഡർ #%d\n" +
                "Ref: *%s*\n\n" +
                "കസ്റ്റമറോട് പണം നൽകാനോ അല്ലെങ്കിൽ വീണ്ടും ശ്രമിക്കാനോ പറയുക.", orderId, paymentId);
    }

    public String getPaymentWaitMessageQr() {
        return "QR ഉപഭോക്താവിന് അയച്ചു. പേയ്മെന്റ് സ്ഥിരീകരണം ലഭിക്കുന്നത് വരെ കാത്തിരിക്കുക. ഉപഭോക്താവിന് ലഭിച്ചില്ലെങ്കിൽ, ദയവായി ഇത് പങ്കിടുക. ⏳";
    }

    public String getPaymentWaitMessageLink() {
        return "പേയ്മെന്റ് ലിങ്ക് ഉപഭോക്താവിന് അയച്ചു. പേയ്മെന്റ് സ്ഥിരീകരണം ലഭിക്കുന്നത് വരെ കാത്തിരിക്കുക. ഉപഭോക്താവിന് ലഭിച്ചില്ലെങ്കിൽ, ദയവായി ഇത് പങ്കിടുക. ⏳";
    }
}
