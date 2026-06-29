# Products Module Implementation

## Overview

The Products module provides full CRUD functionality for managing inventory products. It integrates with the Spring Boot backend and follows the existing dashboard design system.

---

## Architecture

```
Products Page
├── PageHeader (title + subtitle + Add button)
├── Search Bar (product name search)
├── ProductTable (data grid with pagination)
├── ProductFormDialog (add / edit modal)
├── DeleteConfirmDialog (delete confirmation)
└── Snackbar (success/error notifications)
```

---

## Files Created

| File | Purpose |
|---|---|
| `src/api/productApi.js` | Product API service (5 endpoints) |
| `src/components/products/ProductTable.jsx` | Reusable product table with pagination |
| `src/components/products/ProductFormDialog.jsx` | Add/Edit product dialog with validation |
| `src/components/products/DeleteConfirmDialog.jsx` | Delete confirmation dialog |
| `docs/PRODUCTS_MODULE.md` | This file |

## Files Modified

| File | Change |
|---|---|
| `src/pages/Products.jsx` | Full CRUD implementation, replaces ComingSoon placeholder |

---

## Product APIs Integrated

| Endpoint | Method | Function | Description |
|---|---|---|---|
| `GET /products?page=&size=&search=` | GET | `getProducts(params)` | List with pagination + search |
| `GET /products/:id` | GET | `getProductById(id)` | Single product |
| `POST /products` | POST | `createProduct(data)` | Create product |
| `PUT /products/:id` | PUT | `updateProduct(id, data)` | Update product |
| `DELETE /products/:id` | DELETE | `deleteProduct(id)` | Delete product |

All API calls go through the existing Axios instance which handles auth headers and token refresh automatically.

---

## CRUD Functionality

### Create
1. Click **Add Product** button in PageHeader
2. `ProductFormDialog` opens in "Add" mode (empty form)
3. Fill in: Product Name *, Quantity *, Price Per Unit *, Description
4. Click **Save** → `POST /products` → Snackbar "Product created successfully" → table refreshes

### Read
- On page load: `GET /products?page=0&size=10` → populates `ProductTable`
- Server-side pagination: page and size sent as query params
- Search: `GET /products?search=term` filters by product name

### Update
1. Click **Edit** icon on any row
2. `ProductFormDialog` opens in "Edit" mode (pre-filled form)
3. Modify fields → Click **Save** → `PUT /products/:id` → Snackbar "Product updated successfully" → table refreshes

### Delete
1. Click **Delete** icon on any row
2. `DeleteConfirmDialog` opens: "Are you sure you want to delete [name]?"
3. Click **Delete** → `DELETE /products/:id` → Snackbar "Product deleted successfully" → table refreshes
4. Click **Cancel** → dialog closes, no action

---

## Search Implementation

- **Type**: Server-side search via `search` query parameter
- **Field**: Product Name
- **Trigger**: Click Search button or press Enter
- **Clear**: Click Clear button → resets search and reloads all products
- **Empty result**: Shows "No products match your search" with option to clear search

---

## Pagination Implementation

- **Type**: Server-side pagination via `page` (0-indexed) and `size` query params
- **Component**: MUI `TablePagination`
- **Rows per page**: 5, 10 (default), 25, 50
- **Navigation**: Previous, Next, page numbers
- **Total count**: Displayed from `totalElements` in API response

---

## Components

### ProductTable
- **Props**: `products`, `total`, `page`, `rowsPerPage`, `loading`, `onPageChange`, `onRowsPerPageChange`, `onView`, `onEdit`, `onDelete`
- **States**: Loading spinner, empty (delegated to parent), populated table
- **Columns**: Product Name, Quantity, Remaining, Price/Unit, Description, Created Date, Actions
- **Actions**: View (opens edit dialog), Edit (opens edit dialog), Delete (opens confirmation)
- **Price format**: USD currency formatting via `Intl.NumberFormat`
- **Date format**: `Jan 15, 2026` style via `toLocaleDateString`

### ProductFormDialog
- **Props**: `open`, `onClose`, `onSave`, `product` (null = add mode), `loading`
- **Fields**: Product Name *, Quantity *, Price Per Unit *, Description
- **Validation**:
  - Product Name: required
  - Quantity: required, must be >= 0
  - Price Per Unit: required, must be >= 0
  - Description: optional
- **States**: Add mode (empty form), Edit mode (pre-filled), saving (disabled + spinner)

### DeleteConfirmDialog
- **Props**: `open`, `onClose`, `onConfirm`, `productName`, `loading`
- **Content**: Warning icon + confirmation message with product name
- **States**: Open, deleting (disabled + spinner)

---

## Loading States

| State | Component | Visual |
|---|---|---|
| Initial load | ProductTable | Centered `CircularProgress` |
| Saving form | ProductFormDialog | Button shows `CircularProgress(20px)`, button + Cancel disabled |
| Deleting | DeleteConfirmDialog | Delete button shows `CircularProgress(20px)`, buttons disabled |

---

## Error Handling

| Scenario | Behavior |
|---|---|
| Backend validation error | Show API error message in Snackbar (top-right, red) |
| Network error | "Unable to connect to server" Snackbar |
| Create success | "Product created successfully" Snackbar (top-right, green) |
| Update success | "Product updated successfully" Snackbar |
| Delete success | "Product deleted successfully" Snackbar |

All Snackbars auto-dismiss after 4 seconds using `Snackbar + Alert(variant="filled")`.

---

## Empty State

When no products exist:
- Centered icon (`Inventory2Outlined`)
- "No products found" heading
- "Get started by adding your first product." message
- **Add Product** button in the empty state card

When search returns no results:
- "No products match your search"
- "Try a different search term." message
- **Clear** button to reset search

---

## Data Flow

```
User action → Page component → API call → Backend → Response
                                              ↓
Page component ← setState ← response data ←──┘
      ↓
   Snackbar notification (success/error)
   Table re-render with new data
```

---

## Responsive Behavior

| Breakpoint | Table |
|---|---|
| Desktop (md+) | Full table with all columns |
| Tablet (sm) | Full table, horizontal scroll if needed |
| Mobile (xs) | Horizontal scroll on table container |

The form dialog is full-width on mobile (`maxWidth="sm"` with `fullWidth`).

---

## Verification

- ✅ `npm run build` — passes
- ✅ `npm run lint` — passes (0 errors, 0 warnings)
- ✅ Product CRUD: Create, Read, Update, Delete
- ✅ Search by product name
- ✅ Pagination with rows per page selector
- ✅ Form validation (name, quantity, price)
- ✅ Loading states on initial load, save, and delete
- ✅ Success/error snackbar notifications
- ✅ Empty state when no products
- ✅ Empty search results state
- ✅ Dialog open/close behavior
- ✅ Auth integration (tokens via existing Axios interceptor)
- ✅ All existing features continue working (auth, layout, sidebar, routing)

---

## Assumptions

- API base path: `/products` under the existing `VITE_API_URL` (e.g., `http://localhost:5000/api/v1/products`)
- API response format: `{ success: boolean, message: string, data: { content: [], totalElements: number, ... } }`
- Product fields map: `productName` (or `name`), `quantity`, `remainingQuantity`, `pricePerUnit` (or `price`), `description`, `createdAt` (or `createdDate`)
- Backend supports pagination params: `page` (0-indexed), `size`, `search`
- Currency: USD

---

## Future Modules That Can Reuse This Pattern

| Module | Reusable Components |
|---|---|
| **Stock In** | `PageHeader`, `Snackbar`, empty state pattern, pagination pattern |
| **Stock Out** | `PageHeader`, `Snackbar`, empty state pattern, pagination pattern |
| **Reports** | `PageHeader`, `Snackbar` |
| **Users / Roles** | `ProductTable` → adapt to `UsersTable`, `ProductFormDialog` → adapt to `UserFormDialog`, `DeleteConfirmDialog` directly reusable |
