# Restaurant Menu Feature - Complete Implementation Guide

## Overview

This document provides a complete implementation guide for the restaurant menu feature in the Café Management System. The feature displays menu items grouped by category with support for printing, pagination, and responsive design.

---

## 📋 Table of Contents

1. [Data Model](#data-model)
2. [Backend Implementation](#backend-implementation)
3. [Frontend Implementation](#frontend-implementation)
4. [Print & PDF Support](#print--pdf-support)
5. [Database Setup](#database-setup)
6. [API Documentation](#api-documentation)
7. [Usage Instructions](#usage-instructions)
8. [Configuration](#configuration)

---

## 1. Data Model

### MenuItemWrapper (DTO)

```java
{
  "id": 101,
  "categoryId": 1,
  "categoryName": "Starters (Veg)",
  "productName": "Paneer Tikka",
  "description": "Cottage cheese cubes marinated in spices",
  "price": 280,
  "isVeg": true,
  "isActive": true
}
```

### MenuCategoryWrapper (DTO)

```java
{
  "categoryId": 1,
  "categoryName": "Starters (Veg)",
  "displayOrder": 1,
  "items": [MenuItemWrapper...]
}
```

### MenuResponseWrapper (DTO)

```java
{
  "categories": [MenuCategoryWrapper...],
  "totalCategories": 12,
  "totalItems": 95,
  "pagination": {
    "page": 0,
    "size": 20,
    "totalPages": 5,
    "totalElements": 95
  }
}
```

---

## 2. Backend Implementation

### Components Created

#### Entities (POJO)

- **Product.java** - Enhanced with `isVeg` field
- **Category.java** - Enhanced with `displayOrder` field

#### Wrappers (DTOs)

- **MenuItemWrapper.java** - Menu item data transfer object
- **MenuCategoryWrapper.java** - Category with items
- **MenuResponseWrapper.java** - Complete menu response

#### REST API

- **MenuRest.java** - REST interface
- **MenuRestImpl.java** - REST controller implementation

#### Service Layer

- **MenuService.java** - Service interface
- **MenuServiceImpl.java** - Business logic implementation

#### Data Access

- **ProductDao.java** - Enhanced with menu queries

### Named Queries Added

```java
@NamedQuery(name = "Product.getMenuItems",
  query = "select new com.inn.cafe.wrapper.MenuItemWrapper(p.id, p.category.id, p.category.name, p.name, p.description, p.price, p.isVeg) from Product p where p.status='true' order by p.category.id, p.name")

@NamedQuery(name = "Product.getMenuItemsByCategory",
  query = "select new com.inn.cafe.wrapper.MenuItemWrapper(p.id, p.category.id, p.category.name, p.name, p.description, p.price, p.isVeg) from Product p where p.category.id=:categoryId and p.status='true' order by p.name")
```

---

## 3. Frontend Implementation

### Components Created

#### Service

- **menu.service.ts** - HTTP service for menu API calls

#### Models

- **menu.model.ts** - TypeScript interfaces

#### Component

- **restaurant-menu.component.ts** - Main menu component
- **restaurant-menu.component.html** - Template
- **restaurant-menu.component.scss** - Styles

### Key Features

1. **Category Grouping** - Items automatically grouped by category
2. **Veg/Non-Veg Indicators** - Visual badges for dietary preferences
3. **Responsive Design** - Works on desktop, tablet, and mobile
4. **Print Support** - Optimized for printing and PDF generation
5. **Pagination** - Optional pagination for large menus
6. **Price Formatting** - INR (₹) symbol with proper formatting

---

## 4. Print & PDF Support

### Print CSS Features

```scss
@media print {
  @page {
    size: A4;
    margin: 15mm;
  }

  .no-print {
    display: none !important;
  }
  .print-only {
    display: block !important;
  }
  .page-break {
    page-break-before: always;
  }
}
```

### Print-Specific Elements

1. **Restaurant Header** - Displays café name and legend
2. **Page Breaks** - Intelligent page break calculation
3. **Footer** - Thank you message and disclaimers
4. **Legend** - Veg/Non-Veg indicator explanation

### Multi-Page Handling

The component includes `calculatePageBreaks()` method that:

- Configures items per page (default: 20)
- Prevents category headers from splitting from first item
- Adds page breaks at appropriate positions
- Handles large categories spanning multiple pages

---

## 5. Database Setup

### Schema Updates Required

```sql
-- Add is_veg column to product table
ALTER TABLE product
ADD COLUMN is_veg BOOLEAN DEFAULT TRUE;

-- Add display_order column to category table
ALTER TABLE category
ADD COLUMN display_order INT DEFAULT 0;
```

### Category Setup

Run the SQL script: `db_scripts/menu_schema_updates.sql`

This will:

1. Update schema with new columns
2. Insert 12 restaurant categories
3. Populate sample menu data (95 items)
4. Set display order for categories

### Categories Included

1. Starters (Veg)
2. Starters (Non-Veg)
3. Quick Bites
4. Burgers & Sandwiches
5. Pizza
6. Pasta & Noodles
7. Main Course (Veg)
8. Main Course (Non-Veg)
9. Rice & Biryani
10. Indian Breads
11. Desserts
12. Beverages (Hot & Cold)

---

## 6. API Documentation

### Endpoints

#### GET /menu/get

**Description:** Fetch complete menu with all categories and items

**Response:**

```json
{
  "categories": [...],
  "totalCategories": 12,
  "totalItems": 95,
  "pagination": null
}
```

**Status Codes:**

- 200 OK - Success
- 500 Internal Server Error - Server error

---

#### GET /menu/getByCategory?categoryId={id}

**Description:** Fetch menu items for specific category

**Parameters:**

- `categoryId` (required) - Category ID to filter

**Response:**

```json
{
  "categories": [{
    "categoryId": 1,
    "categoryName": "Starters (Veg)",
    "items": [...]
  }],
  "totalCategories": 1,
  "totalItems": 5,
  "pagination": null
}
```

**Status Codes:**

- 200 OK - Success
- 400 Bad Request - Invalid category ID
- 500 Internal Server Error - Server error

---

#### GET /menu/getPaginated?page={page}&size={size}

**Description:** Fetch paginated menu

**Parameters:**

- `page` (optional, default: 0) - Page number (0-indexed)
- `size` (optional, default: 20) - Items per page

**Response:**

```json
{
  "categories": [...],
  "totalCategories": 3,
  "totalItems": 20,
  "pagination": {
    "page": 0,
    "size": 20,
    "totalPages": 5,
    "totalElements": 95
  }
}
```

**Status Codes:**

- 200 OK - Success
- 400 Bad Request - Invalid page number
- 500 Internal Server Error - Server error

---

## 7. Usage Instructions

### Adding Menu Component to Application

1. **Import Module** (if not auto-imported):

```typescript
// app.module.ts
import { RestaurantMenuComponent } from './restaurant-menu/restaurant-menu.component';

@NgModule({
  declarations: [
    RestaurantMenuComponent
  ]
})
```

2. **Add Route**:

```typescript
// app-routing.module.ts
{
  path: 'menu',
  component: RestaurantMenuComponent
}
```

3. **Add to Navigation Menu**:

```typescript
// menu-items.ts
export const MENU_ITEMS = [
  // ...existing items
  {
    state: "menu",
    name: "Restaurant Menu",
    icon: "restaurant_menu",
    role: "",
  },
];
```

### Printing Menu

Users can print the menu by:

1. Clicking the "Print Menu" button in the component
2. Using browser print (Ctrl+P / Cmd+P)

The CSS automatically:

- Hides screen-only elements (buttons, pagination)
- Shows print-only elements (restaurant header, footer)
- Applies proper page breaks
- Formats for A4 paper size

### PDF Generation Options

#### Option 1: Browser Print to PDF

1. Click "Print Menu"
2. Select "Save as PDF" in print dialog

#### Option 2: Client-Side PDF (Future Enhancement)

Install libraries:

```bash
npm install jspdf html2canvas
```

Add method to component:

```typescript
import jsPDF from 'jspdf';
import html2canvas from 'html2canvas';

generatePDF() {
  const element = document.querySelector('.menu-content');
  html2canvas(element).then(canvas => {
    const imgData = canvas.toDataURL('image/png');
    const pdf = new jsPDF('p', 'mm', 'a4');
    pdf.addImage(imgData, 'PNG', 0, 0, 210, 297);
    pdf.save('menu.pdf');
  });
}
```

#### Option 3: Server-Side PDF (Future Enhancement)

Add dependency to pom.xml:

```xml
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itext7-core</artifactId>
    <version>7.2.5</version>
</dependency>
```

Create PDF generation service in backend.

---

## 8. Configuration

### Items Per Page (Print)

Adjust in component:

```typescript
// restaurant-menu.component.ts
calculatePageBreaks() {
  const itemsPerPage = 20; // Change this value
  // ...
}
```

### Page Size for Pagination

Adjust default:

```typescript
pageSize: number = 30; // Change default page size
```

### Price Formatting

Modify format method:

```typescript
formatPrice(price: number): string {
  return `₹${price}`; // Customize format
}
```

### Category Display Order

Update in database:

```sql
UPDATE category SET display_order = {order} WHERE name = '{name}';
```

---

## 🎨 Styling Customization

### Color Scheme

Modify in SCSS:

```scss
$primary-color: #667eea;
$secondary-color: #764ba2;
$veg-color: #22c55e;
$non-veg-color: #ef4444;
```

### Typography

```scss
$title-font-size: 2.5rem;
$category-font-size: 1.8rem;
$item-font-size: 1.2rem;
```

---

## 🔍 Testing

### Manual Testing Checklist

- [ ] Menu loads successfully
- [ ] Categories display in correct order
- [ ] Items grouped correctly by category
- [ ] Veg/Non-Veg indicators display correctly
- [ ] Prices formatted as INR (₹)
- [ ] Print preview looks correct
- [ ] Page breaks occur at appropriate positions
- [ ] Responsive on mobile devices
- [ ] Category filter works
- [ ] Pagination works (if enabled)

### API Testing (using curl or Postman)

```bash
# Get complete menu
curl -X GET http://localhost:8080/menu/get

# Get category menu
curl -X GET "http://localhost:8080/menu/getByCategory?categoryId=1"

# Get paginated menu
curl -X GET "http://localhost:8080/menu/getPaginated?page=0&size=20"
```

---

## 📊 Performance Considerations

### Database Optimization

1. **Index on category_fk**:

```sql
CREATE INDEX idx_product_category ON product(category_fk);
```

2. **Index on status**:

```sql
CREATE INDEX idx_product_status ON product(status);
```

### Frontend Optimization

1. **Lazy Loading** - Load component only when needed
2. **Caching** - Cache menu data in service
3. **Virtual Scrolling** - For very large menus (100+ items)

---

## 🔧 Troubleshooting

### Issue: Menu not loading

**Solution:** Check:

1. Backend service is running
2. Database connection is active
3. Products have `status='true'`
4. Console for API errors

### Issue: Veg indicator not showing

**Solution:**

1. Ensure `is_veg` column exists in database
2. Check that products have `is_veg` value set
3. Verify CSS for veg-indicator class

### Issue: Print layout broken

**Solution:**

1. Clear browser cache
2. Check @media print styles
3. Test in different browsers
4. Verify page-break CSS

---

## 🚀 Future Enhancements

1. **Search & Filter**

   - Search by item name
   - Filter by price range
   - Filter by dietary preferences

2. **Multi-language Support**

   - Hindi translations
   - Regional language support

3. **Images**

   - Product images
   - Category banners

4. **Advanced PDF**

   - Custom PDF templates
   - Logo and branding
   - QR codes for online ordering

5. **Analytics**
   - Track most viewed items
   - Popular categories
   - Print statistics

---

## 📝 Notes

- All prices are in INR (Indian Rupees)
- Menu displays only active products (`status='true'`)
- Categories without products are not displayed
- Default pagination size is 20 items
- Print layout optimized for A4 paper

---

## 👥 Support

For issues or questions:

1. Check this documentation
2. Review sample data in `docs/menu/sample-menu-data.json`
3. Test API endpoints manually
4. Check browser console for errors

---

## ✅ Deployment Checklist

Before deploying to production:

- [ ] Run database migration script
- [ ] Populate categories with correct display order
- [ ] Add actual menu items with accurate data
- [ ] Test all API endpoints
- [ ] Verify print layout
- [ ] Test on multiple devices
- [ ] Check performance with full menu (80-120 items)
- [ ] Configure CORS if needed
- [ ] Set up logging for menu access
- [ ] Document any environment-specific configuration

---

**Version:** 1.0  
**Last Updated:** December 2025  
**Author:** Senior Full-Stack Engineer
