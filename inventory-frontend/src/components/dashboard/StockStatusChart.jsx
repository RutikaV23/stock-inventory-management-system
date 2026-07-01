import { Box, Paper, Typography } from '@mui/material';
import {
  PieChart,
  Pie,
  Cell,
  ResponsiveContainer,
  Legend,
  Tooltip,
} from 'recharts';
import { useLanguage } from '../../context/LanguageContext';

const StockStatusChart = () => {
  const { t } = useLanguage();
  const data = [
    { name: t('Available'), value: 65, color: '#4caf50' },
    { name: t('Low Stock'), value: 20, color: '#ff9800' },
    { name: t('Out of Stock'), value: 15, color: '#f44336' },
  ];
  return (
    <Paper
      elevation={0}
      sx={{
        p: 3,
        borderRadius: 3,
        border: '1px solid',
        borderColor: 'grey.200',
        flex: '1 1 300px',
        minWidth: 260,
      }}
    >
      <Typography variant="h6" fontWeight={600} sx={{ mb: 2 }}>
        {t('Stock Status')}
      </Typography>
      <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
        {t('Current stock distribution')}
      </Typography>
      <Box sx={{ width: '100%', height: 260, display: 'flex', justifyContent: 'center' }}>
        <ResponsiveContainer>
          <PieChart>
            <Pie
              data={data}
              cx="50%"
              cy="50%"
              innerRadius={60}
              outerRadius={90}
              paddingAngle={4}
              dataKey="value"
            >
              {data.map((entry, index) => (
                <Cell key={index} fill={entry.color} />
              ))}
            </Pie>
            <Tooltip
              formatter={(value) => [`${value}%`, t('Percentage')]}
              contentStyle={{ borderRadius: 8, border: '1px solid #e0e0e0' }}
            />
            <Legend
              verticalAlign="bottom"
              iconType="circle"
              formatter={(value) => (
                <span style={{ color: '#666', fontSize: 13 }}>{value}</span>
              )}
            />
          </PieChart>
        </ResponsiveContainer>
      </Box>
    </Paper>
  );
};

export default StockStatusChart;
