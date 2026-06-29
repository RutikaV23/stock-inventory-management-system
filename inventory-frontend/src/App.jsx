import { Box, CircularProgress } from '@mui/material';
import { useAuth } from './context/AuthContext';
import AppRoutes from './routes/AppRoutes';

function App() {
  const { loading } = useAuth();

  if (loading) {
    return (
      <Box
        sx={{
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          height: '100vh',
          bgcolor: 'background.default',
        }}
      >
        <CircularProgress />
      </Box>
    );
  }

  return <AppRoutes />;
}

export default App;
