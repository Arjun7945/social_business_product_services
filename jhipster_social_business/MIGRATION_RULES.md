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

## 2.1 TASK OF THE DAY
* **Trigger:** If you encounter ANY error, bug, or missing functionality in the New Code. follow the step 3 below
* **task:**
    1. Analyze the image uploaded and based on the image and requirements, implement the logic in the new code.
    2. requirements: so based on the image you can see that how track order page is designed in it nad i need the same ui to be implemented inmy new code ourcustomer section.
    2.1 you can skip the colors shown in the image and use current suitable coloes in the ourcustomer section
    2.2 i need animation on the status chart shown in the image where the order recieved to delivered should have animation each time the page is loaded and based on the status of the product the animation should be shown.
    2.3 estimated time can be removed while updating the track page.
    2.4 the view on map should be replaced with view purchase order.
    2.5 order reieved will be replaced with order placed.
    2.6 order confirmed should be replaced with order onway
    2.7 order processed will be replaced with payment pending
    2.8 out for delivery will be replaced with payment successfully recieved or payment failed (based on the payment status)
    2.9 if the payment was success then show order deliverd successfully.
    3. also the time should also be shown as description of these status.
    3.1 time should be calculated based on how many time is taken to move from one status to another.
    4. once the status had reached the payment pending then we will show the success or failure based on the payment status.and once the payment is success then we will show the order delivered successfully.
    5. the referrence image is attached along with this .md file
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
