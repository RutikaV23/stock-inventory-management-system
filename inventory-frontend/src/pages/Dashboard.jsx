import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Paper,
  Typography,
  Button,
  CircularProgress,
} from '@mui/material';
import {
  Inventory2Outlined,
  WarehouseOutlined,
  CheckCircle,
  WarningAmberOutlined,
  Error as ErrorIcon,
  AttachMoneyOutlined,
  Add,
  Login,
  Logout,
  AssessmentOutlined,
} from '@mui/icons-material';
import { getProducts } from '../api/productApi';
import formatCurrency from '../utils/formatCurrency';
import StatCard from '../components/dashboard/StatCard';
import InventoryChart from '../components/dashboard/InventoryChart';
import StockStatusChart from '../components/dashboard/StockStatusChart';
import RecentProductsCard from '../components/dashboard/RecentProductsCard';
import LowStockCard from '../components/dashboard/LowStockCard';

const icons = {
  total: <Inventory2Outlined />,
  totalStock: <WarehouseOutlined />,
  available: <CheckCircle />,
  lowStock: <WarningAmberOutlined />,
  outOfStock: <ErrorIcon />,
  value: <AttachMoneyOutlined />,
};

const Dashboard = () => {
  const navigate = useNavigate();
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const { data } = await getProducts({ page: 0, size: 200 });
        const all = data.data?.content || [];

        const totalProducts = all.length;
        const totalStockQty = all.reduce((sum, p) => sum + (p.stockQuantity || 0), 0);
        const availableQty = all
          .filter((p) => p.status === 'ACTIVE')
          .reduce((sum, p) => sum + (p.stockQuantity || 0), 0);
        const lowStockCount = all.filter(
          (p) => p.stockQuantity != null && p.minimumStock != null && p.stockQuantity <= p.minimumStock
        ).length;
        const outOfStockCount = all.filter((p) => p.stockQuantity === 0 || p.stockQuantity == null).length;
        const totalValue = all.reduce(
          (sum, p) => sum + (p.price || 0) * (p.stockQuantity || 0),
          0
        );

        setStats({
          totalProducts,
          totalStockQty,
          availableQty,
          lowStockCount,
          outOfStockCount,
          totalValue,
        });
      } catch {
        setStats({
          totalProducts: 0,
          totalStockQty: 0,
          availableQty: 0,
          lowStockCount: 0,
          outOfStockCount: 0,
          totalValue: 0,
        });
      } finally {
        setLoading(false);
      }
    };
    fetchStats();
  }, []);

  const quickActions = [
    { label: 'Add Product', icon: <Add />, path: '/products', color: 'primary' },
    { label: 'Stock In', icon: <Login />, path: '/stock-in', color: 'success' },
    { label: 'Stock Out', icon: <Logout />, path: '/stock-out', color: 'warning' },
    { label: 'View Reports', icon: <AssessmentOutlined />, path: '/reports', color: 'info' },
  ];

  return (
    <>
      <Box>
        <Typography variant="h5" fontWeight={700} sx={{ mb: 0.5 }}>
          Dashboard
        </Typography>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
          Overview of your inventory
        </Typography>
      </Box>

      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
          <CircularProgress />
        </Box>
      ) : (
        <>
          <Box
            sx={{
              display: 'flex',
              flexWrap: 'wrap',
              gap: 2.5,
              mb: 4,
            }}
          >
            <StatCard
              icon={icons.total}
              title="Total Products"
              value={stats.totalProducts}
              description="All products"
              color="#1976d2"
            />
            <StatCard
              icon={icons.totalStock}
              title="Total Stock"
              value={stats.totalStockQty.toLocaleString()}
              description="Units in stock"
              color="#388e3c"
            />
            <StatCard
              icon={icons.available}
              title="Available Qty"
              value={stats.availableQty.toLocaleString()}
              description="Active products"
              color="#0288d1"
            />
            <StatCard
              icon={icons.lowStock}
              title="Low Stock"
              value={stats.lowStockCount}
              description="Need replenishment"
              color="#f57c00"
            />
            <StatCard
              icon={icons.outOfStock}
              title="Out of Stock"
              value={stats.outOfStockCount}
              description="Zero inventory"
              color="#d32f2f"
            />
            <StatCard
              icon={icons.value}
              title="Inventory Value"
              value={formatCurrency(stats.totalValue)}
              description="Total worth"
              color="#7b1fa2"
            />
          </Box>

          <Box
            sx={{
              display: 'flex',
              flexWrap: 'wrap',
              gap: 2.5,
              mb: 4,
            }}
          >
            <InventoryChart />
            <StockStatusChart />
          </Box>

          <Box
            sx={{
              display: 'flex',
              flexWrap: 'wrap',
              gap: 2.5,
              mb: 4,
            }}
          >
            <RecentProductsCard />
            <LowStockCard />
          </Box>

          <Paper
            elevation={0}
            sx={{
              p: 3,
              borderRadius: 3,
              border: '1px solid',
              borderColor: 'grey.200',
            }}
          >
            <Typography variant="h6" fontWeight={600} sx={{ mb: 2.5 }}>
              Quick Actions
            </Typography>
            <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 2 }}>
              {quickActions.map((action) => (
                <Button
                  key={action.label}
                  variant="contained"
                  color={action.color}
                  startIcon={action.icon}
                  onClick={() => navigate(action.path)}
                  sx={{
                    px: 3,
                    py: 1.2,
                    borderRadius: 2,
                    boxShadow: '0 4px 14px rgba(0,0,0,0.08)',
                    textTransform: 'none',
                    fontWeight: 600,
                  }}
                >
                  {action.label}
                </Button>
              ))}
            </Box>
          </Paper>
        </>
      )}
    </>
  );
};

export default Dashboard;
