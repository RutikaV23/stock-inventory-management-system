import { useLocation } from 'react-router-dom';
import {
  AppBar,
  Toolbar,
  Typography,
  IconButton,
  Box,
  Avatar,
} from '@mui/material';
import { Menu as MenuIcon } from '@mui/icons-material';
import { useAuth } from '../../context/AuthContext';

const pageTitles = {
  '/': 'Dashboard',
  '/products': 'Products',
  '/stock-in': 'Stock In',
  '/stock-out': 'Stock Out',
  '/reports': 'Reports',
  '/profile': 'Profile',
  '/change-password': 'Change Password',
};

const currentDate = new Date().toLocaleDateString('en-US', {
  weekday: 'short',
  year: 'numeric',
  month: 'short',
  day: 'numeric',
});

const Navbar = ({ onToggleSidebar }) => {
  const location = useLocation();
  const { user } = useAuth();

  const pageTitle = pageTitles[location.pathname] || 'Dashboard';

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

        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
          <Typography
            variant="body2"
            color="text.secondary"
            sx={{ display: { xs: 'none', sm: 'block' } }}
          >
            {currentDate}
          </Typography>

          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            <Typography
              variant="body2"
              fontWeight={500}
              sx={{ display: { xs: 'none', sm: 'block' } }}
            >
              {user?.firstName} {user?.lastName}
            </Typography>
            <Avatar
              sx={{
                width: 34,
                height: 34,
                bgcolor: 'primary.main',
                fontSize: '0.8rem',
                fontWeight: 700,
              }}
            >
              {user?.firstName?.[0]}
              {user?.lastName?.[0]}
            </Avatar>
          </Box>
        </Box>
      </Toolbar>
    </AppBar>
  );
};

export default Navbar;
