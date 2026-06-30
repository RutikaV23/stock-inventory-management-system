import { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import {
  AppBar,
  Toolbar,
  Typography,
  IconButton,
  Box,
  Avatar,
  Menu,
  MenuItem,
  ListItemIcon,
  Divider,
} from '@mui/material';
import { Menu as MenuIcon, Person, Logout as LogoutIcon } from '@mui/icons-material';
import { useAuth } from '../../context/AuthContext';

const pageTitles = {
  '/': 'Dashboard',
  '/products': 'Products',
  '/users': 'Users',
  '/stock-in': 'Stock In',
  '/stock-out': 'Stock Out',
  '/reports': 'Reports',
  '/profile': 'Profile',
};

const Navbar = ({ onToggleSidebar }) => {
  const location = useLocation();
  const navigate = useNavigate();
  const { user, logout } = useAuth();
  const [anchorEl, setAnchorEl] = useState(null);
  const open = Boolean(anchorEl);

  const pageTitle = pageTitles[location.pathname] || 'Dashboard';

  const handleAvatarClick = (e) => {
    setAnchorEl(e.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const handleProfile = () => {
    handleClose();
    navigate('/profile');
  };

  const handleLogout = async () => {
    handleClose();
    await logout();
    navigate('/login', { replace: true });
  };

  return (
    <AppBar
      position="sticky"
      elevation={0}
      sx={{
        bgcolor: 'white',
        borderBottom: '1px solid',
        borderColor: 'grey.200',
        color: 'text.primary',
      }}
    >
      <Toolbar sx={{ minHeight: 64 }}>
        <IconButton
          edge="start"
          onClick={onToggleSidebar}
          sx={{ mr: 2, color: 'text.secondary' }}
        >
          <MenuIcon />
        </IconButton>

        <Typography variant="h6" fontWeight={600} sx={{ flex: 1 }}>
          {pageTitle}
        </Typography>

        <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
          <IconButton onClick={handleAvatarClick} sx={{ p: 0 }}>
            <Avatar
              sx={{
                width: 34,
                height: 34,
                bgcolor: 'primary.main',
                fontSize: '0.8rem',
                fontWeight: 700,
                cursor: 'pointer',
              }}
            >
              {user?.firstName?.[0]}
              {user?.lastName?.[0]}
            </Avatar>
          </IconButton>
          <Typography variant="caption" fontWeight={500} sx={{ lineHeight: 1.2, mt: 0.3 }}>
            {user?.firstName} {user?.lastName}
          </Typography>
        </Box>

        <Menu
          anchorEl={anchorEl}
          open={open}
          onClose={handleClose}
          transformOrigin={{ horizontal: 'right', vertical: 'top' }}
          anchorOrigin={{ horizontal: 'right', vertical: 'bottom' }}
          PaperProps={{
            elevation: 2,
            sx: {
              mt: 1,
              minWidth: 220,
              borderRadius: 2,
              border: '1px solid',
              borderColor: 'grey.200',
            },
          }}
        >
          <Box sx={{ px: 2, py: 2, textAlign: 'center' }}>
            <Avatar
              sx={{
                width: 48,
                height: 48,
                bgcolor: 'primary.main',
                fontSize: '1.1rem',
                fontWeight: 700,
                mx: 'auto',
              }}
            >
              {user?.firstName?.[0]}
              {user?.lastName?.[0]}
            </Avatar>
          </Box>

          <Divider />

          <MenuItem onClick={handleProfile} sx={{ py: 1.5 }}>
            <ListItemIcon>
              <Person fontSize="small" />
            </ListItemIcon>
            My Profile
          </MenuItem>

          <MenuItem onClick={handleLogout} sx={{ py: 1.5 }}>
            <ListItemIcon>
              <LogoutIcon fontSize="small" />
            </ListItemIcon>
            Logout
          </MenuItem>
        </Menu>
      </Toolbar>
    </AppBar>
  );
};

export default Navbar;
