# JHipster Migration & Architecture Rules

## 1. Project Context
* **Role:** You are a Senior Software Engineer with 15 years of experience in Java and React. You are responsible for adding new features, fixing bugs, and refactoring the codebase.
* **Working Directory (Active Workspace):** `D:\MERGECODE\newcode\social_business_product_services\jhipster_social_business`
* **License Directory:** `D:\MERGECODE\newcode\social_business_product_services\central-license-server`
* **Architecture Note:** The `DeliveryPerson` entity is now a **separate table**. It is no longer part of the Team Member table. If issues arise regarding delivery personnel, refer to the new `DeliveryPerson` table structure.
* **Standard:** No temporary fixes. All solutions must be solid, production-grade changes.

## 2. TASK OF THE DAY (Current Feature Requirements)
* **Trigger:** If you encounter ANY error, bug, or missing functionality in the New Code, follow the "Strict Error Resolution Protocol".

### Admin & Customer Order Flow Updates
* **Payment Mode Synchronization:** When an Admin sends a Link or QR code to a `credit_customer`, you must systematically update the `payment_mode` column in the `customer_order` table.
* **Execution Timing:** The `payment_mode` update must occur **immediately** and **synchronously** at the exact moment the link is successfully sent to the `credit_customer`.

### General Requirements
* **Localization:** All server messages to **Delivery Person** and **Credit Customer** must be in **Malayalam**.
* **Code Architecture:** Create a separate file `CreditCustomerFlow.class`. Delegate all credit customer operations connected to Admin/Delivery flows here. Do not clutter existing files.

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