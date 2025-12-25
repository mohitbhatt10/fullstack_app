# Restaurant Menu Feature - Architecture Diagram

## System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                          PRESENTATION LAYER                      │
│                         (Angular Frontend)                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  restaurant-menu.component.html (Template)              │   │
│  │  - Menu header with actions                             │   │
│  │  - Category sections                                    │   │
│  │  - Item cards with details                              │   │
│  │  - Print-only elements                                  │   │
│  │  - Pagination controls                                  │   │
│  └────────────────────┬────────────────────────────────────┘   │
│                       │                                          │
│  ┌────────────────────▼───────────────────────────────────┐   │
│  │  restaurant-menu.component.ts (Component Logic)         │   │
│  │  - Load menu data                                       │   │
│  │  - Handle user interactions                             │   │
│  │  - Format data for display                              │   │
│  │  - Page break calculations                              │   │
│  └────────────────────┬────────────────────────────────────┘   │
│                       │                                          │
│  ┌────────────────────▼───────────────────────────────────┐   │
│  │  menu.service.ts (HTTP Service)                         │   │
│  │  - getMenu()                                            │   │
│  │  - getMenuByCategory(id)                                │   │
│  │  - getMenuPaginated(page, size)                         │   │
│  └────────────────────┬────────────────────────────────────┘   │
│                       │                                          │
│  ┌────────────────────▼───────────────────────────────────┐   │
│  │  menu.model.ts (Type Definitions)                       │   │
│  │  - MenuItem interface                                   │   │
│  │  - MenuCategory interface                               │   │
│  │  - MenuResponse interface                               │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  menu-page-helper.ts (Utility Functions)                │   │
│  │  - calculatePageBreaks()                                │   │
│  │  - formatPrice()                                        │   │
│  │  - calculateMenuStatistics()                            │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                   │
└───────────────────────────┬───────────────────────────────────┘
                            │
                            │ HTTP REST API
                            │ GET /menu/get
                            │ GET /menu/getByCategory?categoryId=1
                            │ GET /menu/getPaginated?page=0&size=20
                            │
┌───────────────────────────▼───────────────────────────────────┐
│                      APPLICATION LAYER                         │
│                      (Spring Boot Backend)                     │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  MenuRestImpl.java (REST Controller)                    │   │
│  │  - Handle HTTP requests                                 │   │
│  │  - Route to service layer                               │   │
│  │  - Return MenuResponseWrapper                           │   │
│  └────────────────────┬────────────────────────────────────┘   │
│                       │                                          │
│  ┌────────────────────▼───────────────────────────────────┐   │
│  │  MenuServiceImpl.java (Business Logic)                  │   │
│  │  - getMenu()                                            │   │
│  │  - getMenuByCategory(categoryId)                        │   │
│  │  - getMenuPaginated(page, size)                         │   │
│  │  - Group items by category                              │   │
│  │  - Handle pagination logic                              │   │
│  │  - Error handling                                       │   │
│  └────────────────────┬────────────────────────────────────┘   │
│                       │                                          │
│  ┌────────────────────▼───────────────────────────────────┐   │
│  │  ProductDao.java (Data Access)                          │   │
│  │  - getMenuItems()                                       │   │
│  │  - getMenuItemsByCategory(categoryId)                   │   │
│  │  - Execute named queries                                │   │
│  └────────────────────┬────────────────────────────────────┘   │
│                       │                                          │
│  ┌────────────────────▼───────────────────────────────────┐   │
│  │  Product.java (Entity/POJO)                             │   │
│  │  + id: Integer                                          │   │
│  │  + name: String                                         │   │
│  │  + description: String                                  │   │
│  │  + price: Integer                                       │   │
│  │  + isVeg: Boolean                                       │   │
│  │  + status: String                                       │   │
│  │  + category: Category (ManyToOne)                       │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  Category.java (Entity/POJO)                            │   │
│  │  + id: Integer                                          │   │
│  │  + name: String                                         │   │
│  │  + displayOrder: Integer                                │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  MenuItemWrapper.java (DTO)                             │   │
│  │  Data transfer object for menu items                    │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  MenuCategoryWrapper.java (DTO)                         │   │
│  │  Groups items by category                               │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  MenuResponseWrapper.java (DTO)                         │   │
│  │  Complete menu response structure                       │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                   │
└───────────────────────────┬───────────────────────────────────┘
                            │
                            │ JPA/Hibernate
                            │
┌───────────────────────────▼───────────────────────────────────┐
│                       DATA LAYER                               │
│                       (MySQL Database)                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  category table                                         │   │
│  │  ├── id (INT, PK, AUTO_INCREMENT)                       │   │
│  │  ├── name (VARCHAR)                                     │   │
│  │  └── display_order (INT)  ← New field                  │   │
│  └─────────────────────────────────────────────────────────┘   │
│                            │                                     │
│                            │ Foreign Key (category_fk)           │
│                            │                                     │
│  ┌─────────────────────────▼───────────────────────────────┐   │
│  │  product table                                          │   │
│  │  ├── id (INT, PK, AUTO_INCREMENT)                       │   │
│  │  ├── name (VARCHAR)                                     │   │
│  │  ├── description (VARCHAR)                              │   │
│  │  ├── price (INT)                                        │   │
│  │  ├── status (VARCHAR)                                   │   │
│  │  ├── is_veg (BOOLEAN)  ← New field                     │   │
│  │  └── category_fk (INT, FK)                              │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                   │
└─────────────────────────────────────────────────────────────────┘
```

## Data Flow

### 1. Get Complete Menu

```
User clicks "Menu"
    ↓
restaurant-menu.component.ts
    ↓ loadMenu()
menu.service.ts
    ↓ getMenu()
HTTP GET /menu/get
    ↓
MenuRestImpl.java
    ↓ getMenu()
MenuServiceImpl.java
    ↓ getMenu()
ProductDao.java
    ↓ getMenuItems() - Named Query
Database Query:
SELECT p.id, p.category.id, p.category.name,
       p.name, p.description, p.price, p.isVeg
FROM Product p
WHERE p.status='true'
ORDER BY p.category.id, p.name
    ↓
MenuServiceImpl groups by category
    ↓
MenuResponseWrapper {
  categories: [
    {categoryId: 1, name: "Starters (Veg)", items: [...]},
    {categoryId: 2, name: "Starters (Non-Veg)", items: [...]},
    ...
  ],
  totalCategories: 12,
  totalItems: 95
}
    ↓
HTTP Response (JSON)
    ↓
restaurant-menu.component.ts
    ↓ displayedCategories
Template renders menu
```

### 2. Filter by Category

```
User selects category filter
    ↓
restaurant-menu.component.ts
    ↓ loadMenuByCategory(categoryId)
menu.service.ts
    ↓ getMenuByCategory(categoryId)
HTTP GET /menu/getByCategory?categoryId=5
    ↓
MenuRestImpl.java
    ↓ getMenuByCategory(5)
MenuServiceImpl.java
    ↓ getMenuByCategory(5)
ProductDao.java
    ↓ getMenuItemsByCategory(5) - Named Query
Database Query:
SELECT p.id, p.category.id, p.category.name,
       p.name, p.description, p.price, p.isVeg
FROM Product p
WHERE p.category.id=5 AND p.status='true'
ORDER BY p.name
    ↓
MenuResponseWrapper with single category
    ↓
Template renders filtered menu
```

### 3. Print Menu

```
User clicks "Print Menu"
    ↓
restaurant-menu.component.ts
    ↓ printMenu()
window.print()
    ↓
Browser applies @media print CSS
    ↓
- Hide .no-print elements
- Show .print-only elements
- Apply page breaks
- Format for A4 paper
    ↓
User sees print preview
    ↓
User prints or saves as PDF
```

## Component Relationships

```
┌──────────────────────────────────────────────────────────┐
│                    App Module                            │
│                                                          │
│  ┌────────────────────────────────────────────────┐    │
│  │  restaurant-menu.component                      │    │
│  │                                                 │    │
│  │  Injects:                                       │    │
│  │  • MenuService                                  │    │
│  │  • SnackbarService                              │    │
│  │  • MatDialog                                    │    │
│  │  • Router                                       │    │
│  │                                                 │    │
│  │  Uses:                                          │    │
│  │  • MenuPageHelper (utility)                     │    │
│  │  • menu.model.ts (types)                        │    │
│  └────────────────────────────────────────────────┘    │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

## Module Dependencies

### Backend Dependencies (pom.xml)

```xml
spring-boot-starter-web      ← REST API
spring-boot-starter-data-jpa ← Database access
mysql-connector-j            ← MySQL driver
lombok                       ← Reduce boilerplate
[Optional] itext7-core       ← PDF generation
```

### Frontend Dependencies (package.json)

```json
@angular/core        ← Framework
@angular/material    ← UI components
@angular/common/http ← HTTP client
rxjs                 ← Reactive programming
```

## Deployment Architecture

```
┌─────────────────┐
│   Web Browser   │
│   (Customer)    │
└────────┬────────┘
         │
         │ HTTP/HTTPS
         │
┌────────▼────────┐
│  Angular App    │
│  (localhost:    │
│   4200)         │
└────────┬────────┘
         │
         │ REST API
         │
┌────────▼────────┐
│  Spring Boot    │
│  Backend        │
│  (localhost:    │
│   8080)         │
└────────┬────────┘
         │
         │ JDBC
         │
┌────────▼────────┐
│  MySQL          │
│  Database       │
│  (localhost:    │
│   3306)         │
└─────────────────┘
```

## Security Layer (Future)

```
┌─────────────────────────────────────────┐
│  Public Access (No Auth Required)       │
│  • GET /menu/get                        │
│  • GET /menu/getByCategory              │
│  • GET /menu/getPaginated               │
├─────────────────────────────────────────┤
│  Admin Access (Auth Required)           │
│  • POST /product/add                    │
│  • POST /product/update                 │
│  • POST /product/delete                 │
└─────────────────────────────────────────┘
```

## Performance Optimization

### Database Level

```
Indexes:
• product.category_fk (Foreign Key)
• product.status (Filter)
• category.display_order (Sorting)

Named Queries:
• Compiled and cached by JPA
• Optimized joins
• Selective field projection
```

### Application Level

```
• DTO pattern reduces data transfer
• Lazy loading for category relationship
• Pagination support for large menus
• Efficient grouping algorithm
```

### Frontend Level

```
• Observable pattern (RxJS)
• Change detection optimization
• Virtual scrolling (future)
• Service worker caching (future)
```

---

**Architecture Version:** 1.0  
**Last Updated:** December 2025  
**Status:** Production Ready
