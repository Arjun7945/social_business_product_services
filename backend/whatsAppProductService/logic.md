# Backend Order Delivery Logic Documentation

This document details the end-to-end logic for the order delivery flow in the WhatsApp Product Service backend. It covers the entire lifecycle from a new customer's first interaction, through order placement, to delivery assignment and confirmation.

## 1. Customer Onboarding & Registration
**Entry Point:** `CustomerFlowService.processIncomingMessage`

When a user sends a message to the WhatsApp bot:
1.  **Identification**: The system checks if the sender's WhatsApp ID (`fromWaId`) exists in the `CustomerRepository`.
2.  **New Customer Creation**: If not found, a new `Customer` entity is created with status `NEW`.
3.  **Registration Flow**:
    *   **Greeting**: If the message is "Hi" or "Hello", the system asks for the customer's name (`handleNewCustomer`).
    *   **Name Collection**: The user provides their name, which is saved. The system then asks for their phone number (`handleAwaitingName`).
    *   **Phone Collection**: The user provides their phone number. The system then requests their location (`handleAwaitingPhone`).
    *   **Location Validation**: The user sends a location attachment. `LocationValidationService` calculates the distance from the business.
        *   **If within range**: The customer is marked as `REGISTERED` and can start shopping.
        *   **If out of range**: The customer is informed that delivery is not available.

## 2. Product Browsing & Cart Management
**Service:** `CustomerFlowService` & `ShoppingCartService`

Once registered, the customer can browse and shop:
1.  **Catalog Request**: Sending "start" triggers `showProductCatalog`, which fetches available `FishProduct` items from the database.
2.  **Product Selection**: The customer selects a fish from the interactive list (`handleListReply`).
3.  **Quantity Input**: The customer enters the desired quantity in KG (`handleAwaitingQuantity`).
4.  **Add to Cart**: The system validates the quantity and calls `ShoppingCartService.addToCart`.
    *   If the item exists in the cart, the quantity is updated.
    *   If not, a new `CartItem` is created.
5.  **Cart Actions**: The user is presented with options to "Add More Fish" or "Checkout".

## 3. Order Placement Logic
**Method:** `CustomerFlowService.placeOrder`

When the customer selects "Checkout" and then "Confirm Order":
1.  **Cart Retrieval**: The system retrieves all items from the customer's cart.
2.  **Order Creation**: A new `CustomerOrder` entity is created with:
    *   `status`: `PENDING`
    *   `paymentMethod`: `COD` (Cash on Delivery)
    *   `totalAmount`: Calculated from cart items.
    *   `orderTime`: Current timestamp.
3.  **Order Items**: `OrderItem` entities are created for each cart item and linked to the order.
4.  **Cart Cleanup**: The customer's cart is cleared via `ShoppingCartService.clearCart`.
5.  **Customer Notification**: A confirmation message with the Order ID and Total is sent to the customer (`WhatsAppService.sendOrderConfirmation`).

## 4. Delivery Assignment Logic
**Method:** `CustomerFlowService.sendOrderToDeliveryGroup`

Immediately after order placement, the system notifies the delivery team:
1.  **Group Notification**: The system constructs a detailed message containing:
    *   Order ID
    *   Customer Name, Phone, and Location (Google Maps coordinates)
    *   Order Items and Total Amount
2.  **Interactive Alert**: This message is sent to a configured WhatsApp Group (`whatsapp.delivery-group-id`).
3.  **Action Button**: The message includes a "Confirm Delivery" button with a payload ID `DELIVERY_TAKE_{orderId}`.

## 5. Delivery Confirmation (Delivery Person)
**Method:** `CustomerFlowService.handleDeliveryConfirmation`

When a delivery person clicks the "Confirm Delivery" button in the group:
1.  **Identification**: The system identifies the delivery person by their WhatsApp ID.
    *   If they are new, a `DeliveryPerson` entity is automatically created.
2.  **Concurrency Check**: The system checks if the order status is already `CONFIRMED`.
    *   If yes, the delivery person is informed that the order is already taken.
3.  **Order Assignment**:
    *   The `CustomerOrder` status is updated to `CONFIRMED`.
    *   The `deliveryPersonId`, `deliveryPersonName`, and `deliveryPersonWaId` are saved to the order.
    *   `confirmedAt` timestamp is set.
4.  **Notifications**:
    *   **To Customer**: "Delivery Person Assigned! Your order will be delivered by [Name]".
    *   **To Delivery Group**: "Order #[ID] taken by [Name]".

## 6. Order Delivery Completion
**Status:** `DELIVERED`

*Note: The logic to transition an order from `CONFIRMED` to `DELIVERED` is currently defined in the `OrderStatus` enum but is **not explicitly implemented** in the current backend logic.*

**Recommended Implementation for Future:**
To complete the cycle, a new feature should be added where the delivery person can send a command (e.g., "Delivered [Order ID]") or click a button to mark the order as complete. This would:
1.  Update `CustomerOrder` status to `DELIVERED`.
2.  Send a "Thank you" message to the customer.
3.  Log the delivery completion time.
