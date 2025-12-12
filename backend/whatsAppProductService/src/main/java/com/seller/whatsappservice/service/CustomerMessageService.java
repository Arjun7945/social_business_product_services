package com.seller.whatsappservice.service;

import org.springframework.stereotype.Service;

/**
 * Centralized service for all customer-facing messages in Malayalam
 * 
 * This service provides all 38 customer messages in Malayalam language,
 * replacing hardcoded English messages throughout the application.
 * 
 * Benefits:
 * - Better customer experience for Malayalam-speaking users
 * - Centralized message management
 * - Easy to maintain and update messages
 * - Scalable for future multi-language support
 * 
 * @author WhatsApp Product Service Team
 * @version 1.0
 * @since 2025-12-04
 */
@Service
public class CustomerMessageService {

    // ========================================
    // CATEGORY 1: REGISTRATION FLOW MESSAGES (7 messages)
    // ========================================

    /**
     * Welcome message for new customers who send "Hi" or "Hello"
     * Asks for customer's name to begin registration
     * 
     * @return Welcome message in Malayalam
     */
    public String getWelcomeMessageNewCustomer() {
        return "🙏 *ഞങ്ങളുടെ ഫ്രഷ് ഫിഷ് സ്റ്റോറിലേക്ക് സ്വാഗതം!* 🐟\n\n" +
                "നിങ്ങളെ ഇവിടെ കാണുന്നതിൽ ഞങ്ങൾക്ക് സന്തോഷമുണ്ട്! ✨\n\n" +
                "നിങ്ങളെ നന്നായി സേവിക്കാൻ, നിങ്ങളുടെ പേര് എന്താണെന്ന് അറിയാമോ?";
    }

    /**
     * Welcome message for new customers who send text other than "Hi/Hello"
     * Guides them to send "Hi" to start
     * 
     * @return Guidance message in Malayalam
     */
    public String getWelcomeMessageOtherText() {
        return "👋 *ഹലോ! ഞങ്ങളുടെ ഫ്രഷ് ഫിഷ് സ്റ്റോറിലേക്ക് സ്വാഗതം!* 🐟\n\n" +
                "ഞങ്ങൾ ദിവസത്തിലെ ഏറ്റവും പുതിയ മീൻ നിങ്ങളുടെ വീട്ടിൽ എത്തിക്കുന്നു! 🚚\n\n" +
                "ആരംഭിക്കാൻ *'Hi'* എന്ന് അയക്കുക. 😊";
    }

    /**
     * Confirmation message after receiving customer's name
     * Requests phone number
     * 
     * @param customerName Customer's name
     * @return Name confirmation message in Malayalam
     */
    public String getNameConfirmation(String customerName) {
        return String.format("🙏 *%s, നിങ്ങളെ കാണാൻ സന്തോഷം!* 👋\n\n" +
                "ഞങ്ങളെ തിരഞ്ഞെടുത്തതിന് നന്ദി! ഏറ്റവും പുതിയ മീൻ നിങ്ങൾക്ക് നൽകാൻ ഞങ്ങൾ ആവേശത്തിലാണ്. 🐟\n\n" +
                "നിങ്ങളുടെ ഫോൺ നമ്പർ പങ്കിടാമോ?",
                customerName);
    }

    /**
     * Confirmation after receiving phone number
     * Requests location sharing with instructions
     * 
     * @return Phone confirmation and location request in Malayalam
     */
    public String getPhoneConfirmationAndLocationRequest() {
        return "✅ *മികച്ചത്! നന്ദി!* 🙏\n\n" +
                "ഇപ്പോൾ, പുതിയ മീൻ നിങ്ങളുടെ വീട്ടിൽ എത്തിക്കാൻ, നിങ്ങളുടെ ലൊക്കേഷൻ പങ്കിടുക. 📍\n\n" +
                "📢 *എങ്ങനെ പങ്കിടാം:*\n" +
                "• അറ്റാച്ച്മെന്റ് ഐക്കൺ (📎) ടാപ്പ് ചെയ്യുക\n" +
                "• 'Location' തിരഞ്ഞെടുക്കുക\n" +
                "• നിങ്ങളുടെ നിലവിലെ ലൊക്കേഷൻ അയക്കുക\n\n" +
                "ഇത് ഞങ്ങളെ നിങ്ങളെ നന്നായി സേവിക്കാൻ സഹായിക്കുന്നു! 😊";
    }

    /**
     * Message when customer's location is within delivery zone
     * 
     * @param customerName Customer's name
     * @param distance     Distance from store in kilometers
     * @return Location accepted message in Malayalam
     */
    public String getLocationAccepted(String customerName, double distance) {
        return String.format("✨ *അതിശയകരമായ വാർത്ത, %s!* 🎉\n\n" +
                "✅ നിങ്ങൾ ഞങ്ങളുടെ ഡെലിവറി ഏരിയയിലാണ്! നിങ്ങളെ സേവിക്കാൻ ഞങ്ങൾ സന്തോഷിക്കുന്നു. 🙏\n" +
                "📍 ഞങ്ങളുടെ കടയിൽ നിന്നുള്ള ദൂരം: *%.2f km*\n\n" +
                "🐟 *ഞങ്ങളുടെ പുതിയ മീൻ കാണാൻ തയ്യാറാണോ?*\n" +
                "ഞങ്ങളുടെ പ്രീമിയം മീൻ തിരഞ്ഞെടുക്കാൻ *'start'* എന്ന് അയക്കുക!\n\n" +
                "💚 ഏറ്റവും പുതിയ ഗുണനിലവാരം, ശ്രദ്ധയോടെ ഡെലിവർ ചെയ്യുമെന്ന് ഞങ്ങൾ വാഗ്ദാനം ചെയ്യുന്നു!",
                customerName, distance);
    }

    /**
     * Message when customer's location is outside delivery zone
     * 
     * @param customerName Customer's name
     * @param distance     Distance from store in kilometers
     * @return Location rejected message in Malayalam
     */
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

    /**
     * Greeting for registered customers who send "Hi" or "Hello"
     * 
     * @param customerName Customer's name
     * @return Registered customer greeting in Malayalam
     */
    public String getRegisteredCustomerGreeting(String customerName) {
        return String.format("👋 *ഹലോ %s!* 😊\n\n" +
                "🐟 ഞങ്ങളുടെ പുതിയ മീൻ തിരഞ്ഞെടുക്കാൻ തയ്യാറാണോ?\n\n" +
                "ഇന്നത്തെ പ്രീമിയം മീൻ കാണാൻ *'start'* എന്ന് അയക്കുക! ✨",
                customerName);
    }

    // ========================================
    // CATEGORY 2: PRODUCT BROWSING MESSAGES (5 messages)
    // ========================================

    /**
     * Header for product catalog
     * 
     * @param customerName Customer's name
     * @return Product catalog header in Malayalam
     */
    public String getProductCatalogHeader(String customerName) {
        return String.format("🐟 *ഇന്ന് ലഭ്യമായ പുതിയ മീൻ, %s!* ✨\n\n" +
                "🌊 പ്രീമിയം ഗുണനിലവാരം, പുതുതായി പിടിച്ചത്!\n" +
                "🚚 നിങ്ങളുടെ വീട്ടിൽ എത്തിക്കുന്നു!\n\n" +
                "നിങ്ങളുടെ കാർട്ടിലേക്ക് ചേർക്കാൻ ഒരു മീൻ തിരഞ്ഞെടുക്കുക:",
                customerName);
    }

    /**
     * Message when no products are available
     * 
     * @param customerName Customer's name
     * @return No products available message in Malayalam
     */
    public String getNoProductsAvailable(String customerName) {
        return String.format("😔 *ക്ഷമിക്കണം, %s!*\n\n" +
                "ഈ നിമിഷം ഞങ്ങളുടെ പക്കൽ പുതിയ മീൻ ലഭ്യമല്ല. 🐟\n\n" +
                "🕒 ഞങ്ങളുടെ പുതിയ സ്റ്റോക്ക് ദിവസവും എത്തുന്നു!\n" +
                "അൽപ്പസമയത്തിനുള്ളിൽ ഞങ്ങളെ വീണ്ടും സമീപിക്കുക. ഏറ്റവും പുതിയ മീൻ നിങ്ങൾക്കായി തയ്യാറാക്കാം! ✨\n\n" +
                "നിങ്ങളുടെ ക്ഷമയ്ക്ക് നന്ദി! 🙏",
                customerName);
    }

    /**
     * Message after product selection, requesting quantity
     * 
     * @param customerName Customer's name
     * @param fishName     Name of selected fish
     * @param price        Price per kg
     * @return Product selected message in Malayalam
     */
    public String getProductSelectedQuantityRequest(String customerName, String fishName, double price) {
        return String.format("✅ *മികച്ച തിരഞ്ഞെടുപ്പ്, %s!* 🎉\n\n" +
                "🐟 നിങ്ങൾ തിരഞ്ഞെടുത്തത്: *%s*\n" +
                "💰 വില: *₹%.2f കിലോയ്ക്ക്*\n\n" +
                "⚖️ എത്ര കിലോഗ്രാം വേണം?\n" +
                "(ഉദാഹരണം: 2 അല്ലെങ്കിൽ 2.5)",
                customerName, fishName, price);
    }

    /**
     * Message when selected product is no longer available
     * 
     * @return Product unavailable message in Malayalam
     */
    public String getProductNoLongerAvailable() {
        return "😔 *ക്ഷമിക്കണം!*\n\n" +
                "ആ മീൻ ഇപ്പോൾ ലഭ്യമല്ല. 🐟\n\n" +
                "നിലവിലെ പുതിയ മീൻ കാണാൻ *'start'* എന്ന് അയക്കുക! ✨";
    }

    /**
     * Prompt to browse products
     * 
     * @param customerName Customer's name
     * @return Browse prompt in Malayalam
     */
    public String getPromptToBrowse(String customerName) {
        return String.format("👋 *ഹലോ %s!* 😊\n\n" +
                "🐟 ഞങ്ങളുടെ പുതിയ മീൻ തിരഞ്ഞെടുക്കാൻ തയ്യാറാണോ?\n\n" +
                "ഇന്നത്തെ പ്രീമിയം മീൻ കാണാൻ *'start'* എന്ന് അയക്കുക! ✨",
                customerName);
    }

    // ========================================
    // CATEGORY 3: CART MANAGEMENT MESSAGES (11 messages)
    // ========================================

    /**
     * Error message for zero or negative quantity
     * 
     * @return Invalid quantity message in Malayalam
     */
    public String getInvalidQuantityZeroOrNegative() {
        return "⚠️ *തെറ്റായ അളവ്!*\n\n" +
                "0-ൽ കൂടുതൽ അളവ് നൽകുക.\n" +
                "ഉദാഹരണം: 2 അല്ലെങ്കിൽ 2.5 😊";
    }

    /**
     * Error message for invalid quantity format
     * 
     * @return Invalid format message in Malayalam
     */
    public String getInvalidQuantityFormat() {
        return "⚠️ *ക്ഷമിക്കണം! തെറ്റായ ഇൻപുട്ട്*\n\n" +
                "അളവിനായി സാധുവായ നമ്പർ നൽകുക.\n" +
                "ഉദാഹരണങ്ങൾ: *2* അല്ലെങ്കിൽ *1.5* അല്ലെങ്കിൽ *3.5* 😊";
    }

    /**
     * Confirmation message after quantity update
     * 
     * @return Quantity updated message in Malayalam
     */
    public String getQuantityUpdated() {
        return "✅ *മികച്ചത്!* 🎉\n\n" +
                "അളവ് വിജയകരമായി അപ്ഡേറ്റ് ചെയ്തു! ✨";
    }

    /**
     * Confirmation message after adding item to cart
     * 
     * @param customerName Customer's name
     * @return Item added message in Malayalam
     */
    public String getItemAddedToCart(String customerName) {
        return String.format("✅ *കാർട്ടിലേക്ക് വിജയകരമായി ചേർത്തു!* 🎉\n\n" +
                "👍 മികച്ച തിരഞ്ഞെടുപ്പ്, %s!\n\n" +
                "അടുത്തതായി എന്താണ് ചെയ്യാൻ ആഗ്രഹിക്കുന്നത്?",
                customerName);
    }

    /**
     * Cart summary header
     * 
     * @param customerName Customer's name
     * @return Cart summary header in Malayalam
     */
    public String getCartSummaryHeader(String customerName) {
        return String.format("🛒 *നിങ്ങളുടെ കാർട്ട് സംഗ്രഹം, %s:*\n\n", customerName);
    }

    /**
     * Cart summary footer with total and payment info
     * 
     * @param total Total amount
     * @return Cart summary footer in Malayalam
     */
    public String getCartSummaryFooter(double total) {
        return String.format("\n💰 *ആകെ തുക: ₹%.2f*\n" +
                "💵 *പേയ്മെന്റ്: COD (ക്യാഷ് ഓൺ ഡെലിവറി)*\n\n" +
                "✅ നിങ്ങളുടെ ഓർഡർ സ്ഥിരീകരിക്കാൻ തയ്യാറാണോ?",
                total);
    }

    /**
     * Message when cart is empty
     * 
     * @return Empty cart message in Malayalam
     */
    public String getEmptyCart() {
        return "നിങ്ങളുടെ കാർട്ട് ശൂന്യമാണ്! മീൻ കാണാൻ 'start' എന്ന് അയക്കുക.";
    }

    /**
     * Edit order menu header
     * 
     * @param customerName Customer's name
     * @return Edit order menu in Malayalam
     */
    public String getEditOrderMenu(String customerName) {
        return String.format("📝 *നിങ്ങളുടെ ഓർഡർ എഡിറ്റ് ചെയ്യുക, %s!*\n\n" +
                "എന്താണ് മാറ്റാൻ ആഗ്രഹിക്കുന്നത്?",
                customerName);
    }

    /**
     * Remove product header for single item
     * 
     * @return Remove product header in Malayalam
     */
    public String getRemoveProductHeaderSingle() {
        return "🗑️ *ഉൽപ്പന്നം നീക്കം ചെയ്യുക*\n\n*നിലവിലെ കാർട്ട്:*\n";
    }

    /**
     * Remove product prompt
     * 
     * @return Remove product prompt in Malayalam
     */
    public String getRemoveProductPrompt() {
        return "ഈ ഇനം നീക്കം ചെയ്യണോ?";
    }

    /**
     * Edit quantity header
     * 
     * @param fishName Name of fish
     * @param quantity Current quantity
     * @return Edit quantity header in Malayalam
     */
    public String getEditQuantityHeader(String fishName, double quantity) {
        return String.format("*%s-ന്റെ നിലവിലെ അളവ്:* %.2f kg\n\n" +
                "പുതിയ അളവ് നൽകുക (കിലോയിൽ):",
                fishName, quantity);
    }

    // ========================================
    // CATEGORY 4: ORDER MANAGEMENT MESSAGES (5 messages)
    // ========================================

    /**
     * Order confirmation message
     * 
     * @param orderId Order ID
     * @param total   Total amount
     * @return Order confirmation in Malayalam
     */
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

    /**
     * Delivery assignment notification
     * 
     * @param deliveryPersonName  Delivery person's name
     * @param deliveryPersonPhone Delivery person's phone
     * @return Delivery assignment notification in Malayalam
     */
    public String getDeliveryAssignmentNotification(String deliveryPersonName, String deliveryPersonPhone) {
        return String.format("🎉 *ഓർഡർ സ്ഥിരീകരിച്ചു!* 🚀\n\n" +
                "നിങ്ങളുടെ ഓർഡർ ഞങ്ങളുടെ ഡെലിവറി വ്യക്തി ഏറ്റെടുത്തു!\n\n" +
                "👤 *ഡെലിവറി വ്യക്തി:* %s\n" +
                "📞 *WhatsApp:* %s\n\n" +
                "ഡെലിവറിക്കായി അവർ ഉടൻ നിങ്ങളെ ബന്ധപ്പെടും. 📦",
                deliveryPersonName, deliveryPersonPhone);
    }

    /**
     * Order cancelled message
     * 
     * @param customerName Customer's name
     * @return Order cancelled message in Malayalam
     */
    public String getOrderCancelled(String customerName) {
        return String.format("✅ *ഓർഡർ റദ്ദാക്കി, %s* 🙏\n\n" +
                "🗑️ നിങ്ങളുടെ കാർട്ട് ക്ലിയർ ചെയ്തു.\n\n" +
                "🐟 നിങ്ങൾ തയ്യാറാകുമ്പോൾ, ഞങ്ങളുടെ പുതിയ മീൻ വീണ്ടും കാണാൻ *'start'* എന്ന് അയക്കുക! ✨",
                customerName);
    }

    /**
     * Empty cart during order message
     * 
     * @return Empty cart message in Malayalam
     */
    public String getEmptyCartDuringOrder() {
        return "നിങ്ങളുടെ കാർട്ട് ശൂന്യമാണ്!";
    }

    /**
     * All products removed message
     * 
     * @param customerName Customer's name
     * @return All products removed message in Malayalam
     */
    public String getAllProductsRemoved(String customerName) {
        return String.format("✅ *എല്ലാ ഉൽപ്പന്നങ്ങളും നീക്കം ചെയ്തു!* 🙏\n\n" +
                "🛒 നിങ്ങളുടെ കാർട്ട് ഇപ്പോൾ ശൂന്യമാണ്, %s.\n\n" +
                "🐟 ഇന്ന് ലഭ്യമായ ഞങ്ങളുടെ പുതിയ മീൻ ഇതാ:",
                customerName);
    }

    // ========================================
    // CATEGORY 5: PRODUCT REMOVAL MESSAGES (2 messages)
    // ========================================

    /**
     * Product removed successfully message
     * 
     * @return Product removed message in Malayalam
     */
    public String getProductRemovedSuccessfully() {
        return "✅ *ഉൽപ്പന്നം വിജയകരമായി നീക്കം ചെയ്തു!* 👍\n\n" +
                "ഇനം നിങ്ങളുടെ കാർട്ടിൽ നിന്ന് നീക്കം ചെയ്തു. ✨";
    }

    /**
     * Cart empty after removal message
     * 
     * @return Cart empty message in Malayalam
     */
    public String getCartEmptyAfterRemoval() {
        return "നിങ്ങളുടെ കാർട്ട് ഇപ്പോൾ ശൂന്യമാണ്. ലഭ്യമായ ഞങ്ങളുടെ ഉൽപ്പന്നങ്ങൾ ഇതാ:";
    }

    // ========================================
    // CATEGORY 6: ERROR/FALLBACK MESSAGES (3 messages)
    // ========================================

    /**
     * Unrecognized command message
     * 
     * @return Unrecognized command message in Malayalam
     */
    public String getUnrecognizedCommand() {
        return "എനിക്ക് അത് മനസ്സിലായില്ല. ഞങ്ങളുടെ മീൻ തിരഞ്ഞെടുക്കാൻ 'start' എന്ന് അയക്കുക.";
    }

    /**
     * Order placement failure message
     * 
     * @return Order placement failure message in Malayalam
     */
    public String getOrderPlacementFailure() {
        return "ക്ഷമിക്കണം, നിങ്ങളുടെ ഓർഡർ നൽകുന്നതിൽ ഒരു പ്രശ്നമുണ്ടായി. ദയവായി വീണ്ടും ശ്രമിക്കുക.";
    }

    /**
     * Remove products prompt
     * 
     * @return Remove products prompt in Malayalam
     */
    public String getRemoveProductsPrompt() {
        return "🗑️ *ഉൽപ്പന്നങ്ങൾ നീക്കം ചെയ്യുക*\n\n" +
                "ഒരു ഓപ്ഷൻ തിരഞ്ഞെടുക്കുക:";
    }

    // ========================================
    // CATEGORY 7: EXECUTIVE WELCOME MESSAGE (1 message)
    // ========================================

    /**
     * Welcome message for customer added by executive
     * 
     * @param customerName   Customer's name
     * @param customerPhone  Customer's phone
     * @param executiveName  Executive's name
     * @param executivePhone Executive's phone
     * @return Executive welcome message in Malayalam
     */
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
    // CATEGORY 8: BUTTON LABELS (Malayalam Translations)
    // ========================================

    /**
     * Button label: Add More Fish
     * 
     * @return Button label in Malayalam
     */
    public String getButtonAddMoreFish() {
        return "കൂടുതൽ മീൻ ചേർക്കുക";
    }

    /**
     * Button label: Checkout
     * 
     * @return Button label in Malayalam
     */
    public String getButtonCheckout() {
        return "ചെക്ക്ഔട്ട്";
    }

    /**
     * Button label: Confirm Order
     * 
     * @return Button label in Malayalam
     */
    public String getButtonConfirmOrder() {
        return "ഓർഡർ സ്ഥിരീകരിക്കുക";
    }

    /**
     * Button label: Edit Order
     * 
     * @return Button label in Malayalam
     */
    public String getButtonEditOrder() {
        return "ഓർഡർ എഡിറ്റ് ചെയ്യുക";
    }

    /**
     * Button label: Cancel Order
     * 
     * @return Button label in Malayalam
     */
    public String getButtonCancelOrder() {
        return "ഓർഡർ റദ്ദാക്കുക";
    }

    /**
     * Button label: Edit Product
     * 
     * @return Button label in Malayalam
     */
    public String getButtonEditProduct() {
        return "ഉൽപ്പന്നം എഡിറ്റ് ചെയ്യുക";
    }

    /**
     * Button label: Edit Quantity
     * 
     * @return Button label in Malayalam
     */
    public String getButtonEditQuantity() {
        return "അളവ് എഡിറ്റ് ചെയ്യുക";
    }

    /**
     * Button label: Back to Checkout
     * 
     * @return Button label in Malayalam
     */
    public String getButtonBackToCheckout() {
        return "ചെക്ക്ഔട്ടിലേക്ക് മടങ്ങുക";
    }

    /**
     * Button label: Remove All
     * 
     * @return Button label in Malayalam
     */
    public String getButtonRemoveAll() {
        return "എല്ലാം നീക്കം ചെയ്യുക";
    }

    /**
     * Button label: Remove [Product]
     * 
     * @param productName Product name
     * @return Button label in Malayalam
     */
    public String getButtonRemoveProduct(String productName) {
        return String.format("%s നീക്കം ചെയ്യുക", productName);
    }

    /**
     * Button label: View Fish (for interactive list button)
     * 
     * @return Button label in Malayalam
     */
    public String getButtonViewFish() {
        return "മീൻ കാണുക";
    }

    /**
     * Section title: Available Fish (for interactive list section)
     * 
     * @return Section title in Malayalam
     */
    public String getSectionTitleAvailableFish() {
        return "ലഭ്യമായ മീൻ";
    }

    /**
     * Button label: Accept Order (for delivery person)
     * 
     * @return Button label in Malayalam
     */
    public String getButtonAcceptOrder() {
        return "✅ ഓർഡർ സ്വീകരിക്കുക";
    }
}
