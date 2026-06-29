import { Box, Paper, Typography } from '@mui/material';

const StatCard = ({ icon, title, value, description, color = 'primary.main' }) => {
  return (
    <Paper
      elevation={0}
      sx={{
        p: 3,
        borderRadius: 3,
        border: '1px solid',
        borderColor: 'grey.200',
        flex: '1 1 180px',
        minWidth: 180,
        transition: 'transform 0.2s, box-shadow 0.2s',
        '&:hover': {
          transform: 'translateY(-4px)',
          boxShadow: '0 8px 24px rgba(0,0,0,0.08)',
        },
      }}
    >
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5, mb: 2 }}>
        <Box
          sx={{
            width: 40,
            height: 40,
            borderRadius: 2,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            bgcolor: `${color}15`,
            color: color,
          }}
        >
          {icon}
        </Box>
        <Typography variant="body2" color="text.secondary" fontWeight={500}>
          {title}
        </Typography>
      </Box>
      <Typography variant="h4" fontWeight={700} sx={{ mb: 0.5 }}>
        {value}
      </Typography>
      <Typography variant="caption" color="text.disabled">
        {description}
      </Typography>
    </Paper>
  );
};

export default StatCard;
