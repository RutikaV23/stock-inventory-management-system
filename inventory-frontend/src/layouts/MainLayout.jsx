import { useState } from 'react';
import { Outlet } from 'react-router-dom';
import { Box, useMediaQuery } from '@mui/material';
import { useTheme } from '@mui/material/styles';
import Sidebar from '../components/layout/Sidebar';
import Navbar from '../components/layout/Navbar';

const MainLayout = () => {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));
  const [sidebarOpen, setSidebarOpen] = useState(true);
  const [mobileOpen, setMobileOpen] = useState(false);

  const handleToggleSidebar = () => {
    if (isMobile) {
      setMobileOpen((prev) => !prev);
    } else {
      setSidebarOpen((prev) => !prev);
    }
  };

  return (
    <Box sx={{ display: 'flex', minHeight: '100vh' }}>
      <Sidebar
        open={sidebarOpen}
        mobileOpen={mobileOpen}
        onClose={() => setMobileOpen(false)}
      />

      <Box
        sx={{
          flexGrow: 1,
          display: 'flex',
          flexDirection: 'column',
          minWidth: 0,
        }}
      >
        <Navbar onToggleSidebar={handleToggleSidebar} />

        <Box
          component="main"
          sx={{
            flexGrow: 1,
            bgcolor: 'background.default',
            p: { xs: 2, sm: 3 },
          }}
        >
          <Outlet />
        </Box>
      </Box>
    </Box>
  );
};

export default MainLayout;
