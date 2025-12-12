# Admin Flow Implementation Plan

## Overview
This document outlines the implementation plan for the Admin Flow feature in the WhatsApp-based fish seller application. The admin role will have comprehensive CRUD (Create, Read, Update, Delete) capabilities for managing customers, delivery persons, executives, and assistant admins.

---

## Requirements Summary

### Admin Capabilities
1. **Greeting & Welcome**: Personalized greeting with admin name and role
2. **Main Menu Options**:
   - Customer Section
   - Delivery Person Section
   - Executive Section
   - Assistant Admin Section
   - Contact Developer

3. **CRUD Operations** for each section:
   - Add (Create)
   - Update (Modify)
   - Delete (Remove)
   - Show All (List)

4. **Special Features**:
   - **ABORT Command**: Cancel current operation and return to main menu
   - **Confirm/Edit Feature**: Review and confirm before finalizing operations

---

## Implementation Steps

### Phase 1: Database & Model Updates

#### 1.1 Update UserRole Enum
**File**: `src/main/java/com/fishseller/whatsappservice/model/enums/UserRole.java`

**Changes**:
```java
public enum UserRole {
    CUSTOMER,
    EXECUTIVE,
    DELIVERY_PERSON,
    ASSISTANT_ADMIN,  // NEW
    ADMIN,            // NEW
    DEVELOPER         // NEW
}
```

#### 1.2 Create AdminFlowStage Enum
**File**: `src/main/java/com/fishseller/whatsappservice/model/enums/AdminFlowStage.java` (NEW)

**Content**:
```java
public enum AdminFlowStage {
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
    CONFIRMING_EXEC_UPDATE,
    
    // Assistant Admin Management
    ASSISTANT_MENU,
    AWAITING_ASSISTANT_NAME,
    AWAITING_ASSISTANT_PHONE,
    AWAITING_ASSISTANT_WAPHONE,
    AWAITING_ASSISTANT_STATUS,
    AWAITING_ASSISTANT_UPDATE_SELECTION,
    AWAITING_ASSISTANT_UPDATE_FIELD,
    AWAITING_ASSISTANT_DELETE_CONFIRMATION,
    CONFIRMING_ASSISTANT_ADD,
    CONFIRMING_ASSISTANT_UPDATE
}
```

#### 1.3 Update TeamMember Model
**File**: `src/main/java/com/fishseller/whatsappservice/model/TeamMember.java`

**Changes**:
- Add `AdminFlowStage currentAdminFlowStage` field for admin-specific flow tracking
- Add temporary fields for CRUD operations:
  ```java
  private String tempEntityType;        // "CUSTOMER", "DELIVERY", "EXECUTIVE", "ASSISTANT"
  private Long tempEntityId;            // ID of entity being updated/deleted
  private String tempFieldName;         // Field being updated
  private String tempFieldValue;        // New value for field
  ```

---

### Phase 2: Service Layer Implementation

#### 2.1 Create AdminFlowService
**File**: `src/main/java/com/fishseller/whatsappservice/service/AdminFlowService.java` (NEW)

**Responsibilities**:
- Handle all admin WhatsApp interactions
- Implement CRUD operations for all entity types
- Handle ABORT command at any stage
- Implement confirm/edit workflow

**Key Methods**:

```java
// Main entry point
public void handleAdminMessage(TeamMember admin, WhatsAppWebhookDto.Message message)

// Menu handlers
private void showMainMenu(TeamMember admin)
private void showCustomerMenu(TeamMember admin)
private void showDeliveryPersonMenu(TeamMember admin)
private void showExecutiveMenu(TeamMember admin)
private void showAssistantAdminMenu(TeamMember admin)

// Customer CRUD
private void startAddCustomer(TeamMember admin)
private void handleCustomerNameInput(TeamMember admin, String name)
private void handleCustomerPhoneInput(TeamMember admin, String phone)
private void handleCustomerWaPhoneInput(TeamMember admin, String waPhone)
private void handleCustomerLocationInput(TeamMember admin, Location location)
private void confirmCustomerAdd(TeamMember admin)
private void finalizeCustomerAdd(TeamMember admin)
private void showAllCustomers(TeamMember admin)
private void startUpdateCustomer(TeamMember admin)
private void handleCustomerUpdateSelection(TeamMember admin, Long customerId)
private void handleCustomerFieldUpdate(TeamMember admin, String field, String value)
private void confirmCustomerUpdate(TeamMember admin)
private void startDeleteCustomer(TeamMember admin)
private void confirmCustomerDelete(TeamMember admin, Long customerId)

// Delivery Person CRUD (similar structure)
private void startAddDeliveryPerson(TeamMember admin)
private void handleDeliveryPersonNameInput(TeamMember admin, String name)
// ... similar methods for delivery person

// Executive CRUD (similar structure)
private void startAddExecutive(TeamMember admin)
// ... similar methods for executive

// Assistant Admin CRUD (similar structure)
private void startAddAssistantAdmin(TeamMember admin)
// ... similar methods for assistant admin

// Utility methods
private void handleAbortCommand(TeamMember admin)
private boolean isAbortCommand(String text)
private void sendConfirmationPrompt(TeamMember admin, String entityType, String operation, String details)
```

---

### Phase 3: WhatsApp Message Flow Design

#### 3.1 Admin Greeting Flow
```
Admin sends: "Hi" or "Hello"
System responds:
  "🎉 Welcome, [Admin Name]! 👑
  
  You are logged in as: ADMIN
  
  Please select a section to manage:
  
  [Button: 👥 Customer Section]
  [Button: 🚚 Delivery Person Section]
  [Button: 💼 Executive Section]
  [Button: 🛡️ Assistant Admin Section]
  [Button: 👨‍💻 Contact Developer]
  
  💡 Tip: Send 'ABORT' anytime to return to this menu."
```

#### 3.2 Customer Section Flow
```
Admin selects: Customer Section
System responds:
  "👥 Customer Management
  
  What would you like to do?
  
  [Button: ➕ Add Customer]
  [Button: ✏️ Update Customer]
  [Button: 🗑️ Delete Customer]
  [Button: 📋 Show All Customers]
  [Button: ⬅️ Back to Main Menu]"
```

#### 3.3 Add Customer Flow with Confirm/Edit
```
Step 1: Admin selects "Add Customer"
System: "Please provide the customer's name:"

Step 2: Admin enters name
System: "Please provide the customer's phone number:"

Step 3: Admin enters phone
System: "Please provide the customer's WhatsApp number (with country code):"

Step 4: Admin enters WhatsApp number
System: "Please share the customer's location."

Step 5: Admin shares location
System: "✅ Customer Details Summary:
  
  Name: [Name]
  Phone: [Phone]
  WhatsApp: [WhatsApp]
  Location: [Lat, Lon]
  Distance: [X km]
  
  [Button: ✅ Confirm & Add]
  [Button: ✏️ Edit Details]
  [Button: ❌ Cancel]"

Step 6a: If Confirm
System: "✅ Customer added successfully!"
[Return to Customer Menu]

Step 6b: If Edit
System: "What would you like to edit?
  [Button: Name]
  [Button: Phone]
  [Button: WhatsApp Number]
  [Button: Location]
  [Button: ⬅️ Back]"
```

#### 3.4 Update Customer Flow
```
Step 1: Admin selects "Update Customer"
System: Shows list of customers (interactive list or paginated buttons)

Step 2: Admin selects customer
System: "Selected: [Customer Name]
  
  Current Details:
  Name: [Name]
  Phone: [Phone]
  WhatsApp: [WhatsApp]
  Location: [Lat, Lon]
  
  What would you like to update?
  [Button: Name]
  [Button: Phone]
  [Button: WhatsApp Number]
  [Button: Location]
  [Button: ⬅️ Back]"

Step 3: Admin selects field
System: "Current [Field]: [Value]
  Please enter new value:"

Step 4: Admin enters new value
System: "Confirm Update:
  [Field]: [Old Value] → [New Value]
  
  [Button: ✅ Confirm]
  [Button: ✏️ Edit Again]
  [Button: ❌ Cancel]"

Step 5: Admin confirms
System: "✅ Customer updated successfully!"
```

#### 3.5 Delete Customer Flow
```
Step 1: Admin selects "Delete Customer"
System: Shows list of customers

Step 2: Admin selects customer
System: "⚠️ Delete Confirmation
  
  Customer: [Name]
  Phone: [Phone]
  
  Are you sure you want to delete this customer?
  This action cannot be undone!
  
  [Button: ✅ Yes, Delete]
  [Button: ❌ No, Cancel]"

Step 3: Admin confirms
System: "✅ Customer deleted successfully!"
```

#### 3.6 Show All Customers Flow
```
Admin selects: "Show All Customers"
System: "📋 All Customers (Total: X)
  
  1. [Name] - [Phone]
     WhatsApp: [WA Phone]
     Distance: [X km]
     Added: [Date]
  
  2. [Name] - [Phone]
     ...
  
  [Button: ⬅️ Back to Customer Menu]"
```

#### 3.7 ABORT Command Flow
```
At any stage, Admin sends: "ABORT"
System: "❌ Operation cancelled.
  
  Returning to main menu..."
[Shows Main Menu]
```

---

### Phase 4: Similar Flows for Other Entities

#### 4.1 Delivery Person Management
- Same CRUD structure as Customer
- Fields: Name, Phone, WhatsApp Number, Is Active

#### 4.2 Executive Management
- Same CRUD structure as Customer
- Fields: Name, Phone, WhatsApp Number, Is Active

#### 4.3 Assistant Admin Management
- Same CRUD structure as Customer
- Fields: Name, Phone, WhatsApp Number, Is Active
- **Note**: Assistant Admin is stored in `team_members` table with `role = ASSISTANT_ADMIN`

---

### Phase 5: Integration with Existing System

#### 5.1 Update WhatsAppService
**File**: `src/main/java/com/fishseller/whatsappservice/service/WhatsAppService.java`

**Changes**:
- Add method to send admin-specific messages
- Add helper methods for interactive lists with more than 3 items (for showing all entities)

#### 5.2 Update CustomerFlowService
**File**: `src/main/java/com/fishseller/whatsappservice/service/CustomerFlowService.java`

**Changes** in `processIncomingMessage()`:
```java
case ADMIN:
case DEVELOPER:
    TeamMember admin = lookupResult.asTeamMember();
    log.info("Routing to Admin Flow for: {}", admin.getName());
    adminFlowService.handleAdminMessage(admin, message);
    return;
```

#### 5.3 Update TeamMemberRepository
**File**: `src/main/java/com/fishseller/whatsappservice/repository/TeamMemberRepository.java`

**Add Methods**:
```java
List<TeamMember> findByRole(UserRole role);
List<TeamMember> findByRoleAndIsActive(UserRole role, boolean isActive);
```

---

### Phase 6: Testing & Validation

#### 6.1 Unit Tests
- Test each CRUD operation for all entity types
- Test ABORT command at various stages
- Test confirm/edit workflow
- Test validation (e.g., duplicate phone numbers, invalid locations)

#### 6.2 Integration Tests
- Test complete flows from greeting to operation completion
- Test switching between different sections
- Test error handling and edge cases

#### 6.3 Manual Testing Checklist
- [ ] Admin greeting displays correctly with name and role
- [ ] All main menu options work
- [ ] Customer CRUD operations work end-to-end
- [ ] Delivery Person CRUD operations work end-to-end
- [ ] Executive CRUD operations work end-to-end
- [ ] Assistant Admin CRUD operations work end-to-end
- [ ] ABORT command works at all stages
- [ ] Confirm/Edit feature works for all add/update operations
- [ ] Show All displays correctly for each entity type
- [ ] Contact Developer option works
- [ ] Error messages are clear and helpful

---

## Database Schema Changes

### TeamMember Table Updates
```sql
-- Add new columns for admin flow tracking
ALTER TABLE team_members 
ADD COLUMN current_admin_flow_stage VARCHAR(50),
ADD COLUMN temp_entity_type VARCHAR(50),
ADD COLUMN temp_entity_id BIGINT,
ADD COLUMN temp_field_name VARCHAR(50),
ADD COLUMN temp_field_value VARCHAR(255);

-- No changes needed for role column as it already supports ENUM
```

---

## Error Handling

### Common Error Scenarios
1. **Invalid Input**: Provide clear error message and ask for re-entry
2. **Entity Not Found**: Inform admin and return to menu
3. **Duplicate Entry**: Check for existing phone/WhatsApp numbers before adding
4. **Location Out of Range**: Warn admin but allow adding (admin override)
5. **Network Issues**: Retry mechanism for WhatsApp API calls

---

## Security Considerations

1. **Role Verification**: Always verify admin role before allowing operations
2. **Audit Logging**: Log all admin actions (add, update, delete) with timestamp and admin ID
3. **Soft Delete**: Consider soft delete instead of hard delete for customers and team members
4. **Data Validation**: Validate all inputs (phone numbers, names, etc.)

---

## Future Enhancements

1. **Pagination**: For "Show All" when entity count is high
2. **Search Functionality**: Search customers/team members by name or phone
3. **Bulk Operations**: Add/update/delete multiple entities at once
4. **Reports**: Generate reports on customer activity, order statistics, etc.
5. **Export Data**: Export customer/order data to CSV/Excel
6. **Analytics Dashboard**: View key metrics via WhatsApp

---

## Timeline Estimate

- **Phase 1** (Database & Models): 2-3 hours
- **Phase 2** (Service Layer): 8-10 hours
- **Phase 3** (Message Flows): 4-5 hours
- **Phase 4** (Other Entities): 6-8 hours
- **Phase 5** (Integration): 2-3 hours
- **Phase 6** (Testing): 4-6 hours

**Total Estimated Time**: 26-35 hours

---

## Dependencies

- Existing models: `Customer`, `TeamMember`
- Existing services: `WhatsAppService`, `LocationValidationService`
- Existing repositories: `CustomerRepository`, `TeamMemberRepository`
- WhatsApp Business API for interactive messages

---

## Notes

- The admin flow should be intuitive and require minimal typing
- Use interactive buttons and lists wherever possible
- Provide clear feedback for every action
- ABORT command should be prominently mentioned in initial greeting
- Confirm/Edit feature prevents accidental data entry errors
