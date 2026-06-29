# Inventory Frontend — Folder Structure

```
src
├── api/              # API client setup and endpoint modules
│   ├── axios.js      # Axios instance with base config
│   └── authApi.js    # Auth-related API calls (login, logout, me)
├── assets/           # Static assets (images, icons, etc.)
├── components/       # Reusable UI components
│   ├── common/       # Shared components (buttons, inputs, modals, etc.)
│   └── layout/       # Layout-specific components (sidebar, header, etc.)
├── context/          # React context providers
│   └── AuthContext.jsx  # Authentication context + useAuth hook
├── layouts/          # Page-level layout wrappers
│   └── MainLayout.jsx  # Default authenticated layout with <Outlet />
├── pages/            # Route-level page components
│   ├── Login.jsx
│   ├── Dashboard.jsx
│   ├── Profile.jsx
│   └── ChangePassword.jsx
├── routes/           # Routing configuration
│   ├── AppRoutes.jsx    # All application routes
│   └── PrivateRoute.jsx # Auth guard wrapper
├── utils/            # Utility/helper functions
├── App.jsx           # Root component (renders AppRoutes)
└── main.jsx          # Entry point (BrowserRouter + AuthContext)
```
