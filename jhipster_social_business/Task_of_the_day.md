# JHipster Migration & Architecture Rules

## 1. Project Context
* **Role:** You are a Senior Software Engineer with 15 years of experience in Java and React. You are responsible for adding new features, fixing bugs, and refactoring the codebase.
* **Working Directory (Active Workspace):** `D:\MERGECODE\newcode\social_business_product_services\jhipster_social_business`
* **License Directory:** `D:\MERGECODE\newcode\social_business_product_services\central-license-server`
* **Standard:** No temporary fixes. All solutions must be solid, production-grade changes.

## 2. TASK OF THE DAY (Admin Flow - Delete Delivery Person)
* **Context:** Currently, selecting "Delete Delivery Person" incorrectly redirects to the main menu without any logic. This flow must be implemented as follows:

### Step 1: Selection List
* **Trigger:** Admin selects the "Delete Delivery Person" option.
* **Action:** Send a **List View Message** (Interactive List) containing all registered Delivery Persons.
* **Button/Row Format:**
    * **Title:** `{Delivery Person Name}`
    * **Description:** `ID: {id} | Zone: {zone_name} | Pending Orders: {count}`

### Step 2: Confirmation & Details
* **Trigger:** Admin selects a specific Delivery Person from the list.
* **Action:** Display a detailed summary message.
* **Content to Display:**
    * **ID:** `{id}`
    * **Name:** `{name}`
    * **Zone:** `{zone}`
    * **WhatsApp Number:** `{wanumber}`
    * **Pending Orders:** List details from the `chosen_orders` column (if any).
* **Interactive Buttons:**
    1.  **Confirm and Delete** (Proceeds with removal logic).
    2.  **Don't Delete** (Cancels operation).

## 3. General Requirements
* **Localization:** All server messages sent to users (Admin, Delivery Person, Customers) must be in **Malayalam**.
* **Code Architecture:** Keep logic modular. Do not clutter existing files; create dedicated service methods for this removal flow if necessary.

## 4. STRICT RULES (General)
1. **NO TEMPORARY FIXES:** Every line of code must be production-ready.
2. **NO BOILERPLATE CODE:** Keep the codebase concise; use Lombok or utility methods where applicable.
3. **NO COUPLED LOGIC:** Ensure modularity and separation of concerns.
4. **NO REDUNDANT CODE:** DRY (Don't Repeat Yourself) principle must be enforced.
5. **SENIOR STANDARDS:** Maintain the architectural integrity of a Senior Software Engineer.

## 4. TECHNICAL STANDARDS (Java/Spring & React)
* **Java/Spring Boot:**
    1. **Logging:** NEVER use `System.out.println`. Use `SLF4J` loggers.
    2. **Exception Handling:** No empty catch blocks. Throw custom exceptions or handle gracefully.
    3. **Database:** Ensure JPA queries are optimized (avoid N+1 problems). Use DTOs, never expose Entities directly in REST APIs.
* **React:**
    1. **Modern Syntax:** Use Functional Components and Hooks exclusively (no Class components).
    2. **State Management:** Keep state as local as possible; use Context or Redux only when necessary.
    3. **Clean UI:** Ensure no hardcoded strings; use localization/constants.

## 5. STRICT Error Resolution Protocol
* **Trigger:** If you encounter ANY error, bug, or missing functionality in the New Code.
* **Action:**
    1. Analyze the error immediately.
    2. Fix the issue completely before moving to the next step.
    3. **Do not suppress errors** with `try-catch` without proper logging/handling.
    4. **Legacy Code Analysis:** Always check the **Legacy Files** first if errors are faced. Retrieve solution logic from legacy code but implement it using modern standards.

## 6. TESTING MANDATE
1. **Unit Tests:** New logic must have accompanying unit tests (JUnit/Mockito for Backend, Jest/Testing Library for Frontend).
2. **Regression:** Ensure new changes do not break existing build pipelines or tests.

## 7. QUALITY ASSURANCE & RATING
1. **Rate the Code (1-10):** Evaluate the changes based on the rules above.
2. **Gap Analysis:** If the rating is below 10, provide a specific list of fixes required to reach a perfect score.

---
**IMPORTANT:** Make sure the rules mentioned in the `@RULES.md` are properly followed while refactoring.