import { useLocation, useNavigate } from 'react-router-dom';
import {
  Box,
  Drawer,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Typography,
  Divider,
  Tooltip,
} from '@mui/material';
import {
  Dashboard as DashboardIcon,
  Inventory2 as InventoryIcon,
  ArrowCircleDown as StockInIcon,
  ArrowCircleUp as StockOutIcon,
  Assessment as ReportsIcon,
  People as PeopleIcon,
  Inventory2Outlined,
} from '@mui/icons-material';
import { useAuth } from '../../context/AuthContext';

const DRAWER_WIDTH = 260;
const COLLAPSED_WIDTH = 72;

const menuItems = [
  { text: 'Dashboard', icon: <DashboardIcon />, path: '/' },
  { text: 'Products', icon: <InventoryIcon />, path: '/products' },
  { text: 'Users', icon: <PeopleIcon />, path: '/users' },
  { text: 'Stock In', icon: <StockInIcon />, path: '/stock-in' },
  { text: 'Stock Out', icon: <StockOutIcon />, path: '/stock-out' },
  { text: 'Reports', icon: <ReportsIcon />, path: '/reports' },
];

const Sidebar = ({ open, mobileOpen, onClose }) => {
  const location = useLocation();
  const navigate = useNavigate();
  const { user } = useAuth();

  const drawerContent = (
    <Box
      sx={{
        height: '100%',
        display: 'flex',
        flexDirection: 'column',
        bgcolor: '#1a2035',
        color: 'white',
      }}
    >
      <Box
        sx={{
          display: 'flex',
          alignItems: 'center',
          gap: 1.5,
          px: open ? 2.5 : 1.5,
          py: 2,
          minHeight: 64,
          justifyContent: open ? 'flex-start' : 'center',
        }}
      >
        <Inventory2Outlined sx={{ fontSize: 32, color: '#42a5f5' }} />
        {open && (
          <Box>
            <Typography variant="h6" fontWeight={700} sx={{ lineHeight: 1.2 }}>
              Inventory
            </Typography>
            <Typography variant="caption" sx={{ opacity: 0.6 }}>
              Management System
            </Typography>
          </Box>
        )}
      </Box>

      <Divider sx={{ borderColor: 'rgba(255,255,255,0.08)' }} />

      <List sx={{ flex: 1, px: 1, pt: 1 }}>
        {menuItems.map((item) => {
          const isActive = location.pathname === item.path;

          return (
            <ListItem key={item.path} disablePadding sx={{ mb: 0.5 }}>
              <Tooltip title={open ? '' : item.text} placement="right">
                <ListItemButton
                  selected={isActive}
                  onClick={() => {
                    navigate(item.path);
                    if (onClose) onClose();
                  }}
                  sx={{
                    borderRadius: 2,
                    minHeight: 44,
                    justifyContent: open ? 'initial' : 'center',
                    px: open ? 2 : 1,
                    color: isActive ? 'white' : 'rgba(255,255,255,0.65)',
                    bgcolor: isActive ? 'rgba(66,165,245,0.15)' : 'transparent',
                    '&:hover': {
                      bgcolor: isActive
                        ? 'rgba(66,165,245,0.2)'
                        : 'rgba(255,255,255,0.06)',
                      color: 'white',
                    },
                    '&.Mui-selected': {
                      bgcolor: 'rgba(66,165,245,0.15)',
                      color: 'white',
                      '&:hover': {
                        bgcolor: 'rgba(66,165,245,0.2)',
                      },
                      '& .MuiListItemIcon-root': {
                        color: '#42a5f5',
                      },
                    },
                  }}
                >
                  <ListItemIcon
                    sx={{
                      minWidth: open ? 40 : 0,
                      justifyContent: 'center',
                      color: isActive ? '#42a5f5' : 'rgba(255,255,255,0.65)',
                    }}
                  >
                    {item.icon}
                  </ListItemIcon>
                  {open && (
                    <ListItemText
                      primary={item.text}
                      primaryTypographyProps={{
                        fontSize: '0.875rem',
                        fontWeight: isActive ? 600 : 400,
                      }}
                    />
                  )}
                </ListItemButton>
              </Tooltip>
            </ListItem>
          );
        })}
      </List>

      <Divider sx={{ borderColor: 'rgba(255,255,255,0.08)' }} />

      <Box
        sx={{
          px: open ? 2.5 : 1.5,
          py: 2,
          textAlign: open ? 'left' : 'center',
        }}
      >
        <Typography
          variant="caption"
          noWrap
          sx={{ opacity: 0.6, fontSize: '0.7rem' }}
        >
          {user?.role?.replace(/_/g, ' ')}
        </Typography>
      </Box>
    </Box>
  );

  return (
    <>
      <Drawer
        variant="temporary"
        open={mobileOpen}
        onClose={onClose}
        ModalProps={{ keepMounted: true }}
        sx={{
          display: { xs: 'block', md: 'none' },
          '& .MuiDrawer-paper': {
            width: DRAWER_WIDTH,
            boxSizing: 'border-box',
          },
        }}
      >
        {drawerContent}
      </Drawer>

      <Drawer
        variant="permanent"
        sx={{
          display: { xs: 'none', md: 'block' },
          width: open ? DRAWER_WIDTH : COLLAPSED_WIDTH,
          flexShrink: 0,
          whiteSpace: 'nowrap',
          '& .MuiDrawer-paper': {
            width: open ? DRAWER_WIDTH : COLLAPSED_WIDTH,
            transition: (theme) =>
              theme.transitions.create('width', {
                easing: theme.transitions.easing.sharp,
                duration: theme.transitions.duration.enteringScreen,
              }),
            overflowX: 'hidden',
            borderRight: 'none',
          },
        }}
      >
        {drawerContent}
      </Drawer>
    </>
  );
};

export default Sidebar;
