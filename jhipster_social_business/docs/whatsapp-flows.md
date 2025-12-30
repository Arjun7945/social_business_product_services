# WhatsApp Message Flows

This document outlines the message dispatching logic handled by `WhatsAppDispatcherService`.

## Dispatcher Logic

The system identifies the sender's role based on their phone number and dispatches the message to the appropriate service.

```mermaid
flowchart TD
    Start[Incoming Message] --> StaleCheck{Stale Button?}
    StaleCheck -- Yes --> Warning[Send Warning & Recover]
    StaleCheck -- No --> TeamCheck{Is Team Member?}

    TeamCheck -- Yes --> RoleSwitch{Check Role}
    RoleSwitch -- ADMIN --> AdminFlow[AdminFlowService]
    RoleSwitch -- EXECUTIVE --> ExecFlow[ExecutiveFlowService]
    RoleSwitch -- ACCOUNTS_TEAM --> AccFlow[AccountsFlowService]
    RoleSwitch -- DELIVERY_PERSON --> DeliveryCheck{Is Delivery Person?}

    TeamCheck -- No --> DeliveryCheck

    DeliveryCheck -- Yes --> DeliveryFlow[DeliveryFlowService]
    DeliveryCheck -- No --> CustomerCheck{Is Existing Customer?}

    CustomerCheck -- Yes --> CustFlow[CustomerFlowService]
    CustomerCheck -- No --> UnknownFlow{Unknown/guest}

    UnknownFlow --> Onboarding[UnknownCustomerFlowService<br/>Start Onboarding]
```

## Service Responsibilities

| Service | Role / Function |
| :--- | :--- |
| **AdminFlowService** | Handles messages from Administrators (Product management, user management). |
| **ExecutiveFlowService** | Handles messages from Executives (Order oversight). |
| **DeliveryFlowService** | Handles Delivery Personnel interactions (Order pickup/delivery). |
| **AccountsFlowService** | Handles Accounts Team interactions (Payment verification). |
| **CustomerFlowService** | Handles registered customer shopping flows. |
| **UnknownCustomerFlowService** | Handles registration and onboarding for new numbers. |
