# JHipster Migration & Architecture Rules

## 1. Project Context
* **Role:** You are a Senior Software Engineer with 15 years of experience in Java and React. You are responsible for adding new features, fixing bugs, and refactoring the codebase.
* **Working Directory (Active Workspace):** `D:\MERGECODE\newcode\social_business_product_services\jhipster_social_business`
* **License Directory:** `D:\MERGECODE\newcode\social_business_product_services\central-license-server`
* **Standard:** No temporary fixes. All solutions must be solid, production-grade changes.

## 2. TASK OF THE DAY (Feature Requirements & Logic)
* **Trigger:** If you encounter ANY error, bug, or missing functionality in the New Code, follow the "Strict Error Resolution Protocol".

### Admin Credit Request Flow (Communication)
* **Customer Notification:** When the Admin processes a credit request (initiated by the Delivery Person upon declined payment), a separate notification must be sent directly to the **Customer** (in addition to the existing Delivery Person reply).
* **Scenarios:** Handle all three Admin decision buttons:
    1.  **Always Allow**
    2.  **Allow for this Order**
    3.  **Decline Request**
* **Message Content:**
    * Must be in **Malayalam**.
    * Tone: Humble, respectful, and polite.
    * Variables: Must include **Customer Name** and **Order ID**.
    * Context: Clearly inform the customer of the Admin's decision regarding their credit request.

### Delivery Person Flow (Link/QR & Removal)
* **Link/QR Message Update:**
    * Update the server-to-customer message sent when a Delivery Person triggers "Send Link" or "Generate QR".
    * **Content:** Make the Malayalam text more attractive, meaningful, and polite.
    * **Variables:** Strictly include the **Order ID** in the message.
* **Removal Safeguard (Backend & Frontend):**
    * **Validation:** Before removing a Delivery Person (moving to removed/history tables), check the `chosen_orders` column.
    * **Condition:** If the Delivery Person has pending orders, **block the removal**.
    * **Error Message:** Return a detailed message stating: "Delivery Person {Name} has {Count} pending orders to complete. Please complete or reassign."
    * **Details to Show:** Display the specific **Order IDs** and the **Total Amount** for each pending order.

### Executive Flow (Data Integrity)
* **Fix Missing Data:** When an Executive adds a customer, ensure the following columns are populated (currently empty):
    1.  **Address:** Reuse existing logic/methods available in the codebase for address handling.
    2.  **Zone:** Assign a default zone fetched from the `delivery_zone` table.
    3.  **Added By:** Debug and ensure the `added_by` column is correctly recording the Executive's ID/Name.

### Client-Side Notifications
* **Restore User Alert:** When a user is successfully restored, trigger a standard JHipster UI notification.
* **Content:** The message must display the **Restored User ID** and the **Table Name** to which they were restored.

## 3. General Requirements
* **Localization:** All server messages to **Delivery Person**, **Credit Customer**, and **Regular Customer** must be in **Malayalam**.

## 4. STRICT Error Resolution Protocol
* **Trigger:** If you encounter ANY error, bug, or missing functionality in the New Code.
* **Action:**
    1.  **Legacy Code Analysis:** Always check the **Legacy Files** first if errors or issues are faced. Analyze the error in the new code, but retrieve the solution logic from the legacy code.
    2.  **No Assumptions:** Do not make your own decisions regarding business logic; strictly follow the legacy implementation.
    3.  **Fix:** Apply the fix immediately to the new code.
    4.  **Testing:** Run all test cases. Ensure all pass and no test cases are pending.
    5.  **New Features:** Ensure valid test cases are added for any new features implemented.

---
**IMPORTANT:** Make sure the rules mentioned in the `@RULES.md` are properly followed while refactoring.