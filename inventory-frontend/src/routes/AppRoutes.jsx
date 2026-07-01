import { Routes, Route, Navigate } from 'react-router-dom';
import MainLayout from '../layouts/MainLayout';
import Login from '../pages/Login';
import Dashboard from '../pages/Dashboard';
import Products from '../pages/Products';
import Users from '../pages/Users';
import StockIn from '../pages/StockIn';
import StockOut from '../pages/StockOut';
import Reports from '../pages/Reports';
import Profile from '../pages/Profile';
import ProfileEdit from '../pages/ProfileEdit';
import PrivateRoute from './PrivateRoute';
import { useAuth } from '../context/AuthContext';

const AppRoutes = () => {
  const { user } = useAuth();
  const role = user?.role || localStorage.getItem('role');

  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route element={<MainLayout />}>
        <Route element={<PrivateRoute />}>
          <Route path="/" element={<Dashboard />} />
          <Route path="/products" element={<Products />} />
          <Route path="/users" element={
            role === 'SUPER_ADMIN' ? <Users /> : <Navigate to="/" replace />
          } />
          <Route path="/stock-in" element={<StockIn />} />
          <Route path="/stock-out" element={<StockOut />} />
          <Route path="/reports" element={<Reports />} />
          <Route path="/profile" element={<Profile />} />
          <Route path="/profile/edit" element={<ProfileEdit />} />
        </Route>
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
};

export default AppRoutes;
