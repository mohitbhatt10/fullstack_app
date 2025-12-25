# Restaurant Menu Feature - Quick Setup Guide

## 🚀 Quick Start (5 Minutes)

Follow these steps to get the restaurant menu feature running in your café management system.

---

## Prerequisites

✅ Java 11+  
✅ Spring Boot 2.7+  
✅ Angular 17+  
✅ MySQL 8.0+  
✅ Maven

---

## Step 1: Database Setup (2 minutes)

### Run Migration Script

```bash
cd cafe-management
mysql -u root -p cafemanagementsystem < ../db_scripts/menu_schema_updates.sql
```

Or manually execute in MySQL Workbench:

```sql
-- Add columns
ALTER TABLE product ADD COLUMN is_veg BOOLEAN DEFAULT TRUE;
ALTER TABLE category ADD COLUMN display_order INT DEFAULT 0;

-- Insert categories (see full script for all data)
INSERT INTO category (name, display_order) VALUES
('Starters (Veg)', 1),
('Starters (Non-Veg)', 2),
('Quick Bites', 3);
-- ... more categories
```

### Verify Installation

```sql
-- Check columns exist
DESCRIBE product;
DESCRIBE category;

-- View sample data
SELECT * FROM category ORDER BY display_order;
```

---

## Step 2: Backend Setup (1 minute)

### Files Created

All backend files are already created in:

```
com.inn.cafe/src/main/java/com/inn/cafe/
├── POJO/
│   ├── Product.java (modified - added isVeg)
│   └── Category.java (modified - added displayOrder)
├── wrapper/
│   ├── MenuItemWrapper.java (new)
│   ├── MenuCategoryWrapper.java (new)
│   └── MenuResponseWrapper.java (new)
├── rest/
│   └── MenuRest.java (new)
├── restImpl/
│   └── MenuRestImpl.java (new)
├── service/
│   └── MenuService.java (new)
├── serviceImpl/
│   └── MenuServiceImpl.java (new)
└── dao/
    └── ProductDao.java (modified - added menu queries)
```

### Rebuild Application

```bash
cd com.inn.cafe
mvn clean install
mvn spring-boot:run
```

### Verify Backend

```bash
# Test API endpoint
curl http://localhost:8080/menu/get

# Expected: JSON response with menu data
```

---

## Step 3: Frontend Setup (2 minutes)

### Files Created

All frontend files are created in:

```
Frontend/src/app/
├── models/
│   └── menu.model.ts (new)
├── services/
│   └── menu.service.ts (new)
├── shared/
│   └── menu-page-helper.ts (new)
└── restaurant-menu/
    ├── restaurant-menu.component.ts (new)
    ├── restaurant-menu.component.html (new)
    └── restaurant-menu.component.scss (new)
```

### Add Route to Application

Edit `app-routing.module.ts` or `dashboard.routing.ts`:

```typescript
import { RestaurantMenuComponent } from "./restaurant-menu/restaurant-menu.component";

const routes: Routes = [
  // ... existing routes
  {
    path: "menu",
    component: RestaurantMenuComponent,
  },
];
```

### Add to Navigation Menu

Edit `shared/menu-items.ts`:

```typescript
export const MENU_ITEMS = [
  // ... existing items
  {
    state: "menu",
    name: "Restaurant Menu",
    icon: "restaurant_menu",
    role: "", // Empty = accessible to all
  },
];
```

### Rebuild Frontend

```bash
cd Frontend
npm install
ng serve
```

---

## Step 4: Verify Installation

### 1. Check Backend API

Visit: http://localhost:8080/menu/get

Expected response:

```json
{
  "categories": [...],
  "totalCategories": 12,
  "totalItems": 95
}
```

### 2. Check Frontend

Visit: http://localhost:4200/menu

You should see:

- ✅ Menu header with "Our Menu" title
- ✅ Print button
- ✅ Categories displayed in order
- ✅ Items under each category
- ✅ Prices in ₹ format
- ✅ Green dots for veg items
- ✅ Red dots for non-veg items

### 3. Test Print Function

1. Click "Print Menu" button
2. Print preview should show:
   - Restaurant header
   - Menu items with proper layout
   - Page breaks at appropriate positions
   - Footer with thank you message

---

## Configuration Options

### Change Items Per Page (Print)

Edit `restaurant-menu.component.ts`:

```typescript
calculatePageBreaks() {
  const itemsPerPage = 20; // Change to 15, 25, 30, etc.
  // ...
}
```

### Change Pagination Size

Edit `restaurant-menu.component.ts`:

```typescript
pageSize: number = 30; // Change to 20, 40, 50, etc.
```

### Customize Colors

Edit `restaurant-menu.component.scss`:

```scss
.menu-header {
  background: linear-gradient(135deg, #YOUR_COLOR 0%, #YOUR_COLOR 100%);
}

.veg-indicator {
  border-color: #YOUR_VEG_COLOR;
}

.non-veg-indicator {
  border-color: #YOUR_NONVEG_COLOR;
}
```

---

## Troubleshooting

### Backend Issues

**Problem:** API returns 404  
**Solution:** Check if MenuRestImpl has @RestController annotation

**Problem:** Empty menu response  
**Solution:**

```sql
-- Ensure products have status='true'
UPDATE product SET status='true' WHERE status IS NULL;
```

### Frontend Issues

**Problem:** Menu component not found  
**Solution:** Add component to module declarations

**Problem:** API connection error  
**Solution:** Check `environment.ts` has correct API URL:

```typescript
export const environment = {
  production: false,
  apiUrl: "http://localhost:8080",
};
```

### Database Issues

**Problem:** Column 'is_veg' doesn't exist  
**Solution:** Run migration script again

**Problem:** No categories showing  
**Solution:** Insert categories:

```sql
INSERT INTO category (name, display_order) VALUES
('Starters (Veg)', 1);
-- ... more categories
```

---

## Testing Checklist

- [ ] Backend API responds to `/menu/get`
- [ ] Frontend menu component loads
- [ ] Categories display in correct order
- [ ] Items show under categories
- [ ] Veg/non-veg indicators visible
- [ ] Prices formatted correctly (₹)
- [ ] Print button works
- [ ] Print preview looks correct
- [ ] Responsive on mobile (test viewport)

---

## Sample Data

Want to start with sample data? Run the full SQL script:

```bash
mysql -u root -p cafemanagementsystem < ../db_scripts/menu_schema_updates.sql
```

This inserts 95 realistic menu items across 12 categories!

---

## Next Steps

1. **Customize Menu Items**

   - Replace sample data with your actual menu
   - Update prices to match your restaurant

2. **Add Images** (Future)

   - Add image column to product table
   - Display images in menu cards

3. **Add Search/Filter** (Future)

   - Implement search by item name
   - Filter by price range or category

4. **Enable Online Ordering** (Future)
   - Add "Add to Cart" functionality
   - Integrate with order management

---

## Support

Need help? Check:

1. **Implementation Guide**: `docs/menu/IMPLEMENTATION_GUIDE.md`
2. **API Documentation**: Section 6 in Implementation Guide
3. **Sample Data**: `docs/menu/sample-menu-data.json`

---

## Success! 🎉

You now have a fully functional restaurant menu feature with:

✅ Backend REST API  
✅ Frontend Angular component  
✅ Print support  
✅ Responsive design  
✅ Veg/Non-veg indicators  
✅ Sample data

**Access your menu at:** http://localhost:4200/menu

---

**Setup Time:** ~5 minutes  
**Version:** 1.0  
**Last Updated:** December 2025
