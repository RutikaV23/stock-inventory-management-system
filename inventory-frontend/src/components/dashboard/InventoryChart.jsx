import { Box, Paper, Typography } from '@mui/material';
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from 'recharts';
import formatCurrency from '../../utils/formatCurrency';
import { useLanguage } from '../../context/LanguageContext';

const data = [
  { month: 'Jan', value: 45000 },
  { month: 'Feb', value: 52000 },
  { month: 'Mar', value: 48000 },
  { month: 'Apr', value: 61000 },
  { month: 'May', value: 58000 },
  { month: 'Jun', value: 72000 },
  { month: 'Jul', value: 68000 },
  { month: 'Aug', value: 75000 },
];

const InventoryChart = () => {
  const { t } = useLanguage();
  return (
    <Paper
      elevation={0}
      sx={{
        p: 3,
        borderRadius: 3,
        border: '1px solid',
        borderColor: 'grey.200',
        flex: '1 1 400px',
        minWidth: 300,
      }}
    >
      <Typography variant="h6" fontWeight={600} sx={{ mb: 2 }}>
        {t('Inventory Value')}
      </Typography>
      <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
        {t('Monthly inventory value trend (INR)')}
      </Typography>
      <Box sx={{ width: '100%', height: 260 }}>
        <ResponsiveContainer>
          <LineChart data={data} margin={{ top: 5, right: 20, left: 0, bottom: 5 }}>
            <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
            <XAxis dataKey="month" tick={{ fontSize: 12 }} stroke="#9e9e9e" />
            <YAxis tick={{ fontSize: 12 }} stroke="#9e9e9e" tickFormatter={(v) => `₹${(v / 1000).toFixed(0)}k`} />
            <Tooltip
              formatter={(value) => [formatCurrency(value), t('Value')]}
              contentStyle={{ borderRadius: 8, border: '1px solid #e0e0e0' }}
            />
            <Line
              type="monotone"
              dataKey="value"
              stroke="#1976d2"
              strokeWidth={3}
              dot={{ fill: '#1976d2', strokeWidth: 2, r: 4 }}
              activeDot={{ r: 6 }}
            />
          </LineChart>
        </ResponsiveContainer>
      </Box>
    </Paper>
  );
};

export default InventoryChart;
