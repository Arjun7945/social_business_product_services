# Architectural Hardening: Microservices Refactoring Report

**Engineering Journal** | **Date**: December 22, 2025

## 📋 Abstract

This report documents the targeted engineering effort to elevate the application from a standard Monolith to a **Production-Grade, Microservice-Ready Architecture**. We conducted a deep-dive audit against 20-year Distributed Systems standards, identifying invisible risks (Concurrency, Resilience, Scalability) and implementing robust patterns to mitigate them.

---

## 1. The Challenge Landscape

In a distributed cloud environment, applications face stressors that local monoliths do not. Our initial expert analysis revealed the following "Silent Killers":

1.  **Race Conditions (The "Lost Update" Problem)**:
    - _Risk_: Two admins editing the same Order or Inventory Item simultaneously would silently overwrite each other's work.
2.  **Availability Risks (DoS & OOM)**:
    - _Risk_: Public APIs were unprotected (No Rate Limiting), making the system vulnerable to scraper bots.
    - _Risk_: File uploads loaded entire locking byte-arrays into RAM, posing a high risk of `OutOfMemoryError` crashes under load.
3.  **Data Integrity (The "Double Spend" Problem)**:
    - _Risk_: If a user's network timed out during `POST /orders`, a retry would create a duplicate order and double-charge the customer.
4.  **Operational Maturity**:
    - _Risk_: "Dependency Hell" due to lax Maven constraints and potential downtime during deployments (Hard Shutdowns).

---

## 2. Solutions & Architecture Upgrades

We systematically refactored the core logic to address these risks.

### 🛡️ Resilience & Security

- **Rate Limiting (Token Bucket Algorithm)**:

  - **Implementation**: Integrated `Bucket4j` via a custom `RateLimitingFilter`.
  - **Policy**: Enforced a strict limit of **20 requests/minute per IP** on all `/api/**` endpoints.
  - **Outcome**: The system now gracefully rejects abuse (`429 Too Many Requests`) while remaining responsive for legitimate users.

- **Idempotency (Safe Retries)**:
  - **Implementation**: Created `IdempotencyFilter` for the critical `POST /api/customer-orders` path.
  - **Mechanism**: Tracks unique `Idempotency-Key` headers. If a key is seen twice, the system returns the cached success response immediately.
  - **Outcome**: Zero risk of double-billing or duplicate orders, even on flaky mobile networks.

### 🔄 Data Consistency (Concurrency Control)

- **Optimistic Locking**:
  - **Implementation**: Added `@Version` fields to critical stateful entities:
    - `CustomerOrder` (Transaction State)
    - `FishProduct` (Inventory State)
    - `ShoppingCart` (User Session State)
  - **Outcome**: The database now enforces strict version sequencing. Concurrent modifications trigger an `OptimisticLockingFailureException`, preserving data integrity.

### 🚀 Scalability & Performance

- **Streaming File Uploads**:
  - **Implementation**: Refactored `ProductImageResource` to use Spring's `MultipartFile` interface.
  - **Improvement**: Switched from memory-heavy `byte[]` buffering to stream-based processing. The application can now handle large concurrent uploads without spiking Heap Memory.

### 🏗️ DevOps & Build Hygiene

- **Dependency Convergence**:
  - **Action**: Configured `maven-enforcer-plugin` to `<fail>true</fail>`.
  - **Outcome**: Guarantees a deterministic classpath (No "Method Not Found" runtime errors due to library conflicts).
- **Graceful Shutdown**:
  - **Action**: Enabled `server.shutdown: graceful` in `application.yml`.
  - **Outcome**: Kubernetes/Docker can now terminate pods safely, allowing in-flight requests to complete before killing the process.

---

## 3. Completeness Verification

A final audit was conducted to ensure no gaps remained:

- ✅ **Inventory Safety**: Confirmed `@Version` was applied to `FishProduct` (not just Orders).
- ✅ **Cart Safety**: Confirmed `@Version` was applied to `ShoppingCart`.
- ✅ **Global coverage**: Confirmed Rate Limiting protects ALL API endpoints.

## 🏁 Conclusion

The codebase has transitioned from **Level 1 (Functional)** to **Level 3 (Resilient & Distributed)**. It is now structurally prepared for high-concurrency production environments.
