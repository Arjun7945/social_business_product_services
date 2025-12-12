# Quick Testing Guide - Product Image Carousel

## Admin Testing Flow

### 1. Start Admin Session
```
Send: "hi"
Expected: Welcome message with main menu buttons
```

### 2. Navigate to Products
```
Click: "🐟 Products" button
Expected: Product management menu with options:
  - ➕ Add Product
  - 📋 Show All
  - ⬅️ Back
```

### 3. Add Product with Images
```
Click: "➕ Add Product"

Step 1 - Name:
  Send: "Fresh Salmon"
  Expected: "✅ Name: Fresh Salmon\n\n💰 Please provide the price per kg (in ₹):"

Step 2 - Price:
  Send: "500"
  Expected: "✅ Price: ₹500.0/kg\n\n📄 Please provide a description:"

Step 3 - Description:
  Send: "Premium quality Atlantic salmon, rich in Omega-3"
  Expected: "✅ Description saved\n\n🔄 Is this product available? (yes/no):"

Step 4 - Availability:
  Send: "yes"
  Expected: "✅ Availability: Available\n\n📸 Send product images..."

Step 5 - Images:
  Action: Send 2-3 images of salmon
  Expected: After each image: "✅ Image X received!"

Step 6 - Finalize:
  Send: "DONE"
  Expected: "✅ Product Added Successfully! 🐟 Fresh Salmon has been added with X images."
```

### 4. View All Products
```
Click: "📋 Show All"
Expected: List of all products with image counts
```

## Customer Testing Flow

### 1. Start Customer Session
```
Send: "hi"
Expected: Welcome message in Malayalam with menu
```

### 2. Browse Products
```
Click: "Browse Products" button (Malayalam)
Expected: 
  - If products have images: Carousel appears with product cards
  - Each card shows: Image, Name, Price, Description, "Select" button
  - If products don't have images: List format
```

### 3. Select Product from Carousel
```
Click: "Select" button on any product card
Expected: "How many kg of [Product Name] would you like? (₹X.XX/kg)"
```

### 4. Enter Quantity
```
Send: "2"
Expected: Cart options menu:
  - Continue Shopping
  - Checkout
```

### 5. Complete Order
```
Click: "Checkout"
Expected: Cart summary with total
Click: "Confirm Order"
Expected: Order confirmation
```

## Testing Scenarios

### Scenario 1: Product with Multiple Images
```
Admin adds product with 3 images
Customer views catalog
Expected: Carousel card shows first image
Customer can select and order
```

### Scenario 2: Mixed Products
```
Admin adds:
  - Product A: 2 images
  - Product B: No images
  - Product C: 5 images

Customer views catalog
Expected:
  - Carousel shows Product A and C
  - List shows Product B
```

### Scenario 3: Maximum Images
```
Admin adds product with 10 images
Expected: All 10 images accepted
Customer sees first image in carousel
```

### Scenario 4: No Images
```
Admin adds product without images (types "SKIP")
Customer views catalog
Expected: Product appears in list format
```

## Verification Points

### Database Checks
```sql
-- Check products
SELECT id, name, price_per_kg, is_available FROM fish_products;

-- Check product images
SELECT id, product_id, mime_type, display_order, 
       LENGTH(image_data) as size_bytes 
FROM product_images 
ORDER BY product_id, display_order;

-- Check image counts per product
SELECT p.name, COUNT(pi.id) as image_count
FROM fish_products p
LEFT JOIN product_images pi ON p.id = pi.product_id
GROUP BY p.id, p.name;
```

### API Logs to Monitor
```
Admin Flow:
- "Processing admin message from: [Admin Name]"
- "Admin button reply: PRODUCT_SECTION"
- "Admin button reply: ADD_PRODUCT"
- Image download logs from WhatsAppMediaService

Customer Flow:
- "Fetching available fish products for customer"
- "Found X available fish products"
- Carousel message sending logs
```

## Common Issues & Solutions

### Issue 1: Images Not Displaying
**Symptom**: Customer sees list instead of carousel
**Check**: 
- Product has images in database
- Images have valid media IDs
- WhatsApp Media Service is working

### Issue 2: "Product No Longer Available"
**Symptom**: Error when selecting product
**Check**:
- Product `is_available` = true
- Product exists in database
- Product ID matches

### Issue 3: Image Upload Fails
**Symptom**: Admin can't add images
**Check**:
- WhatsApp API token is valid
- Media ID is correct
- Network connectivity to WhatsApp API

## Expected Behavior Summary

| Action | Expected Result |
|--------|----------------|
| Admin adds product with images | Images stored in DB, product created |
| Admin adds product without images | Product created, no images |
| Customer views products (with images) | Carousel displayed |
| Customer views products (no images) | List displayed |
| Customer selects from carousel | Quantity prompt appears |
| Customer selects from list | Quantity prompt appears |
| Customer adds to cart | Cart updated, options shown |

## Performance Metrics

- **Image Download**: < 3 seconds per image
- **Carousel Display**: < 2 seconds
- **Product Selection**: < 1 second
- **Database Query**: < 500ms

## Success Criteria

✅ Admin can add products with 1-10 images
✅ Images are stored correctly in database
✅ Customer sees carousel for products with images
✅ Customer sees list for products without images
✅ Customer can select products from both formats
✅ Order flow works end-to-end
✅ No errors in application logs
✅ Application runs without crashes

---

**Ready to Test!** 🚀

Start with the Admin flow to add some products with images, then test the Customer flow to see the carousel in action!
