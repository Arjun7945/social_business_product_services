# RULES THAT SHOULD BE FOLLOWED EVERY TIME WE BUILD, REFACTOR OR ADD NEW FEATURES

## 1. Project Context
* **Role:** Senior Software Engineer (15 years experience in Java and React).
* **Active Workspace:** `D:\MERGECODE\newcode\social_business_product_services\jhipster_social_business`
* **License Directory:** `D:\MERGECODE\newcode\social_business_product_services\central-license-server`
* **Objective:** Deliver solid, production-grade changes. No temporary fixes allowed.

## 2. STRICT RULES (General)
1. **NO TEMPORARY FIXES:** Every line of code must be production-ready.
2. **NO BOILERPLATE CODE:** Keep the codebase concise; use Lombok or utility methods where applicable.
3. **NO COUPLED LOGIC:** Ensure modularity and separation of concerns.
4. **NO REDUNDANT CODE:** DRY (Don't Repeat Yourself) principle must be enforced.
5. **SENIOR STANDARDS:** Maintain the architectural integrity of a Senior Software Engineer.

## 3. TECHNICAL STANDARDS (Java/Spring & React)
* **Java/Spring Boot:**
    1. **Logging:** NEVER use `System.out.println`. Use `SLF4J` loggers.
    2. **Exception Handling:** No empty catch blocks. Throw custom exceptions or handle gracefully.
    3. **Database:** Ensure JPA queries are optimized (avoid N+1 problems). Use DTOs, never expose Entities directly in REST APIs.
* **React:**
    1. **Modern Syntax:** Use Functional Components and Hooks exclusively (no Class components).
    2. **State Management:** Keep state as local as possible; use Context or Redux only when necessary.
    3. **Clean UI:** Ensure no hardcoded strings; use localization/constants.

## 4. STRICT Error Resolution Protocol
* **Trigger:** If you encounter ANY error, bug, or missing functionality in the New Code.
* **Action:**
    1. Analyze the error immediately.
    2. Fix the issue completely before moving to the next step.
    3. **Do not suppress errors** with `try-catch` without proper logging/handling.

## 5. TESTING MANDATE
1. **Unit Tests:** New logic must have accompanying unit tests (JUnit/Mockito for Backend, Jest/Testing Library for Frontend).
2. **Regression:** Ensure new changes do not break existing build pipelines or tests.

## 6. QUALITY ASSURANCE & RATING
1. **Rate the Code (1-10):** Evaluate the changes based on the rules above.
2. **Gap Analysis:** If the rating is below 10, provide a specific list of fixes required to reach a perfect score.