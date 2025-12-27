# Angular to React Migration Plan
## Complete Application Migration - 4 Phases

> **Objective**: Migrate the entire JHipster Angular application to React while maintaining all functionality, improving UX, and ensuring API compatibility.

---

## 📊 Current Angular Application Inventory

### Entities (15 Total)
1. **Customer** - Customer management with WhatsApp integration
2. **Customer Order** - Order management with status tracking
3. **Fish Product** - Product catalog
4. **Product Image** - Product image management
5. **Order Item** - Order line items
6. **Shopping Cart** - Cart management
7. **Cart Item** - Individual cart items
8. **Team Member** - Team/staff management
9. **Bot Session** - WhatsApp bot session tracking
10. **Button Action** - Bot button interactions
11. **Authority** - Role/permission management
12. **User** - User accounts
13. **Removed User** - Soft-deleted users
14. **Removed Order Summary** - Deleted order records

### Modules
- **Account**: Registration, Login, Password Reset, Settings, Activation
- **Admin**: User Management, Health, Metrics, Configuration, Logs, Docs
- **Home**: Dashboard/Landing
- **Contact Dev**: Support contact
- **Raise Ticket**: Support tickets

---

---

## 🚧 Phase 0: Base Routing Setup (Week 0)

> **Goal**: Establish the routing foundation separating the Customer Portal from the Internal App.

### 0.1 Routing Architecture
- **Root (`/`)**: Main entry point for the migrated Internal Admin Application.
- **Customer Portal (`/ourCustomer`)**: Dedicated entry point for all customer-facing features.

### 0.2 Initial Setup
- [ ] Refactor `App.jsx` to use nested routes
- [ ] Create `MainLayout` for internal app placeholder
- [ ] Create `CustomerLayout` for customer pages (Landing, Login, etc.)
- [ ] Verify all existing React pages work under `/ourCustomer/*`

---

## 🎯 Phase 1: Core Customer Experience (Weeks 1-3)

> **Goal**: Migrate customer-facing features for order placement and tracking

### 1.1 Authentication & Account (Week 1)

#### Components to Migrate
| Angular Component | React Component | Priority |
|-------------------|-----------------|----------|
| `login.component.ts` | `LoginPage.jsx` | ✅ DONE |
| `register.component.ts` | `RegisterPage.jsx` | HIGH |
| `activate.component.ts` | `ActivationPage.jsx` | HIGH |
| `password-reset-init.component.ts` | `PasswordResetPage.jsx` | MEDIUM |
| `password-reset-finish.component.ts` | `PasswordResetFinishPage.jsx` | MEDIUM |
| `settings.component.ts` | `SettingsPage.jsx` | MEDIUM |

#### API Endpoints
```javascript
// api/auth.js
export const register = (data) => api.post('/api/register', data);
export const activate = (key) => api.get(`/api/activate?key=${key}`);
export const requestPasswordReset = (email) => api.post('/api/account/reset-password/init', { email });
export const finishPasswordReset = (data) => api.post('/api/account/reset-password/finish', data);
export const getAccount = () => api.get('/api/account');
export const saveAccount = (data) => api.post('/api/account', data);
export const changePassword = (data) => api.post('/api/account/change-password', data);
```

#### Features
- [x] WhatsApp-based login (DONE)
- [ ] Email/password registration
- [ ] Account activation via email
- [ ] Password reset flow
- [ ] User profile settings
- [ ] Password change

---

### 1.2 Product Catalog (Week 2)

#### Components to Migrate
| Angular Component | React Component | API Endpoint |
|-------------------|-----------------|--------------|
| `fish-product.component.ts` (list) | `ProductListPage.jsx` | `GET /api/fish-products` |
| `fish-product-detail.component.ts` | `ProductDetailPage.jsx` | `GET /api/fish-products/{id}` |
| `product-image.component.ts` | Embedded in Product components | `GET /api/product-images` |

#### Features
- Product grid/list view with images
- Product search and filtering
- Product details modal/page
- Price display per kg
- Stock availability
- Product categories

#### React Structure
```
src/pages/products/
├── ProductListPage.jsx       # Main catalog
├── ProductDetailModal.jsx    # Product details
├── ProductCard.jsx           # Reusable card component
└── ProductFilters.jsx        # Search/filter component
```

---

### 1.3 Shopping Cart & Checkout (Week 2-3)

#### Components to Migrate
| Angular Component | React Component | API Endpoint | New Route |
|-------------------|-----------------|--------------|-----------|
| `shopping-cart.component.ts` | `CartPage.jsx` | `GET /api/shopping-carts` | `/ourCustomer/cart` |
| `cart-item.component.ts` | `CartItem.jsx` | `GET /api/cart-items` | - |
| `customer-order-update.component.ts` | `CheckoutPage.jsx` | `POST /api/customer-orders` | `/ourCustomer/checkout` |

#### Features
- Add/remove items from cart
- Update quantities
- Cart summary (subtotal, total)
- Checkout process
- Order placement
- Delivery address input

#### API Integration
```javascript
// api/cart.js
export const getCart = (customerId) => api.get(`/api/shopping-carts?customerId.equals=${customerId}`);
export const addToCart = (data) => api.post('/api/cart-items', data);
export const updateCartItem = (id, data) => api.put(`/api/cart-items/${id}`, data);
export const removeFromCart = (id) => api.delete(`/api/cart-items/${id}`);
export const placeOrder = (data) => api.post('/api/customer-orders', data);
```

---

### 1.4 Order Tracking (Week 3)

#### Components to Migrate
| Angular Component | React Component | Status |
|-------------------|-----------------|--------|
| `customer-order.component.ts` (list) | `OrderHistoryPage.jsx` | NEW |
| `customer-order-detail.component.ts` | `OrderDetailPage.jsx` | NEW |
| `order-tracking-dialog.component.ts` | `TrackOrderPage.jsx` | ✅ DONE (UI) |

#### Features
- Order history list
- Order status timeline
- Real-time tracking
- Driver information
- Delivery status updates
- Order cancellation (if allowed)

#### API Integration
```javascript
// api/orders.js
export const getCustomerOrders = (customerId) => 
  api.get(`/api/customer-orders?customerId.equals=${customerId}`);
export const getOrderById = (id) => api.get(`/api/customer-orders/${id}`);
export const getOrderHistory = (orderId) => 
  api.get(`/api/order-status-histories?orderId.equals=${orderId}`);
export const cancelOrder = (id) => api.delete(`/api/customer-orders/${id}`);
```

---

## 🛠️ Phase 2: Admin & Management (Weeks 4-6)

> **Goal**: Migrate all admin and management features

### 2.1 Product Management (Week 4)

#### Components to Migrate
| Angular Component | React Component | Features |
|-------------------|-----------------|----------|
| `fish-product-update.component.ts` | `ProductFormPage.jsx` | Create/Edit products |
| `fish-product-delete-dialog.component.ts` | `DeleteProductModal.jsx` | Delete confirmation |
| `product-image-update.component.ts` | `ProductImageUpload.jsx` | Image upload |

#### Features
- Create new products
- Edit existing products
- Upload/manage product images
- Set pricing
- Manage stock levels
- Delete products

#### React Structure
```
src/pages/admin/products/
├── ProductManagementPage.jsx    # List with actions
├── ProductFormPage.jsx          # Create/Edit form
├── ProductImageUpload.jsx       # Image management
└── DeleteProductModal.jsx       # Confirmation dialog
```

---

### 2.2 Order Management (Week 4-5)

#### Components to Migrate
| Angular Component | React Component | Features |
|-------------------|-----------------|----------|
| `customer-order.component.ts` | `OrderManagementPage.jsx` | All orders list |
| `customer-order-update.component.ts` | `OrderEditPage.jsx` | Edit order details |
| `order-item.component.ts` | `OrderItemsTable.jsx` | Order items management |

#### Features
- View all orders
- Filter by status, date, customer
- Update order status
- Assign delivery person
- Edit order items
- Generate invoices
- Order analytics

---

### 2.3 Customer Management (Week 5)

#### Components to Migrate
| Angular Component | React Component | Features |
|-------------------|-----------------|----------|
| `customer.component.ts` | `CustomerManagementPage.jsx` | Customer list |
| `customer-detail.component.ts` | `CustomerDetailPage.jsx` | Customer profile |
| `customer-update.component.ts` | `CustomerFormPage.jsx` | Create/Edit customer |
| `customer-delete-dialog.component.ts` | `DeleteCustomerModal.jsx` | Delete confirmation |

#### Features
- Customer list with search
- Customer details view
- Add/edit customers
- View customer orders
- Customer analytics
- WhatsApp number management

---

### 2.4 Team Management (Week 5-6)

#### Components to Migrate
| Angular Component | React Component | Features |
|-------------------|-----------------|----------|
| `team-member.component.ts` | `TeamManagementPage.jsx` | Team list |
| `team-member-update.component.ts` | `TeamMemberFormPage.jsx` | Add/Edit members |
| `user-management.component.ts` | `UserManagementPage.jsx` | User accounts |
| `user-management-update.component.ts` | `UserFormPage.jsx` | Create/Edit users |

#### Features
- Team member directory
- Role assignment
- User account management
- Permissions management
- Activity tracking

---

## 🤖 Phase 3: WhatsApp Bot Integration (Weeks 7-8)

> **Goal**: Migrate bot session and interaction tracking

### 3.1 Bot Session Management (Week 7)

#### Components to Migrate
| Angular Component | React Component | Features |
|-------------------|-----------------|----------|
| `bot-session.component.ts` | `BotSessionsPage.jsx` | Active sessions |
| `bot-session-detail.component.ts` | `BotSessionDetailPage.jsx` | Session details |
| `button-action.component.ts` | `BotActionsPage.jsx` | Button interactions |

#### Features
- View active bot sessions
- Session history
- Customer interaction logs
- Button click analytics
- Session duration tracking
- Conversation flow visualization

#### API Integration
```javascript
// api/bot.js
export const getBotSessions = (params) => api.get('/api/bot-sessions', { params });
export const getSessionById = (id) => api.get(`/api/bot-sessions/${id}`);
export const getButtonActions = (sessionId) => 
  api.get(`/api/button-actions?sessionId.equals=${sessionId}`);
```

---

### 3.2 Bot Analytics Dashboard (Week 8)

#### New Components
| Component | Purpose |
|-----------|---------|
| `BotAnalyticsDashboard.jsx` | Overview metrics |
| `BotConversationFlow.jsx` | Flow visualization |
| `BotPerformanceMetrics.jsx` | Performance stats |

#### Features
- Total sessions count
- Active users
- Conversion rates
- Popular products (via bot)
- Response time metrics
- Error rate tracking

---

## 🔧 Phase 4: Admin Tools & System Management (Weeks 9-10)

> **Goal**: Migrate all admin tools and system features

### 4.1 User & Authority Management (Week 9)

#### Components to Migrate
| Angular Component | React Component | Features |
|-------------------|-----------------|----------|
| `authority.component.ts` | `AuthorityManagementPage.jsx` | Roles/permissions |
| `authority-update.component.ts` | `AuthorityFormPage.jsx` | Create/Edit roles |

#### Features
- Role management (ADMIN, USER, DELIVERY, etc.)
- Permission assignment
- User-role mapping
- Authority hierarchy

---

### 4.2 System Administration (Week 9-10)

#### Components to Migrate
| Angular Component | React Component | Features |
|-------------------|-----------------|----------|
| `health.component.ts` | `SystemHealthPage.jsx` | Health checks |
| `metrics.component.ts` | `SystemMetricsPage.jsx` | Performance metrics |
| `logs.component.ts` | `LogsPage.jsx` | Log viewer |
| `configuration.component.ts` | `ConfigurationPage.jsx` | System config |
| `docs.component.ts` | `ApiDocsPage.jsx` | API documentation |

#### Features
- System health monitoring
- JVM metrics
- Database connection status
- API endpoint metrics
- Log level management
- Configuration viewer
- Swagger/OpenAPI docs

---

### 4.3 Support & Utilities (Week 10)

#### Components to Migrate
| Angular Component | React Component | Features |
|-------------------|-----------------|----------|
| `contact-dev.component.ts` | `ContactSupportPage.jsx` | Contact form |
| `raise-ticket.component.ts` | `SupportTicketPage.jsx` | Ticket creation |
| `removed-user.component.ts` | `DeletedUsersPage.jsx` | Soft-deleted users |
| `removed-order-summary.component.ts` | `DeletedOrdersPage.jsx` | Deleted orders |

#### Features
- Support contact form
- Ticket submission
- View deleted records
- Restore functionality
- Permanent deletion

---

### 4.4 Shared Components & Utilities (Week 10)

#### Components to Migrate
| Angular Component | React Component | Features |
|-------------------|-----------------|----------|
| `alert.component.ts` | `Alert.jsx` | Toast/Inline alerts |
| `alert-error.component.ts` | `AlertError.jsx` | API Error handling |
| `item-count.component.ts` | `ItemCount.jsx` | Pagination stats |
| `filter.component.ts` | `Filter.jsx` | List filtering |
| `sort.directive.ts` | `Sort.jsx` | Table sorting |

#### Directives/Pipes to React Hooks
- `has-any-authority.directive.ts` -> `useAuthority()` hook
- `translate.directive.ts` -> `useTranslate()` hook or `react-i18next`
- `format-medium-date.pipe.ts` -> `formatDate()` utility
- `duration.pipe.ts` -> `formatDuration()` utility

---

## 📁 React Application Structure

```
wts-product-service-app/
├── src/
│   ├── components/
│   │   ├── common/
│   │   │   ├── Button.jsx
│   │   │   ├── Input.jsx
│   │   │   ├── Modal.jsx
│   │   │   ├── Table.jsx
│   │   │   ├── Pagination.jsx
│   │   │   └── LoadingSpinner.jsx
│   │   ├── layout/
│   │   │   ├── Navbar.jsx
│   │   │   ├── Sidebar.jsx
│   │   │   ├── Footer.jsx
│   │   │   └── GlobalBackground.jsx
│   │   └── forms/
│   │       ├── ProductForm.jsx
│   │       ├── OrderForm.jsx
│   │       └── CustomerForm.jsx
│   ├── pages/
│   │   ├── auth/
│   │   │   ├── LoginPage.jsx ✅
│   │   │   ├── RegisterPage.jsx
│   │   │   ├── ActivationPage.jsx
│   │   │   └── PasswordResetPage.jsx
│   │   ├── customer/
│   │   │   ├── ProductListPage.jsx
│   │   │   ├── ProductDetailPage.jsx
│   │   │   ├── CartPage.jsx
│   │   │   ├── CheckoutPage.jsx
│   │   │   ├── OrderHistoryPage.jsx
│   │   │   └── TrackOrderPage.jsx ✅
│   │   ├── admin/
│   │   │   ├── products/
│   │   │   │   ├── ProductManagementPage.jsx
│   │   │   │   └── ProductFormPage.jsx
│   │   │   ├── orders/
│   │   │   │   ├── OrderManagementPage.jsx
│   │   │   │   └── OrderEditPage.jsx
│   │   │   ├── customers/
│   │   │   │   ├── CustomerManagementPage.jsx
│   │   │   │   └── CustomerFormPage.jsx
│   │   │   ├── team/
│   │   │   │   ├── TeamManagementPage.jsx
│   │   │   │   └── UserManagementPage.jsx
│   │   │   ├── bot/
│   │   │   │   ├── BotSessionsPage.jsx
│   │   │   │   └── BotAnalyticsPage.jsx
│   │   │   └── system/
│   │   │       ├── SystemHealthPage.jsx
│   │   │       ├── SystemMetricsPage.jsx
│   │   │       └── LogsPage.jsx
│   │   └── support/
│   │       ├── ContactSupportPage.jsx
│   │       └── SupportTicketPage.jsx
│   ├── api/
│   │   ├── auth.js
│   │   ├── products.js
│   │   ├── cart.js
│   │   ├── orders.js
│   │   ├── customers.js
│   │   ├── team.js
│   │   ├── bot.js
│   │   └── admin.js
│   ├── hooks/
│   │   ├── useAuth.js
│   │   ├── useCart.js
│   │   ├── useOrders.js
│   │   └── usePagination.js
│   ├── context/
│   │   ├── AuthContext.jsx
│   │   ├── CartContext.jsx
│   │   └── ThemeContext.jsx
│   ├── utils/
│   │   ├── formatters.js
│   │   ├── validators.js
│   │   └── constants.js
│   └── App.jsx
```

---

## 🔄 Migration Strategy

### Parallel Development & Routing Strategy
1. **Routing Separation**:
   - **`/` (Root)**: The main internal application (Admin, Management, Staff tools) - replacing the legacy Angular app.
   - **`/ourCustomer`**: The dedicated customer-facing portal (Landing, Login, Tracking, Cart).
   
2. **Phase 1 Execution**:
   - Move existing customer pages (`LandingPage`, `LoginPage`, etc.) to `/ourCustomer` routes.
   - Establish `/` as the entry point for the migrated internal application.

3. **Keep Angular running** at `/admin` (legacy) until fully migrated to React root.
4. **Gradual migration** of admin features to React root.

### API Compatibility
- All existing REST endpoints remain unchanged
- React uses same DTOs and response formats
- No backend modifications required
- Maintain JHipster security model

### Data Migration
- No database changes needed
- All entities remain the same
- Use existing JPA repositories
- Maintain audit trails

---

## ✅ Success Criteria

### Phase 1 Complete When:
- [x] Customer can login via WhatsApp
- [ ] Customer can browse products
- [ ] Customer can add to cart and checkout
- [ ] Customer can track orders
- [ ] All features have same functionality as Angular

### Phase 2 Complete When:
- [ ] Admin can manage products
- [ ] Admin can manage orders
- [ ] Admin can manage customers
- [ ] Admin can manage team members
- [ ] All CRUD operations work

### Phase 3 Complete When:
- [ ] Bot sessions are tracked
- [ ] Bot analytics are visible
- [ ] Interaction logs are accessible

### Phase 4 Complete When:
- [ ] All system admin tools migrated
- [ ] Support features migrated
- [ ] Angular code can be removed
- [ ] 100% feature parity achieved

---

## 📊 Progress Tracking

| Phase | Components | Status | Completion |
|-------|-----------|--------|------------|
| Phase 1 | 15 components | In Progress | 13% (2/15) |
| Phase 2 | 20 components | Not Started | 0% |
| Phase 3 | 8 components | Not Started | 0% |
| Phase 4 | 15 components | Not Started | 0% |
| **TOTAL** | **58 components** | **In Progress** | **3.4%** |

---

## 🚀 Next Steps

1. **Review & Approve** this migration plan
2. **Start Phase 1.1** - Complete authentication pages
3. **Build Phase 1.2** - Product catalog
4. **Implement Phase 1.3** - Shopping cart
5. **Continue sequentially** through all phases

**Estimated Timeline**: 10 weeks for complete migration
**Team Size**: 2-3 developers recommended
