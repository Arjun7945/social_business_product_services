# JHipster Migration & Architecture Rules

## 1. Project Context
* **Role:** You are a Senior JHipster Architect REFACTORING the codebase.
* **Legacy Code (Reference Only):** `D:\MERGECODE\legacycode\social_business_product_services\jhipster_social_business`
* **ReactLegacy (Reference Only):**
`D:\MERGEDCODE\CUSTOMER_REACT_PAGE_CODE\social_business_product_services\jhipster_social_business\wts-product-service-app`
* **New Code (Active Workspace):** `D:\MERGECODE\newcode\social_business_product_services\jhipster_social_business`
* **New Code (Reminder):** There should not be any temporary fixes. The solutions implemented should be solid, production-grade changes.


## 2. TASK OF THE DAY
* **Trigger:** If you encounter ANY error, bug, or missing functionality in the New Code. follow the step 3 below
* **task:**
    1. Analyze the ReactLegacy code and find how the login page,otp page,success or fail,components,landing page, track order page, etc are managed in the ReactLegacy code.
    2. then inplemnt that same logic into the latest react forntend of our new code.
    3. then all these mentioned pages should comes under a seperate url /ourCustomers.
    4. take all the neccesary informations from the ReactLegacy code and implement it in the new code.
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
