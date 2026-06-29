import { Box, Paper, Typography, Avatar } from '@mui/material';
import {
  Inventory2Outlined,
} from '@mui/icons-material';
import { useAuth } from '../context/AuthContext';

const Dashboard = () => {
  const { user } = useAuth();

  return (
    <>
      <Paper
        elevation={0}
        sx={{
          p: 4,
          borderRadius: 3,
          border: '1px solid',
          borderColor: 'grey.200',
          display: 'flex',
          alignItems: 'center',
          gap: 3,
        }}
      >
        <Avatar
          sx={{
            width: 64,
            height: 64,
            bgcolor: 'primary.main',
          }}
        >
          <Inventory2Outlined sx={{ fontSize: 32 }} />
        </Avatar>
        <Box>
          <Typography variant="h5" fontWeight={700} gutterBottom>
            Dashboard
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Welcome back, {user?.firstName} {user?.lastName}
          </Typography>
        </Box>
      </Paper>
    </>
  );
};

export default Dashboard;
