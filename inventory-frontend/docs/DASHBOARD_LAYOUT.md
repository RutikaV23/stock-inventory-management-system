# Dashboard Layout Implementation

## Overview

The dashboard layout provides the authenticated application shell — a persistent dark blue sidebar, a top navbar, and a main content area. This layout wraps all protected pages and is the foundation for the entire inventory management UI.

---

## Architecture

```
MainLayout (flex row, full viewport height)
├── Sidebar
│   ├── Desktop: permanent drawer (toggle 260px / 72px mini)
│   └── Mobile:  temporary drawer overlay
│
└── Right Panel (flex column, flex: 1)
    ├── Navbar (sticky AppBar, 64px)
    └── Main Content (scrollable, <Outlet />)
```

---

## Files Created / Modified

### New Files

| File | Purpose |
|---|---|
| `src/components/layout/Sidebar.jsx` | Dark blue sidebar — menu, user info, logout |
| `src/components/layout/Navbar.jsx` | Sticky top bar — toggle, page title, date, avatar |
| `src/components/common/PageHeader.jsx` | Reusable page title + subtitle + action slot |
| `src/components/common/ComingSoon.jsx` | Placeholder component for unimplemented pages |
| `src/pages/Products.jsx` | Coming Soon placeholder |
| `src/pages/StockIn.jsx` | Coming Soon placeholder |
| `src/pages/StockOut.jsx` | Coming Soon placeholder |
| `src/pages/Reports.jsx` | Coming Soon placeholder |

### Modified Files

| File | Change |
|---|---|
| `src/layouts/MainLayout.jsx` | Full rewrite: Sidebar + Navbar + `<Outlet />` flex layout |
| `src/pages/Dashboard.jsx` | Welcome card with user name from AuthContext |
| `src/pages/Profile.jsx` | Updated to use PageHeader + ComingSoon |
| `src/pages/ChangePassword.jsx` | Updated to use PageHeader + ComingSoon |
| `src/routes/AppRoutes.jsx` | Added routes: `/products`, `/stock-in`, `/stock-out`, `/reports` |
| `src/theme.js` | Added Paper shadow and Drawer border overrides |

---

## Sidebar (`src/components/layout/Sidebar.jsx`)

### States

| State | Description |
|---|---|
| **Expanded** (desktop) | 260px wide, shows icons + labels + user details |
| **Collapsed** (desktop) | 72px mini mode, icons only with tooltips |
| **Mobile drawer** | Temporary overlay, 260px, auto-closes on nav |

### Menu Items (7)

| Label | Icon | Route |
|---|---|---|
| Dashboard | `<DashboardIcon />` | `/` |
| Products | `<InventoryIcon />` | `/products` |
| Stock In | `<ArrowCircleDownIcon />` | `/stock-in` |
| Stock Out | `<ArrowCircleUpIcon />` | `/stock-out` |
| Reports | `<AssessmentIcon />` | `/reports` |
| Profile | `<PersonIcon />` | `/profile` |
| Change Password | `<LockIcon />` | `/change-password` |

### Active Menu Highlighting

- `location.pathname === item.path` determines active state
- Active item gets: blue icon (`#42a5f5`), semi-transparent blue background, white text
- Hover: background lightens slightly, text turns white

### User Info (Bottom Section)

- Avatar with initials (`user.firstName[0] + user.lastName[0]`)
- Full name
- Role (underscores replaced with spaces)
- Logout button — calls `useAuth().logout()` then `navigate('/login')`

### Logout Flow

1. Click logout button
2. `await logout()` — calls `POST /auth/logout` with refreshToken
3. Clears localStorage (accessToken, refreshToken, user, role)
4. Sets `user = null` in AuthContext
5. Navigates to `/login`

---

## Navbar (`src/components/layout/Navbar.jsx`)

### Features

- **Position**: sticky top (64px height)
- **Toggle button**: hamburger menu icon — triggers sidebar collapse (desktop) or drawer open (mobile)
- **Page title**: derived from route path via static map:

```js
const pageTitles = {
  '/': 'Dashboard',
  '/products': 'Products',
  '/stock-in': 'Stock In',
  '/stock-out': 'Stock Out',
  '/reports': 'Reports',
  '/profile': 'Profile',
  '/change-password': 'Change Password',
};
```

- **Current date**: formatted as `Wed, Jun 30, 2026` using `toLocaleDateString`
- **User avatar + name**: from AuthContext (`user.firstName`, `user.lastName`)

### Responsive

- On mobile (`< sm`): user name and date hidden, only avatar and hamburger visible

---

## Routing (`src/routes/AppRoutes.jsx`)

```
/login                  → Login.jsx (public)
/                       → MainLayout → PrivateRoute → Dashboard.jsx
/products               → MainLayout → PrivateRoute → Products.jsx (Coming Soon)
/stock-in               → MainLayout → PrivateRoute → StockIn.jsx (Coming Soon)
/stock-out              → MainLayout → PrivateRoute → StockOut.jsx (Coming Soon)
/reports                → MainLayout → PrivateRoute → Reports.jsx (Coming Soon)
/profile                → MainLayout → PrivateRoute → Profile.jsx (Coming Soon)
/change-password        → MainLayout → PrivateRoute → ChangePassword.jsx (Coming Soon)
*                       → Redirect to /
```

All protected routes are wrapped in `PrivateRoute` which checks `isAuthenticated` from AuthContext.

---

## Responsive Behavior

| Breakpoint | Sidebar | Navbar |
|---|---|---|
| **Desktop** (md+, ≥900px) | Permanent drawer, toggleable expanded/collapsed | Full: toggle + title + date + user |
| **Tablet** (sm, 600-899px) | Permanent drawer, toggleable expanded/collapsed | Full: toggle + title + date + user |
| **Mobile** (xs, <600px) | Hidden, hamburger opens temporary drawer overlay | Compact: toggle + title + avatar only |

---

## Theme (`src/theme.js`)

- `primary.main`: `#1565c0` (blue)
- Sidebar background: `#1a2035` (dark blue, inline in Sidebar component)
- Content background: `#f5f5f5`
- Card shadows: `0 2px 12px rgba(0,0,0,0.04)` (MuiPaper override)
- Drawer border: `none` (MuiDrawer override)
- Border radius: `8px` (global shape)

---

## Data Flow

```
AuthContext (user, logout)
    ├── passed to Sidebar → avatar, name, role, logout handler
    └── passed to Navbar → user name, avatar initials

useLocation() (react-router)
    ├── passed to Sidebar → active menu highlighting
    └── passed to Navbar → page title from route map

Sidebar state (sidebarOpen, mobileOpen)
    └── managed in MainLayout, passed down to Sidebar + Navbar
```

---

## Verification

- ✅ `npm run build` — passes
- ✅ `npm run lint` — passes (0 errors, 0 warnings)
- ✅ `npm run dev` — starts successfully
- ✅ Login redirects to dashboard layout
- ✅ Sidebar navigation works to all routes
- ✅ Active menu highlights correctly
- ✅ Sidebar collapse/expand works on desktop
- ✅ Mobile drawer opens/closes
- ✅ User name and role display from AuthContext
- ✅ Logout clears session and redirects to login
- ✅ Unauthenticated users cannot access protected routes

---

## Assumptions

- User object from AuthContext always has `firstName`, `lastName`, and `role`
- Role string uses underscores (e.g., `SUPER_ADMIN`) which are converted to spaces for display
- All future pages will use `PageHeader` for consistent headings
- Placeholder pages will be replaced with full implementations

---

## Next Module Recommended

**Products** — Build as the next module since:
1. It is the core entity of the inventory system
2. Establishes the CRUD pattern (data table, search, pagination, form dialogs)
3. Stock In / Stock Out depend on products
4. Reports aggregate product data

The Products module can reuse `PageHeader`, will slot into the existing sidebar navigation at `/products`, and is already protected by `PrivateRoute`.
