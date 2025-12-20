package com.aps.service;

import org.springframework.stereotype.Service;

/**
 * Centralized service for all customer-facing messages in Malayalam
 */
@Service
public class CustomerMessageService {

    // ========================================
    // CATEGORY 9: PAYMENT MESSAGES
    // ========================================

    public String getPaymentQrCaption(double amount) {
        return String.format("📱 *Scan & Pay* \n\n" +
                "\u20B9%.2f\n\n" +
                "താങ്കളുടെ ഓർഡറിനുള്ള പേയ്മെൻ്റ് ചെയ്യാൻ ഈ QR Code സ്കാൻ ചെയ്യുക. 🤝", amount);
    }

    public String getPaymentLinkMessage(String link, double amount) {
        return String.format("🔗 *പേയ്മെന്റ് ലിങ്ക്* \n\n" +
                "തുക: \u20B9%.2f\n\n" +
                "പേയ്മെൻ്റ് ചെയ്യാൻ താഴെ കാണുന്ന ലിങ്കിൽ ക്ലിക്ക് ചെയ്യുക: 👇\n%s", amount, link);
    }

    public String getPaymentCapturedMessage(String paymentId, double amount, Long orderId) {
        return String.format("🎉 *പേയ്മെന്റ് വിജയിച്ചു!* ✅\n\n" +
                "ഓർഡർ #%d\n" +
                "തുക: \u20B9%.2f\n" +
                "Ref: *%s*\n\n" +
                "നിങ്ങളുടെ ഓർഡർ സ്ഥിരീകരിച്ചു! നന്ദി! 🙏", orderId, amount, paymentId);
    }

    public String getPaymentFailedMessage(String paymentId, Long orderId) {
        return String.format("❌ *പേയ്മെന്റ് പരാജയപ്പെട്ടു!* ⚠️\n\n" +
                "ഓർഡർ #%d\n" +
                "Ref: *%s*\n\n" +
                "ദയവായി വീണ്ടും ശ്രമിക്കുക അല്ലെങ്കിൽ ക്യാഷ് (COD) തിരഞ്ഞെടുക്കുക.", orderId, paymentId);
    }

    // ========================================
    // CATEGORY 1: REGISTRATION FLOW MESSAGES
    // ========================================

    public String getWelcomeMessageNewCustomer() {
        return "🙏 *ഞങ്ങളുടെ ഫ്രഷ് ഫിഷ് സ്റ്റോറിലേക്ക് സ്വാഗതം!* 🐟\n\n" +
                "നിങ്ങളെ ഇവിടെ കാണുന്നതിൽ ഞങ്ങൾക്ക് സന്തോഷമുണ്ട്! ✨\n\n" +
                "നിങ്ങളെ നന്നായി സേവിക്കാൻ, നിങ്ങളുടെ പേര് എന്താണെന്ന് അറിയാമോ?";
    }

    public String getWelcomeMessageOtherText() {
        return "👋 *ഹലോ! ഞങ്ങളുടെ ഫ്രഷ് ഫിഷ് സ്റ്റോറിലേക്ക് സ്വാഗതം!* 🐟\n\n" +
                "ഞങ്ങൾ ദിവസത്തിലെ ഏറ്റവും പുതിയ മീൻ നിങ്ങളുടെ വീട്ടിൽ എത്തിക്കുന്നു! 🚚\n\n" +
                "ആരംഭിക്കാൻ *'Hi'* എന്ന് അയക്കുക. 😊";
    }

    public String getNameConfirmation(String customerName) {
        return String.format("🙏 *%s, നിങ്ങളെ കാണാൻ സന്തോഷം!* 👋\n\n" +
                "ഞങ്ങളെ തിരഞ്ഞെടുത്തതിന് നന്ദി! ഏറ്റവും പുതിയ മീൻ നിങ്ങൾക്ക് നൽകാൻ ഞങ്ങൾ ആവേശത്തിലാണ്. 🐟\n\n" +
                "നിങ്ങളുടെ ഫോൺ നമ്പർ പങ്കിടാമോ?",
                customerName);
    }

    public String getPhoneConfirmationAndLocationRequest() {
        return "✅ *മികച്ചത്! നന്ദി!* 🙏\n\n" +
                "ഇപ്പോൾ, പുതിയ മീൻ നിങ്ങളുടെ വീട്ടിൽ എത്തിക്കാൻ, നിങ്ങളുടെ ലൊക്കേഷൻ പങ്കിടുക. 📍\n\n" +
                "📢 *എങ്ങനെ പങ്കിടാം:*\n" +
                "• അറ്റാച്ച്മെന്റ് ഐക്കൺ (📎) ടാപ്പ് ചെയ്യുക\n" +
                "• 'Location' തിരഞ്ഞെടുക്കുക\n" +
                "• നിങ്ങളുടെ നിലവിലെ ലൊക്കേഷൻ അയക്കുക\n\n" +
                "ഇത് ഞങ്ങളെ നിങ്ങളെ നന്നായി സേവിക്കാൻ സഹായിക്കുന്നു! 😊";
    }

    public String getLocationAccepted(String customerName, double distance) {
        return String.format("✨ *അതിശയകരമായ വാർത്ത, %s!* 🎉\n\n" +
                "✅ നിങ്ങൾ ഞങ്ങളുടെ ഡെലിവറി ഏരിയയിലാണ്! നിങ്ങളെ സേവിക്കാൻ ഞങ്ങൾ സന്തോഷിക്കുന്നു. 🙏\n" +
                "📍 ഞങ്ങളുടെ കടയിൽ നിന്നുള്ള ദൂരം: *%.2f km*\n\n" +
                "🐟 *ഞങ്ങളുടെ പുതിയ മീൻ കാണാൻ തയ്യാറാണോ?*\n" +
                "ഞങ്ങളുടെ പ്രീമിയം മീൻ തിരഞ്ഞെടുക്കാൻ *'start'* എന്ന് അയക്കുക!\n\n" +
                "💚 ഏറ്റവും പുതിയ ഗുണനിലവാരം, ശ്രദ്ധയോടെ ഡെലിവർ ചെയ്യുമെന്ന് ഞങ്ങൾ വാഗ്ദാനം ചെയ്യുന്നു!",
                customerName, distance);
    }

    public String getLocationRejected(String customerName, double distance) {
        return String.format("😔 *ക്ഷമിക്കണം, %s!*\n\n" +
                "നിർഭാഗ്യവശാൽ, നിങ്ങളുടെ ലൊക്കേഷൻ ഞങ്ങളുടെ നിലവിലെ ഡെലിവറി ഏരിയയ്ക്ക് പുറത്താണ്. 📍\n\n" +
                "📍 ഞങ്ങളുടെ കടയിൽ നിന്നുള്ള ദൂരം: *%.2f km*\n" +
                "🚚 ഞങ്ങളുടെ ഡെലിവറി പരിധി: *50 km*\n\n" +
                "💔 ഭാവിയിൽ നിങ്ങളെ സേവിക്കാൻ ഞങ്ങൾ ആഗ്രഹിക്കുന്നു!\n" +
                "ഞങ്ങൾ നിരന്തരം ഞങ്ങളുടെ ഡെലിവറി ഏരിയകൾ വിപുലീകരിക്കുന്നു. ഉടൻ തന്നെ ഞങ്ങളെ വീണ്ടും സമീപിക്കുക! 🙏\n\n"
                +
                "നിങ്ങളുടെ താൽപ്പര്യത്തിന് നന്ദി! ❤️",
                customerName, distance);
    }

    public String getRegisteredCustomerGreeting(String customerName) {
        return String.format("👋 *ഹലോ %s!* 😊\n\n" +
                "🐟 ഞങ്ങളുടെ പുതിയ മീൻ തിരഞ്ഞെടുക്കാൻ തയ്യാറാണോ?\n\n" +
                "ഇന്നത്തെ പ്രീമിയം മീൻ കാണാൻ *'start'* എന്ന് അയക്കുക! ✨",
                customerName);
    }

    // ========================================
    // CATEGORY 2: PRODUCT BROWSING MESSAGES
    // ========================================

    public String getProductCatalogHeader(String customerName) {
        return String.format("🐟 *ഇന്ന് ലഭ്യമായ പുതിയ മീൻ, %s!* ✨\n\n" +
                "🌊 പ്രീമിയം ഗുണനിലവാരം, പുതുതായി പിടിച്ചത്!\n" +
                "🚚 നിങ്ങളുടെ വീട്ടിൽ എത്തിക്കുന്നു!\n\n" +
                "നിങ്ങളുടെ കാർട്ടിലേക്ക് ചേർക്കാൻ ഒരു മീൻ തിരഞ്ഞെടുക്കുക:",
                customerName);
    }

    public String getNoProductsAvailable(String customerName) {
        return String.format("😔 *ക്ഷമിക്കണം, %s!*\n\n" +
                "ഈ നിമിഷം ഞങ്ങളുടെ പക്കൽ പുതിയ മീൻ ലഭ്യമല്ല. 🐟\n\n" +
                "🕒 ഞങ്ങളുടെ പുതിയ സ്റ്റോക്ക് ദിവസവും എത്തുന്നു!\n" +
                "അൽപ്പസമയത്തിനുള്ളിൽ ഞങ്ങളെ വീണ്ടും സമീപിക്കുക. ഏറ്റവും പുതിയ മീൻ നിങ്ങൾക്കായി തയ്യാറാക്കാം! ✨\n\n" +
                "നിങ്ങളുടെ ക്ഷമയ്ക്ക് നന്ദി! 🙏",
                customerName);
    }

    public String getProductSelectedQuantityRequest(String customerName, String fishName, double price) {
        return String.format("✅ *മികച്ച തിരഞ്ഞെടുപ്പ്, %s!* 🎉\n\n" +
                "🐟 നിങ്ങൾ തിരഞ്ഞെടുത്തത്: *%s*\n" +
                "💰 വില: *₹%.2f കിലോയ്ക്ക്*\n\n" +
                "⚖️ എത്ര കിലോഗ്രാം വേണം?\n" +
                "(ഉദാഹരണം: 2 അല്ലെങ്കിൽ 2.5)",
                customerName, fishName, price);
    }

    public String getProductNoLongerAvailable() {
        return "😔 *ക്ഷമിക്കണം!*\n\n" +
                "ആ മീൻ ഇപ്പോൾ ലഭ്യമല്ല. 🐟\n\n" +
                "നിലവിലെ പുതിയ മീൻ കാണാൻ *'start'* എന്ന് അയക്കുക! ✨";
    }

    public String getPromptToBrowse(String customerName) {
        return String.format("👋 *ഹലോ %s!* 😊\n\n" +
                "🐟 ഞങ്ങളുടെ പുതിയ മീൻ തിരഞ്ഞെടുക്കാൻ തയ്യാറാണോ?\n\n" +
                "ഇന്നത്തെ പ്രീമിയം മീൻ കാണാൻ *'start'* എന്ന് അയക്കുക! ✨",
                customerName);
    }

    // ========================================
    // CATEGORY 3: CART MANAGEMENT MESSAGES
    // ========================================

    public String getInvalidQuantityZeroOrNegative() {
        return "⚠️ *തെറ്റായ അളവ്!*\n\n" +
                "0-ൽ കൂടുതൽ അളവ് നൽകുക.\n" +
                "ഉദാഹരണം: 2 അല്ലെങ്കിൽ 2.5 😊";
    }

    public String getInvalidQuantityFormat() {
        return "⚠️ *ക്ഷമിക്കണം! തെറ്റായ ഇൻപുട്ട്*\n\n" +
                "അളവിനായി സാധുവായ നമ്പർ നൽകുക.\n" +
                "ഉദാഹരണങ്ങൾ: *2* അല്ലെങ്കിൽ *1.5* അല്ലെങ്കിൽ *3.5* 😊";
    }

    public String getInvalidPhoneNumber() {
        return "⚠️ *തെറ്റായ ഫോൺ നമ്പർ!*\n\n" +
                "ദയവായി സാധുവായ ഒരു ഫോൺ നമ്പർ നൽകുക (അക്കങ്ങൾ മാത്രം).\n" +
                "ഉദാഹരണം: 9876543210 😊";
    }

    public String getQuantityUpdated() {
        return "✅ *മികച്ചത്!* 🎉\n\n" +
                "അളവ് വിജയകരമായി അപ്ഡേറ്റ് ചെയ്തു! ✨";
    }

    public String getQuantityUpdatedMessage(double quantity) {
        return String.format("✅ *മികച്ചത്!* 🎉\n\n" +
                "അളവ് വിജയകരമായി അപ്ഡേറ്റ് ചെയ്തു: *%.2f kg* ✨", quantity);
    }

    public String getItemAddedToCart(String customerName) {
        return String.format("✅ *കാർട്ടിലേക്ക് വിജയകരമായി ചേർത്തു!* 🎉\n\n" +
                "👍 മികച്ച തിരഞ്ഞെടുപ്പ്, %s!\n\n" +
                "അടുത്തതായി എന്താണ് ചെയ്യാൻ ആഗ്രഹിക്കുന്നത്?",
                customerName);
    }

    public String getCartSummaryHeader(String customerName) {
        return String.format("🛒 *നിങ്ങളുടെ കാർട്ട് സംഗ്രഹം, %s:*\n\n", customerName);
    }

    public String getCartSummaryFooter(double total) {
        return String.format("\n💰 *ആകെ തുക: ₹%.2f*\n" +
                "💵 *പേയ്മെന്റ്: COD (ക്യാഷ് ഓൺ ഡെലിവറി)*\n\n" +
                "✅ നിങ്ങളുടെ ഓർഡർ സ്ഥിരീകരിക്കാൻ തയ്യാറാണോ?",
                total);
    }

    public String getEmptyCart() {
        return "നിങ്ങളുടെ കാർട്ട് ശൂന്യമാണ്! മീൻ കാണാൻ 'start' എന്ന് അയക്കുക.";
    }

    public String getEditOrderMenu(String customerName) {
        return String.format("📝 *നിങ്ങളുടെ ഓർഡർ എഡിറ്റ് ചെയ്യുക, %s!*\n\n" +
                "എന്താണ് മാറ്റാൻ ആഗ്രഹിക്കുന്നത്?",
                customerName);
    }

    public String getRemoveProductHeaderSingle() {
        return "🗑️ *ഉൽപ്പന്നം നീക്കം ചെയ്യുക*\n\n*നിലവിലെ കാർട്ട്:*\n";
    }

    public String getRemoveProductPrompt() {
        return "ഈ ഇനം നീക്കം ചെയ്യണോ?";
    }

    public String getEditQuantityHeader(String fishName, double quantity) {
        return String.format("*%s-ന്റെ നിലവിലെ അളവ്:* %.2f kg\n\n" +
                "പുതിയ അളവ് നൽകുക (കിലോയിൽ):",
                fishName, quantity);
    }

    // ========================================
    // CATEGORY 4: ORDER MANAGEMENT MESSAGES
    // ========================================

    public String getOrderConfirmation(String orderId, double total) {
        return String.format("🎉 *ഓർഡർ സ്ഥിരീകരിച്ചു!* 🐟\n\n" +
                "ഓർഡർ #%s\n" +
                "💰 *ആകെ:* ₹%.2f\n" +
                "💳 *പേയ്മെന്റ്:* COD (ക്യാഷ് ഓൺ ഡെലിവറി)\n\n" +
                "നിങ്ങളുടെ ഓർഡർ ഞങ്ങളുടെ ഡെലിവറി ടീമിലേക്ക് അയച്ചു. 🚀\n" +
                "ഒരു ഡെലിവറി വ്യക്തി നിയോഗിക്കപ്പെട്ടാൽ ഉടൻ നിങ്ങളെ അറിയിക്കും.\n\n" +
                "ഞങ്ങളെ തിരഞ്ഞെടുത്തതിന് നന്ദി! 🌊",
                orderId, total);
    }

    public String getDeliveryAssignmentNotification(String deliveryPersonName, String deliveryPersonPhone) {
        return String.format("🎉 *ഓർഡർ സ്ഥിരീകരിച്ചു!* 🚀\n\n" +
                "നിങ്ങളുടെ ഓർഡർ ഞങ്ങളുടെ ഡെലിവറി വ്യക്തി ഏറ്റെടുത്തു!\n\n" +
                "👤 *ഡെലിവറി വ്യക്തി:* %s\n" +
                "📞 *WhatsApp:* %s\n\n" +
                "ഡെലിവറിക്കായി അവർ ഉടൻ നിങ്ങളെ ബന്ധപ്പെടും. 📦",
                deliveryPersonName, deliveryPersonPhone);
    }

    public String getOrderShippedMessage() {
        return "🚚 *ഓർഡർ വഴിയിലാണ്!*\n\n" +
                "നിങ്ങളുടെ ഓർഡർ ഡെലിവറിക്കായി പുറപ്പെട്ടു!";
    }

    public String getOrderDeliveredMessage() {
        return "📦 *ഓർഡർ ഡെലിവർ ചെയ്തു!*\n\n" +
                "നിങ്ങളുടെ ഓർഡർ ഡെലിവർ ചെയ്തു. ഞങ്ങളോടൊപ്പം ഷോപ്പിംഗ് നടത്തിയതിന് നന്ദി! 🐟";
    }

    public String getOrderAgainPrompt() {
        return "🔄 *വീണ്ടും ഓർഡർ ചെയ്യണോ?*\n\n" +
                "പുതിയ സാധനങ്ങൾ ഓർഡർ ചെയ്യണമെങ്കിൽ, താഴെ നിന്ന് തിരഞ്ഞെടുക്കൂ... 👇";
    }

    public String getOrderCancelled(String customerName) {
        return String.format("✅ *ഓർഡർ റദ്ദാക്കി, %s* 🙏\n\n" +
                "🗑️ നിങ്ങളുടെ കാർട്ട് ക്ലിയർ ചെയ്തു.\n\n" +
                "🐟 നിങ്ങൾ തയ്യാറാകുമ്പോൾ, ഞങ്ങളുടെ പുതിയ മീൻ വീണ്ടും കാണാൻ *'start'* എന്ന് അയക്കുക! ✨",
                customerName);
    }

    public String getEmptyCartDuringOrder() {
        return "നിങ്ങളുടെ കാർട്ട് ശൂന്യമാണ്!";
    }

    public String getAllProductsRemoved(String customerName) {
        return String.format("✅ *എല്ലാ ഉൽപ്പന്നങ്ങളും നീക്കം ചെയ്തു!* 🙏\n\n" +
                "🛒 നിങ്ങളുടെ കാർട്ട് ഇപ്പോൾ ശൂന്യമാണ്, %s.\n\n" +
                "🐟 ഇന്ന് ലഭ്യമായ ഞങ്ങളുടെ പുതിയ മീൻ ഇതാ:",
                customerName);
    }

    // ========================================
    // CATEGORY 5: PRODUCT REMOVAL MESSAGES
    // ========================================

    public String getProductRemovedSuccessfully() {
        return "✅ *ഉൽപ്പന്നം വിജയകരമായി നീക്കം ചെയ്തു!* 👍\n\n" +
                "ഇനം നിങ്ങളുടെ കാർട്ടിൽ നിന്ന് നീക്കം ചെയ്തു. ✨";
    }

    public String getCartEmptyAfterRemoval() {
        return "നിങ്ങളുടെ കാർട്ട് ഇപ്പോൾ ശൂന്യമാണ്. ലഭ്യമായ ഞങ്ങളുടെ ഉൽപ്പന്നങ്ങൾ ഇതാ:";
    }

    public String getRemoveProductsListHeader() {
        return "🗑️ *ഉൽപ്പന്നങ്ങൾ നീക്കം ചെയ്യുക*\n\n*നിലവിലെ കാർട്ട്:*\n";
    }

    public String getSelectProductToRemove() {
        return "നീക്കം ചെയ്യേണ്ട ഉൽപ്പന്നം തിരഞ്ഞെടുക്കുക:";
    }

    // ========================================
    // CATEGORY 6: ERROR/FALLBACK MESSAGES
    // ========================================

    public String getUnrecognizedCommand() {
        return "എനിക്ക് അത് മനസ്സിലായില്ല. ഞങ്ങളുടെ മീൻ തിരഞ്ഞെടുക്കാൻ 'start' എന്ന് അയക്കുക.";
    }

    public String getOrderPlacementFailure() {
        return "ക്ഷമിക്കണം, നിങ്ങളുടെ ഓർഡർ നൽകുന്നതിൽ ഒരു പ്രശ്നമുണ്ടായി. ദയവായി വീണ്ടും ശ്രമിക്കുക.";
    }

    public String getRemoveProductsPrompt() {
        return "🗑️ *ഉൽപ്പന്നങ്ങൾ നീക്കം ചെയ്യുക*\n\n" +
                "ഒരു ഓപ്ഷൻ തിരഞ്ഞെടുക്കുക:";
    }

    public String getSessionResumptionPrompt(String customerName) {
        return String.format("👋 *ഹലോ %s!* 😊\n\n" +
                "താങ്കൾക്ക് ഒരു സജീവ സെഷൻ ഉണ്ട്. 🛒\n\n" +
                "മുമ്പത്തെ കാര്യങ്ങൾ തുടരണോ അതോ പുതിയതായി തുടങ്ങണോ?", customerName);
    }

    public String getButtonResume() {
        return "▶️ തുടരുക";
    }

    public String getButtonStartNew() {
        return "🆕 പുതിയത് തുടങ്ങുക";
    }

    // ========================================
    // CATEGORY 7: EXECUTIVE WELCOME MESSAGE
    // ========================================

    public String getCustomerWelcomeByExecutive(String customerName, String customerPhone,
            String executiveName, String executivePhone) {
        return String.format("🎉 *ഞങ്ങളുടെ ഫ്രഷ് ഫിഷ് സ്റ്റോറിലേക്ക് സ്വാഗതം!* 🐟\n\n" +
                "ഹലോ *%s!* 👋\n" +
                "ഫോൺ: %s\n\n" +
                "*%s* (📞 %s) നിങ്ങളെ ഞങ്ങളുടെ കസ്റ്റമർ ലിസ്റ്റിലേക്ക് ചേർത്തു.\n\n" +
                "ദിവസവും പുതിയ മീൻ ഓർഡർ ചെയ്യാൻ തയ്യാറാകൂ! 🌊\n\n" +
                "ദിവസേനയുള്ള പുതിയ മീൻ വിശദാംശങ്ങൾ കാണാനും തുടരാനും 'start' എന്ന് അയക്കുക. 🚀",
                customerName, customerPhone, executiveName, executivePhone);
    }

    // ========================================
    // CATEGORY 8: BUTTON LABELS
    // ========================================

    public String getButtonAddMoreFish() {
        return "കൂടുതൽ മീൻ ചേർക്കുക";
    }

    public String getButtonCheckout() {
        return "ചെക്ക്ഔട്ട്";
    }

    public String getButtonConfirmOrder() {
        return "ഓർഡർ സ്ഥിരീകരിക്കുക";
    }

    public String getButtonEditOrder() {
        return "ഓർഡർ എഡിറ്റ് ചെയ്യുക";
    }

    public String getButtonCancelOrder() {
        return "ഓർഡർ റദ്ദാക്കുക";
    }

    public String getButtonEditProduct() {
        return "ഉൽപ്പന്നം എഡിറ്റ് ചെയ്യുക";
    }

    public String getButtonEditQuantity() {
        return "അളവ് എഡിറ്റ് ചെയ്യുക";
    }

    public String getButtonBackToCheckout() {
        return "ചെക്ക്ഔട്ടിലേക്ക് മടങ്ങുക";
    }

    public String getButtonRemoveAll() {
        return "എല്ലാം നീക്കം ചെയ്യുക";
    }

    public String getButtonRemoveProduct(String productName) {
        return String.format("%s നീക്കം ചെയ്യുക", productName);
    }

    public String getButtonViewFish() {
        return "മീൻ കാണുക";
    }

    public String getSectionTitleAvailableFish() {
        return "ലഭ്യമായ മീൻ";
    }

    public String getButtonAcceptOrder() {
        return "✅ ഓർഡർ സ്വീകരിക്കുക";
    }
}
