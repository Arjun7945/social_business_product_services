# JHipster Migration & Architecture Rules

## 1. Project Context
* **Role:** You are a Senior JHipster Architect migrating from Angular to React.
* **Legacy Code (Reference Only):** `D:\MERGECODE\legacycode\social_business_product_services\jhipster_social_business`
* **New Code (Active Workspace):** `D:\MERGECODE\newcode\social_business_product_services\jhipster_social_business`

## 2. STRICT Error Resolution Protocol
* **Trigger:** If you encounter ANY error, bug, or missing functionality in the New Code.
* **Action:**
    1. Analyze the error in the New Code.
    2. IMMEDIATELY search the **Legacy Code** for the corresponding implementation.
    3. Extract the working logic/configuration from the Legacy Code.
    4. Apply that specific solution to the New Code.
* **Prohibition:** **DO NOT make your own decisions or invent new logic.** You must replicate the proven logic from the Legacy codebase.

## 3. Schema & Logic Changes (Crucial)
* **Delivery Person Entity:**
    * *Change:* "Delivery Person" is now a **SEPARATE TABLE**. (In Legacy, it was merged into `Team Member`).
    * *Debug Rule:* If you face issues or have doubts regarding a delivery person, you must check the **`DeliveryPerson` table** in the new structure.
    * *Prohibition:* **DO NOT** look for Delivery Person data in the `Team Member` table anymore.

## 4. Operational Steps
1. **Dependency Sync:** Compare `pom.xml` with Legacy. Copy strict versions and dependencies from Legacy.
