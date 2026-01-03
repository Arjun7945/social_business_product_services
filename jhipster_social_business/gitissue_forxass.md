# [Feature] Implement Core XaaS Infrastructure: Reliability, Licensing, and Deployment

## Summary
This task aims to implement the missing architectural layers defined in the `XaaS_ideas.md` blueprint to make the platform production-ready for a B2B SaaS B2B model.

**Business Value:**
*   **Reliability:** Prevents "Bot Lag" and WhatsApp API timeouts during high traffic by ensuring the server responds immediately to webhooks.
*   **Revenue Protection:** Ensures only active subscribers can use the software via the Licensing Heartbeat.
*   **Scalability:** Automates the deployment process, reducing the manual effort required to onboard 50+ distinct clients.

## Acceptance Criteria
**1. Reliability Layer (Async Processing)**
- [ ] `WhatsAppWebhookController` must return `200 OK` immediately upon receiving a payload.
- [ ] Actual logic processing (DB calls, PDF generation) must be offloaded to an `@Async` service method (e.g., `WhatsAppDispatcherService`).
- [ ] Verify that the main thread is not blocked by heavy operations.

**2. Licensing Layer (Heartbeat)**
- [ ] Implement a `@Scheduled` task that runs on application startup and periodically (e.g., daily).
- [ ] The task must ping a central validation endpoint (can be mocked/configurable in `application.yml`) to verify subscription status.
- [ ] If the license is invalid, the application should log a critical alert or restrict functionality (as per business rule).

**3. Infrastructure Layer (Deployment)**
- [ ] Create a `deploy.sh` script in the project root.
- [ ] The script must handle:
    - Docker installation check.
    - Pulling the latest Docker image.
    - Injecting environment variables (Database credentials, WhatsApp tokens) from a local `.env` file.
    - Starting/Restarting the container.

## Related Epics/Stories
*   **Blueprint Reference:** `LATEST/social_business_product_services/XaaS_ideas.md`
*   **Dependencies:** WhatsApp Business API Integration (Existing)

## Stakeholders
*   **Product Owner:** (Owner of the SaaS roadmap)
*   **Lead Developer:** (Responsible for architecture compliance)
*   **DevOps/System Admin:** (Responsible for deploying to client VPS)

## Additional Notes
*   **Async Configuration:** Ensure `@EnableAsync` is active in the Spring configuration.
*   **Security:** Ensure the License Check does not expose sensitive server details.
*   **Testing:** `deploy.sh` should be tested on a clean Ubuntu/Debian environment to ensure it works without pre-existing dependencies.
