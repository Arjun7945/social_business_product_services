package com.aps.config;

/**
 * Constants used in the WhatsApp flow logic.
 */
public final class FlowConstants {

    private FlowConstants() {
    }

    // Button IDs
    public static final String BTN_CONTINUE_SHOPPING = "CONTINUE_SHOPPING";
    public static final String BTN_CHECKOUT = "CHECKOUT";
    public static final String BTN_CONFIRM_ORDER = "CONFIRM_ORDER";
    public static final String BTN_CANCEL_ORDER = "CANCEL_ORDER";
    public static final String BTN_EDIT_ORDER = "EDIT_ORDER";
    public static final String BTN_EDIT_PRODUCT = "EDIT_PRODUCT";
    public static final String BTN_EDIT_QUANTITY = "EDIT_QUANTITY";
    public static final String BTN_BACK_TO_CHECKOUT = "BACK_TO_CHECKOUT";
    public static final String BTN_REMOVE_ALL_ITEMS = "REMOVE_ALL_ITEMS";

    // Accounts Team Buttons
    public static final String BTN_ACCOUNTS_TODAYS_ORDERS = "ACC_TODAYS_ORDERS";
    public static final String BTN_ACCOUNTS_COMPLETED_ORDERS = "ACC_COMPLETED_ORDERS";
    public static final String BTN_ACCOUNTS_UNPAID_ORDERS = "ACC_UNPAID_ORDERS";
    public static final String BTN_ACCOUNTS_CREDIT_REPORT = "ACC_CREDIT_REPORT";

    // Accounts Report Format
    public static final String BTN_FMT_PDF = "FMT_PDF";
    public static final String BTN_FMT_EXCEL = "FMT_EXCEL";

    // Prefix for dynamic IDs
    public static final String PREFIX_REMOVE_ITEM = "REMOVE_ITEM_";
    public static final String PREFIX_EDIT_QTY = "EDIT_QTY_";
    public static final String PREFIX_SELECT_PRODUCT = "SELECT_";
    public static final String PREFIX_FISH_PRODUCT = "FISH_";
    public static final String PREFIX_DELIVERY_TAKE = "DELIVERY_TAKE_";
    public static final String PREFIX_SHIPPED = "shipped_";
    public static final String PREFIX_DELIVERED = "delivered_";

    // Payment Mode Prefixes
    public static final String PREFIX_PAY_COD = "PAY_COD_";
    public static final String PREFIX_PAY_QR = "PAY_QR_";
    public static final String PREFIX_PAY_LINK = "PAY_LINK_";

    // Delivery Flow Menus
    public static final String DELIVERY_MENU_ORDER_TAKEN = "DELIVERY_MENU_ORDER_TAKEN";
    public static final String DELIVERY_MENU_PROFILE = "DELIVERY_MENU_PROFILE";

    // Delivery Profile Flow
    public static final String DELIVERY_PROFILE_UPDATE_STATUS = "DELIVERY_PROFILE_UPDATE_STATUS";
    public static final String DELIVERY_PROFILE_UPDATE_ZONE = "DELIVERY_PROFILE_UPDATE_ZONE";
    public static final String DELIVERY_PROFILE_MENU = "DELIVERY_PROFILE_MENU";

    // Prefixes
    public static final String PREFIX_DELIVERY_DETAILS = "DELIVERY_DETAILS_";
    public static final String PREFIX_UPDATE_STATUS = "UPDATE_STATUS_";
    public static final String PREFIX_UPDATE_ZONE = "UPDATE_ZONE_";

    // Fallback Images
    public static final String WHATSAPP_LOGO_URL = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6b/WhatsApp.svg/1200px-WhatsApp.svg.png";

    // Commands
    public static final String CMD_DONE = "DONE";
    public static final String CMD_SKIP = "SKIP";
    public static final String CMD_YES = "YES";
    public static final String CMD_NO = "NO";
    public static final String CMD_Y = "Y";
    public static final String CMD_N = "N";
    public static final String CMD_HI = "HI";
    public static final String CMD_HELLO = "HELLO";
    public static final String CMD_ABORT = "ABORT";
}
