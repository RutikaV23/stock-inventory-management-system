# Inventory Frontend — Folder Structure

```
src
├── api/                    # API client setup and endpoint modules
│   ├── axios.js            # Axios instance with interceptors (auth, refresh)
│   └── authApi.js          # Auth API calls (login, logout, refresh, profile)
│
├── assets/                 # Static assets (images, icons, etc.)
│
├── components/             # Reusable UI components
│   ├── common/             # Shared components
│   │   ├── ComingSoon.jsx  # Placeholder for unimplemented pages
│   │   └── PageHeader.jsx  # Page title + subtitle + action slot
│   └── layout/             # Layout-specific components
│       ├── Sidebar.jsx     # Dark blue sidebar with menu, user info, logout
│       └── Navbar.jsx      # Top bar with toggle, page title, date, avatar
│
├── context/                # React context providers
│   └── AuthContext.jsx     # AuthProvider + useAuth hook (login, logout, session)
│
├── layouts/                # Page-level layout wrappers
│   └── MainLayout.jsx     # Authenticated layout: Sidebar + Navbar + Outlet
│
├── pages/                  # Route-level page components
│   ├── Login.jsx           # Login page (MUI, validation, snackbar errors)
│   ├── Dashboard.jsx       # Dashboard with welcome message
│   ├── Products.jsx        # Coming Soon
│   ├── StockIn.jsx         # Coming Soon
│   ├── StockOut.jsx        # Coming Soon
│   ├── Reports.jsx         # Coming Soon
│   ├── Profile.jsx         # Coming Soon
│   └── ChangePassword.jsx  # Coming Soon
│
├── routes/                 # Routing configuration
│   ├── AppRoutes.jsx       # All application routes
│   └── PrivateRoute.jsx    # Auth guard wrapper
│
├── utils/                  # Utility/helper functions
│
├── theme.js                # MUI theme (palette, typography, components)
├── App.jsx                 # Root component (loading state + AppRoutes)
├── main.jsx                # Entry point (ThemeProvider + BrowserRouter + AuthProvider)
└── README.md               # This file
```
