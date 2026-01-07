# JHipster Migration & Architecture Rules

## 1. Project Context
* **Role:** You are a Senior Software Engineer with 15 years of experience in Java and React. You are responsible for adding new features, fixing bugs, and refactoring the codebase.
* **Working Directory (Active Workspace):** `D:\MERGECODE\newcode\social_business_product_services\jhipster_social_business`
* **License Directory:** `D:\MERGECODE\newcode\social_business_product_services\central-license-server`
* **Architecture Note:** The `DeliveryPerson` entity is now a **separate table**. It is no longer part of the Team Member table. If issues arise regarding delivery personnel, refer to the new `DeliveryPerson` table structure.
* **Standard:** No temporary fixes. All solutions must be solid, production-grade changes.

## 2. TASK OF THE DAY
* **Trigger:** If you encounter ANY error, bug, or missing functionality in the New Code, follow the "Strict Error Resolution Protocol" (Section 3).

### A. Role & Status Configuration
1.  **New Role:** Add a new role `CREDIT_CUSTOMER` to `UserRole.java`.
    * This role functions identically to `CUSTOMER` but allows purchasing products without immediate payment (Pay Later).
2.  **New Order Status:** Add `ON_CREDIT_PURCHASE` to the `OrderStatus.java` enum.

### B. Delivery Person Flow Enhancements
1.  **Payment Mode Selection:**
    * In `getPaymentModeSelectionHeader`, change the interface to a **List View**.
    * Add a new button option: `'Payment Resisted'` (Description: "The customer is not paying right now").
2.  **"Payment Resisted" Logic:**
    * If selected, send a message to all users with role **ADMIN**:
        > "Hello {admin_name}, this message is from {delivery_person_name}. I am delivering an order to {customer_name} and they are not willing to pay the amount now and are asking for credit purchase. {customer_name} is a {customer_role}. Total amount: {total_amount}. Order details: {order_details}."
    * Attach 3 buttons to this Admin message:
        1.  `Allow credit for this purchase`
        2.  `Always Grant credit purchase`
        3.  `Deny credit purchase`

### C. Admin Decision Logic (Response to Delivery Person)
1.  **If Admin clicks 'Always Grant credit purchase':**
    * Update Customer Order status to `ON_CREDIT_PURCHASE`.
    * Update Customer Role to `CREDIT_CUSTOMER`.
    * Notify Delivery Person: "Hello {dp_name}, our customer {customer_name} has been upgraded to credit customer privileges. You can deliver order {order_id} and return to warehouse or continue."
2.  **If Admin clicks 'Allow credit for this purchase':**
    * Update Customer Order status to `ON_CREDIT_PURCHASE`.
    * Notify Delivery Person: "Hello {dp_name}, customer {customer_name} has been approved for a credit purchase for order {order_id}. Hand over the product and return/continue."
3.  **If Admin clicks 'Deny credit purchase':**
    * **Do not** update the database.
    * Notify Delivery Person: "Hello {dp_name}, order {order_id} for {customer_name} has been denied for credit. Please collect the amount."

### D. Admin Dashboard - Credit Customer Management
1.  **Main Menu:** Add a new list button `'Credit Customer'` (Description: "Customer with privilege on purchases").
2.  **Sub-Menu:** If clicked, show options:
    * `'Credit Customer Orders'`
    * `'CRUD of Credit Customers'`
3.  **Credit Customer Orders (Pay Later Flow):**
    * Display list of orders where status is `ON_CREDIT_PURCHASE`.
    * Button Description: "{Customer Name} - {WhatsApp Number}".
    * If list is empty, show: "No credit customer orders found till now".
    * **Order Action:** If an order is selected, show full order details (ID, Name, Amount, Status, Products) with 3 buttons:
        1.  `Send payment link`: Generate Razorpay link (existing logic) and WhatsApp it to the customer: "Hi {name}, payment link for order {id} from {admin_name}: {link}".
        2.  `COD`: Mark status directly as `ORDER_DELIVERED_SUCCESSFULLY` (use existing COD logic).
        3.  `Go back to menu`.
4.  **CRUD of Credit Customers:**
    * **Reuse** the existing Customer CRUD. Do not create a new one.
    * **Filter Logic:**
        * "Show all Customers" must **only** show role `CUSTOMER`.
        * "Show all Credit Customers" must **only** show role `CREDIT_CUSTOMER`.

### E. Admin Dashboard - Customer Update Feature
1.  **Menu Addition:** In the Customer Management menu (Add, Show, Delete), add a new option: `'Update'`.
2.  **Search & Validation Step:**
    * When 'Update' is selected, prompt the Admin: "Please enter the ID or Name of the customer you wish to update."
    * **Validation:** Check the database for the provided ID or Name.
    * **Error Handling:** If the ID/Name is invalid or the customer does not exist, send a specific validation error message to the Admin (e.g., "Customer not found, please try again") and halt the flow until valid input is received.
3.  **Update Sub-Menu (List View):**
    * Once a valid customer is identified, show the update options.
    * **Format:** Use a **List View Button** message (since there are >3 options).
    * **Options:** Update Name, WhatsApp Number, Number, Location, Role, Zone.
4.  **Specific Field Logic:**
    * **Name/Number/Location:** Ask for input -> Update DB.
    * **WhatsApp Number:** Ask for input -> Update DB -> Send existing "Welcome" message logic to new number.
    * **Zone:** Show available zones as List View -> Select -> Update DB.
    * **Role:** Show buttons: `CUSTOMER` and `CREDIT_CUSTOMER`. Toggle role based on selection.

### F. Localization
* **Requirement:** All server messages sent to the **Delivery Person** and **Credit Customer** must be in **Malayalam**.

### G. Code Organization & Refactoring
* **Separation of Concerns:** Do **not** write the new `CreditCustomer` logic directly inside the existing Admin or Delivery flow files.
* **Action:** Create a separate class/file named `CreditCustomerFlow`.
* **Implementation:** Delegate all credit customer operations that connect to Admin or Delivery flows to this new file. This is mandatory to reduce code bloat and maintain maintainability.

## 3. STRICT Error Resolution Protocol
* **Trigger:** If you encounter ANY error, bug, or missing functionality in the New Code.
* **Action:**
    1.  **Legacy Code Analysis:** Always check the **Legacy Files** first if errors or issues are faced. Analyze the error in the new code, but retrieve the solution logic from the legacy code.
    2.  **No Assumptions:** Do not make your own decisions regarding business logic; strictly follow the legacy implementation.
    3.  **Fix:** Apply the fix immediately to the new code.
    4.  **Testing:** Run all test cases. Ensure all pass and no test cases are pending.
    5.  **New Features:** Ensure valid test cases are added for any new features implemented.

---
**IMPORTANT:** Make sure the rules mentioned in the `@RULES.md` are properly followed while refactoring.