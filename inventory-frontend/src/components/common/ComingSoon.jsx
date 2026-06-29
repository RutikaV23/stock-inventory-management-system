import { Typography, Paper } from '@mui/material';
import { ConstructionOutlined } from '@mui/icons-material';

const ComingSoon = ({ title }) => {
  return (
    <Paper
      elevation={0}
      sx={{
        p: 6,
        textAlign: 'center',
        borderRadius: 3,
        border: '1px solid',
        borderColor: 'grey.200',
      }}
    >
      <ConstructionOutlined
        sx={{ fontSize: 64, color: 'text.disabled', mb: 2 }}
      />
      <Typography variant="h5" fontWeight={600} color="text.secondary" gutterBottom>
        {title || 'Coming Soon'}
      </Typography>
      <Typography variant="body1" color="text.disabled">
        This feature is under development.
      </Typography>
    </Paper>
  );
};

export default ComingSoon;
