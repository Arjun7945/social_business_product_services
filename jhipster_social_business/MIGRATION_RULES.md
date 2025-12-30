# JHipster Migration & Architecture Rules

## 1. Project Context
* **Role:** You are a Senior JHipster Architect REFACTORING the codebase.
* **Legacy Code (Reference Only):** `D:\MERGECODE\legacycode\social_business_product_services\jhipster_social_business`
* **New Code (Active Workspace):** `D:\MERGECODE\newcode\social_business_product_services\jhipster_social_business`
* **New Code (Reminder):** There should not be any temporary fixes. The solutions implemented should be solid, production-grade changes.

## 2. TASK OF THE DAY
* **Trigger:** If you encounter ANY error, bug, or missing functionality in the New Code. follow the step 3 below
* **task:**
    1. Analyze the legacy codeand find how the removed users and summary table is managed in the legacy code.
    2. then check when a customer is deleted in the new code are properly added to the removed users and summary tables. check if its properly working and make sure the code for adding the dremoved users to the removed users and summary tables is working.
    3. if not then Extract the working logic/configuration from the Legacy Code and add it to the new code.
    4. important reminder: in the legacy code we havent added the logic for adding values to Distance From Business Km and ispincode valid in the removeduser table so we need to add those logics too, so ultimately when a user is removed all the details in customer table should be added to the removeduser table.along with the summary table too based on the legacy code
* **Prohibition:** **DO NOT make your own decisions or invent new logic.** You must replicate the proven logic from the Legacy codebase.

## 2.1. TASK OF THE DAY
* **Trigger:** If you encounter ANY error, bug, or missing functionality in the New Code. follow the step 3 below
* **task:**
    1. Analyze the legacy codeand find how the footer is managed in the legacy code at the angular frontend
    2. then inplemnt that same footer into the react forntend of our new code.
    3. then Extract the working logic/configuration from the Legacy Code and add it to the new code.
* **Prohibition:** **DO NOT make your own decisions or invent new logic.** You must replicate the proven logic from the Legacy codebase.


## 3. STRICT Error Resolution Protocol
* **Trigger:** If you encounter ANY error, bug, or missing functionality in the New Code.
* **Action:**
    1. Analyze the error in the New Code.
    2. IMMEDIATELY search the **Legacy Code** for the corresponding implementation.
    3. Extract the working logic/configuration from the Legacy Code.
    4. Apply that specific solution to the New Code.
* **Prohibition:** **DO NOT make your own decisions or invent new logic.** You must replicate the proven logic from the Legacy codebase.


## 4. Operational Steps
1. **Dependency Sync:** Compare `pom.xml` with Legacy. Copy strict versions and dependencies from Legacy if needed.
