# New Roles Implementation Plan

## Overview
This document outlines the implementation plan for introducing three new roles to the WhatsApp-based fish seller application: **Admin**, **Developer**, and **Assistant Admin**. These roles will have different levels of access and capabilities within the system.

---

## Role Hierarchy & Permissions

### Role Structure
```
┌─────────────────────────────────────┐
│     ADMIN & DEVELOPER (Top Tier)   │
│  - Full system access               │
│  - Manage all roles                 │
│  - CRUD on all entities             │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│     ASSISTANT ADMIN (Mid Tier)      │
│  - Manage operational roles         │
│  - CRUD on Customers, Executives,   │
│    Delivery Persons                 │
│  - Cannot manage Admins/Developers  │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│  EXECUTIVE, DELIVERY_PERSON         │
│  (Operational Tier)                 │
│  - Limited to assigned tasks        │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│         CUSTOMER (End User)         │
│  - Browse & order products          │
└─────────────────────────────────────┘
```

### Detailed Role Permissions

#### 1. ADMIN Role
**Access Level**: Full System Access

**Capabilities**:
- ✅ Create, Read, Update, Delete Customers
- ✅ Create, Read, Update, Delete Delivery Persons
- ✅ Create, Read, Update, Delete Executives
- ✅ Create, Read, Update, Delete Assistant Admins
- ✅ Create, Read, Update, Delete Admins (other admins)
- ✅ Create, Read, Update, Delete Developers
- ✅ View all orders and analytics
- ✅ Modify system settings
- ✅ Access to all features

**Use Case**: System owner, business owner

---

#### 2. DEVELOPER Role
**Access Level**: Full System Access (Same as Admin)

**Capabilities**:
- ✅ Same as Admin role
- ✅ Additional: Technical support and debugging access
- ✅ Can be contacted by other roles for technical issues

**Use Case**: Technical team members, system developers

**Note**: Developer and Admin have identical permissions in the application logic, but are distinguished for organizational purposes.

---

#### 3. ASSISTANT ADMIN Role
**Access Level**: Operational Management

**Capabilities**:
- ✅ Create, Read, Update, Delete Customers
- ✅ Create, Read, Update, Delete Delivery Persons
- ✅ Create, Read, Update, Delete Executives
- ❌ Cannot manage Assistant Admins
- ❌ Cannot manage Admins
- ❌ Cannot manage Developers
- ✅ View orders related to managed entities
- ❌ Cannot modify system settings

**Use Case**: Store manager, operations manager, staff supervisor

---

#### 4. EXECUTIVE Role (Existing)
**Access Level**: Customer Management

**Capabilities**:
- ✅ Add new customers
- ✅ View customers they added
- ❌ Cannot modify or delete customers
- ❌ Cannot manage other roles

---

#### 5. DELIVERY_PERSON Role (Existing)
**Access Level**: Order Fulfillment

**Capabilities**:
- ✅ Confirm order deliveries
- ✅ View assigned orders
- ❌ Cannot manage customers or other entities

---

#### 6. CUSTOMER Role (Existing)
**Access Level**: Product Ordering

**Capabilities**:
- ✅ Browse products
- ✅ Place orders
- ✅ Track order status

---

## Implementation Steps

### Phase 1: Enum & Model Updates

#### 1.1 Update UserRole Enum
**File**: `src/main/java/com/fishseller/whatsappservice/model/enums/UserRole.java`

**Before**:
```java
public enum UserRole {
    CUSTOMER,
    EXECUTIVE,
    DELIVERY_PERSON
}
```

**After**:
```java
public enum UserRole {
    CUSTOMER,           // End users who order products
    EXECUTIVE,          // Staff who add customers
    DELIVERY_PERSON,    // Staff who deliver orders
    ASSISTANT_ADMIN,    // Managers who oversee operations
    ADMIN,              // System administrators
    DEVELOPER           // Technical team members
}
```

#### 1.2 Create AssistantAdminFlowStage Enum
**File**: `src/main/java/com/fishseller/whatsappservice/model/enums/AssistantAdminFlowStage.java` (NEW)

**Content**:
```java
package com.fishseller.whatsappservice.model.enums;

/**
 * Enum representing different stages in the assistant admin conversation flow
 */
public enum AssistantAdminFlowStage {
    IDLE,                           // Main menu state
    
    // Customer Management
    CUSTOMER_MENU,
    AWAITING_CUST_NAME,
    AWAITING_CUST_PHONE,
    AWAITING_CUST_WAPHONE,
    AWAITING_CUST_LOCATION,
    AWAITING_CUST_UPDATE_SELECTION,
    AWAITING_CUST_UPDATE_FIELD,
    AWAITING_CUST_DELETE_CONFIRMATION,
    CONFIRMING_CUST_ADD,
    CONFIRMING_CUST_UPDATE,
    
    // Delivery Person Management
    DELIVERY_MENU,
    AWAITING_DELIVERY_NAME,
    AWAITING_DELIVERY_PHONE,
    AWAITING_DELIVERY_WAPHONE,
    AWAITING_DELIVERY_STATUS,
    AWAITING_DELIVERY_UPDATE_SELECTION,
    AWAITING_DELIVERY_UPDATE_FIELD,
    AWAITING_DELIVERY_DELETE_CONFIRMATION,
    CONFIRMING_DELIVERY_ADD,
    CONFIRMING_DELIVERY_UPDATE,
    
    // Executive Management
    EXECUTIVE_MENU,
    AWAITING_EXEC_NAME,
    AWAITING_EXEC_PHONE,
    AWAITING_EXEC_WAPHONE,
    AWAITING_EXEC_STATUS,
    AWAITING_EXEC_UPDATE_SELECTION,
    AWAITING_EXEC_UPDATE_FIELD,
    AWAITING_EXEC_DELETE_CONFIRMATION,
    CONFIRMING_EXEC_ADD,
    CONFIRMING_EXEC_UPDATE
}
```

#### 1.3 Update TeamMember Model
**File**: `src/main/java/com/fishseller/whatsappservice/model/TeamMember.java`

**Add Field**:
```java
@Enumerated(EnumType.STRING)
private AssistantAdminFlowStage currentAssistantAdminFlowStage;
```

**Update Comment**:
```java
@Enumerated(EnumType.STRING)
@Column(nullable = false)
private UserRole role; // EXECUTIVE, DELIVERY_PERSON, ASSISTANT_ADMIN, ADMIN, or DEVELOPER
```

---

### Phase 2: Database Migration

#### 2.1 Database Schema Updates
```sql
-- The role column already supports ENUM, so it will automatically 
-- accept the new values: ASSISTANT_ADMIN, ADMIN, DEVELOPER

-- Add new column for assistant admin flow tracking
ALTER TABLE team_members 
ADD COLUMN current_assistant_admin_flow_stage VARCHAR(50);

-- Add sample admin user (update with actual details)
INSERT INTO team_members (name, phone_number, wa_phone_number, role, is_active)
VALUES ('System Admin', '1234567890', '911234567890', 'ADMIN', true);

-- Add sample developer user (update with actual details)
INSERT INTO team_members (name, phone_number, wa_phone_number, role, is_active)
VALUES ('System Developer', '0987654321', '910987654321', 'DEVELOPER', true);
```

---

### Phase 3: Service Layer Updates

#### 3.1 Update UserRoleLookupService
**File**: `src/main/java/com/fishseller/whatsappservice/service/UserRoleLookupService.java`

**Changes**:
- Ensure it correctly identifies and returns ADMIN, DEVELOPER, and ASSISTANT_ADMIN roles
- No code changes needed if implementation is generic

#### 3.2 Update CustomerFlowService Routing
**File**: `src/main/java/com/fishseller/whatsappservice/service/CustomerFlowService.java`

**Update `processIncomingMessage()` method**:

**Before**:
```java
switch (lookupResult.getRole()) {
    case EXECUTIVE:
        // Route to Executive Flow
        ...
    case DELIVERY_PERSON:
        // Route to Delivery Flow
        ...
    case CUSTOMER:
        // Route to Customer Flow
        ...
}
```

**After**:
```java
switch (lookupResult.getRole()) {
    case ADMIN:
    case DEVELOPER:
        // Route to Admin Flow
        TeamMember admin = lookupResult.asTeamMember();
        log.info("Routing to Admin Flow for: {} (Role: {})", 
                 admin.getName(), admin.getRole());
        adminFlowService.handleAdminMessage(admin, message);
        return;
    
    case ASSISTANT_ADMIN:
        // Route to Assistant Admin Flow
        TeamMember assistantAdmin = lookupResult.asTeamMember();
        log.info("Routing to Assistant Admin Flow for: {}", 
                 assistantAdmin.getName());
        assistantAdminFlowService.handleAssistantAdminMessage(assistantAdmin, message);
        return;
    
    case EXECUTIVE:
        // Route to Executive Flow (existing)
        ...
    
    case DELIVERY_PERSON:
        // Route to Delivery Flow (existing)
        ...
    
    case CUSTOMER:
        // Route to Customer Flow (existing)
        ...
}
```

#### 3.3 Create AdminFlowService
**File**: `src/main/java/com/fishseller/whatsappservice/service/AdminFlowService.java` (NEW)

**Purpose**: Handle all admin and developer interactions
**Details**: See `admin_flow_implementation_plan.md`

#### 3.4 Create AssistantAdminFlowService
**File**: `src/main/java/com/fishseller/whatsappservice/service/AssistantAdminFlowService.java` (NEW)

**Purpose**: Handle assistant admin interactions

**Key Differences from AdminFlowService**:
- No "Assistant Admin Section" in main menu
- No "Contact Developer" option (or modified to "Contact Admin")
- Cannot manage admins, developers, or other assistant admins
- Similar CRUD flows for customers, delivery persons, and executives

**Main Menu for Assistant Admin**:
```
🛡️ Welcome, [Assistant Admin Name]!

You are logged in as: ASSISTANT ADMIN

Please select a section to manage:

[Button: 👥 Customer Section]
[Button: 🚚 Delivery Person Section]
[Button: 💼 Executive Section]
[Button: 📞 Contact Admin]

💡 Tip: Send 'ABORT' anytime to return to this menu.
```

---

### Phase 4: Repository Updates

#### 4.1 Update TeamMemberRepository
**File**: `src/main/java/com/fishseller/whatsappservice/repository/TeamMemberRepository.java`

**Add Methods**:
```java
// Find all team members by role
List<TeamMember> findByRole(UserRole role);

// Find active team members by role
List<TeamMember> findByRoleAndIsActive(UserRole role, boolean isActive);

// Find all admins (both ADMIN and DEVELOPER)
@Query("SELECT tm FROM TeamMember tm WHERE tm.role IN ('ADMIN', 'DEVELOPER') AND tm.isActive = true")
List<TeamMember> findAllActiveAdmins();

// Find all assistant admins
default List<TeamMember> findAllActiveAssistantAdmins() {
    return findByRoleAndIsActive(UserRole.ASSISTANT_ADMIN, true);
}

// Find all executives
default List<TeamMember> findAllActiveExecutives() {
    return findByRoleAndIsActive(UserRole.EXECUTIVE, true);
}

// Find all delivery persons
default List<TeamMember> findAllActiveDeliveryPersons() {
    return findByRoleAndIsActive(UserRole.DELIVERY_PERSON, true);
}
```

---

### Phase 5: Access Control & Validation

#### 5.1 Create RoleValidator Utility
**File**: `src/main/java/com/fishseller/whatsappservice/util/RoleValidator.java` (NEW)

**Purpose**: Centralized role permission checking

**Content**:
```java
package com.fishseller.whatsappservice.util;

import com.fishseller.whatsappservice.model.enums.UserRole;

public class RoleValidator {
    
    /**
     * Check if a role can manage another role
     */
    public static boolean canManageRole(UserRole manager, UserRole target) {
        // Admins and Developers can manage everyone
        if (manager == UserRole.ADMIN || manager == UserRole.DEVELOPER) {
            return true;
        }
        
        // Assistant Admins can manage operational roles only
        if (manager == UserRole.ASSISTANT_ADMIN) {
            return target == UserRole.CUSTOMER ||
                   target == UserRole.EXECUTIVE ||
                   target == UserRole.DELIVERY_PERSON;
        }
        
        // Other roles cannot manage anyone
        return false;
    }
    
    /**
     * Check if a role is an admin-level role
     */
    public static boolean isAdminLevel(UserRole role) {
        return role == UserRole.ADMIN || role == UserRole.DEVELOPER;
    }
    
    /**
     * Check if a role is a management role
     */
    public static boolean isManagementRole(UserRole role) {
        return role == UserRole.ADMIN || 
               role == UserRole.DEVELOPER || 
               role == UserRole.ASSISTANT_ADMIN;
    }
    
    /**
     * Check if a role is an operational role
     */
    public static boolean isOperationalRole(UserRole role) {
        return role == UserRole.EXECUTIVE || 
               role == UserRole.DELIVERY_PERSON;
    }
}
```

#### 5.2 Implement Permission Checks
In both `AdminFlowService` and `AssistantAdminFlowService`, add validation before any CRUD operation:

```java
private boolean validatePermission(TeamMember manager, UserRole targetRole) {
    if (!RoleValidator.canManageRole(manager.getRole(), targetRole)) {
        whatsAppService.sendSimpleText(manager.getWaPhoneNumber(),
            "❌ Access Denied\n\n" +
            "You don't have permission to manage " + targetRole + " role.");
        return false;
    }
    return true;
}
```

---

### Phase 6: Audit Logging

#### 6.1 Create AuditLog Entity
**File**: `src/main/java/com/fishseller/whatsappservice/model/AuditLog.java` (NEW)

**Purpose**: Track all admin and assistant admin actions

**Content**:
```java
package com.fishseller.whatsappservice.model;

import com.fishseller.whatsappservice.model.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long performedByUserId; // TeamMember ID who performed the action
    
    @Column(nullable = false)
    private String performedByName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole performedByRole;
    
    @Column(nullable = false)
    private String action; // "CREATE", "UPDATE", "DELETE", "VIEW"
    
    @Column(nullable = false)
    private String entityType; // "CUSTOMER", "TEAM_MEMBER", etc.
    
    private Long entityId; // ID of the affected entity
    
    private String entityName; // Name of the affected entity
    
    @Column(length = 1000)
    private String details; // JSON or text description of changes
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}
```

#### 6.2 Create AuditLogRepository
**File**: `src/main/java/com/fishseller/whatsappservice/repository/AuditLogRepository.java` (NEW)

```java
package com.fishseller.whatsappservice.repository;

import com.fishseller.whatsappservice.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    
    List<AuditLog> findByPerformedByUserId(Long userId);
    
    List<AuditLog> findByEntityTypeAndEntityId(String entityType, Long entityId);
    
    List<AuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}
```

#### 6.3 Create AuditLogService
**File**: `src/main/java/com/fishseller/whatsappservice/service/AuditLogService.java` (NEW)

```java
package com.fishseller.whatsappservice.service;

import com.fishseller.whatsappservice.model.AuditLog;
import com.fishseller.whatsappservice.model.TeamMember;
import com.fishseller.whatsappservice.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogService {
    
    private final AuditLogRepository auditLogRepository;
    
    public void logAction(TeamMember performer, String action, 
                         String entityType, Long entityId, 
                         String entityName, String details) {
        AuditLog log = AuditLog.builder()
            .performedByUserId(performer.getId())
            .performedByName(performer.getName())
            .performedByRole(performer.getRole())
            .action(action)
            .entityType(entityType)
            .entityId(entityId)
            .entityName(entityName)
            .details(details)
            .build();
        
        auditLogRepository.save(log);
    }
}
```

#### 6.4 Integrate Audit Logging
In `AdminFlowService` and `AssistantAdminFlowService`, add logging after each CRUD operation:

```java
// Example: After adding a customer
auditLogService.logAction(
    admin,
    "CREATE",
    "CUSTOMER",
    newCustomer.getId(),
    newCustomer.getName(),
    String.format("Added customer: %s, Phone: %s", 
                  newCustomer.getName(), newCustomer.getPhoneNumber())
);
```

---

### Phase 7: Testing Strategy

#### 7.1 Unit Tests

**Test Files to Create**:
1. `RoleValidatorTest.java` - Test permission logic
2. `AdminFlowServiceTest.java` - Test admin CRUD operations
3. `AssistantAdminFlowServiceTest.java` - Test assistant admin operations
4. `AuditLogServiceTest.java` - Test audit logging

**Key Test Cases**:
- ✅ Admin can manage all roles
- ✅ Developer can manage all roles
- ✅ Assistant Admin can manage customers, executives, delivery persons
- ❌ Assistant Admin cannot manage admins, developers, other assistant admins
- ❌ Executive cannot access admin/assistant admin features
- ✅ Audit logs are created for all CRUD operations
- ✅ Role routing works correctly in CustomerFlowService

#### 7.2 Integration Tests

**Test Scenarios**:
1. Complete admin flow: Add customer → Update customer → Delete customer
2. Complete assistant admin flow: Add executive → Update executive → Delete executive
3. Permission denial: Assistant admin tries to add another assistant admin
4. Role switching: User with multiple roles (if applicable)
5. Audit trail: Verify all actions are logged

#### 7.3 Manual Testing Checklist

**Admin Role**:
- [ ] Admin receives personalized greeting with name and role
- [ ] Admin can add customers, delivery persons, executives, assistant admins
- [ ] Admin can update all entity types
- [ ] Admin can delete all entity types
- [ ] Admin can view all entities
- [ ] ABORT command works at all stages
- [ ] Confirm/Edit feature works for all operations

**Developer Role**:
- [ ] Developer has same capabilities as Admin
- [ ] Developer can be contacted via "Contact Developer" option

**Assistant Admin Role**:
- [ ] Assistant Admin receives personalized greeting
- [ ] Assistant Admin can manage customers, delivery persons, executives
- [ ] Assistant Admin CANNOT manage admins, developers, other assistant admins
- [ ] Assistant Admin sees appropriate menu (no Assistant Admin section)
- [ ] ABORT command works
- [ ] Confirm/Edit feature works

**Audit Logging**:
- [ ] All admin actions are logged
- [ ] All assistant admin actions are logged
- [ ] Audit logs contain correct details (who, what, when)

---

## Migration Path

### Step 1: Deploy Database Changes
```sql
-- Run migration script to add new columns
ALTER TABLE team_members 
ADD COLUMN current_assistant_admin_flow_stage VARCHAR(50);
```

### Step 2: Create Initial Admin Users
```sql
-- Add your first admin (update with real details)
INSERT INTO team_members (name, phone_number, wa_phone_number, role, is_active)
VALUES ('Your Name', 'your_phone', 'your_wa_phone', 'ADMIN', true);
```

### Step 3: Deploy Code Changes
1. Deploy updated enums
2. Deploy updated models
3. Deploy new services (AdminFlowService, AssistantAdminFlowService)
4. Deploy updated routing in CustomerFlowService

### Step 4: Test with Admin User
1. Send "Hi" from admin WhatsApp number
2. Verify greeting and menu
3. Test each CRUD operation
4. Verify audit logs

### Step 5: Create Assistant Admin Users
1. Use admin account to add assistant admins
2. Test assistant admin capabilities
3. Verify permission restrictions

---

## Security Best Practices

1. **Role Verification**: Always verify role before allowing operations
2. **Audit Everything**: Log all admin and assistant admin actions
3. **Soft Delete**: Use soft delete (isActive flag) instead of hard delete
4. **Input Validation**: Validate all inputs (phone numbers, names, etc.)
5. **Rate Limiting**: Consider rate limiting for admin operations
6. **Session Management**: Track admin sessions and timeout inactive sessions
7. **Two-Factor Authentication**: Consider adding 2FA for admin roles (future enhancement)

---

## Timeline Estimate

- **Phase 1** (Enums & Models): 1-2 hours
- **Phase 2** (Database Migration): 1 hour
- **Phase 3** (Service Layer): 10-12 hours
- **Phase 4** (Repository Updates): 1-2 hours
- **Phase 5** (Access Control): 2-3 hours
- **Phase 6** (Audit Logging): 3-4 hours
- **Phase 7** (Testing): 4-6 hours

**Total Estimated Time**: 22-30 hours

---

## Dependencies

- Admin Flow Implementation (see `admin_flow_implementation_plan.md`)
- Existing models: `Customer`, `TeamMember`
- Existing services: `WhatsAppService`, `CustomerFlowService`
- Existing repositories: `CustomerRepository`, `TeamMemberRepository`

---

## Post-Implementation Checklist

- [ ] All three new roles (Admin, Developer, Assistant Admin) are functional
- [ ] Role hierarchy is enforced correctly
- [ ] Audit logging is working for all operations
- [ ] Permission checks prevent unauthorized actions
- [ ] All CRUD operations work for each role
- [ ] ABORT command works across all flows
- [ ] Confirm/Edit feature works for all operations
- [ ] Documentation is updated
- [ ] Team is trained on new roles and capabilities

---

## Future Enhancements

1. **Role-Based Analytics**: Different dashboards for different roles
2. **Delegation**: Assistant admins can delegate tasks to executives
3. **Approval Workflows**: Multi-level approval for critical operations
4. **Notifications**: Notify admins of important events
5. **Scheduled Reports**: Automated reports sent to admins
6. **Mobile App**: Dedicated admin mobile app (beyond WhatsApp)
