import { useState, useEffect } from 'react';
import {
  Box,
  Paper,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  CircularProgress,
} from '@mui/material';
import { getProducts } from '../../api/productApi';
import { useLanguage } from '../../context/LanguageContext';

const LowStockCard = () => {
  const { t } = useLanguage();
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchLowStock = async () => {
      try {
        const { data } = await getProducts({ page: 0, size: 100 });
        const all = data.data?.content || [];
        const low = all
          .filter((p) => p.stockQuantity != null && p.minimumStock != null && p.stockQuantity <= p.minimumStock)
          .slice(0, 5);
        setProducts(low);
      } catch {
        setProducts([]);
      } finally {
        setLoading(false);
      }
    };
    fetchLowStock();
  }, []);

  return (
    <Paper
      elevation={0}
      sx={{
        borderRadius: 3,
        border: '1px solid',
        borderColor: 'grey.200',
        overflow: 'hidden',
        flex: '1 1 320px',
        minWidth: 280,
      }}
    >
      <Box sx={{ p: 3, pb: 0 }}>
        <Typography variant="h6" fontWeight={600}>
          {t('Low Stock Alert')}
        </Typography>
        <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5, mb: 2 }}>
          {t('Products needing replenishment')}
        </Typography>
      </Box>

      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
          <CircularProgress size={28} />
        </Box>
      ) : products.length === 0 ? (
        <Typography variant="body2" color="text.disabled" sx={{ px: 3, pb: 3 }}>
          {t('All products are well-stocked.')}
        </Typography>
      ) : (
        <TableContainer>
          <Table size="small">
            <TableHead>
              <TableRow>
                <TableCell sx={{ fontWeight: 600, color: 'text.secondary' }}>
                  {t('Product Name')}
                </TableCell>
                <TableCell
                  align="right"
                  sx={{ fontWeight: 600, color: 'text.secondary' }}
                >
                  {t('Remaining')}
                </TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {products.map((p) => (
                <TableRow key={p.id} sx={{ '&:last-child td': { borderBottom: 0 } }}>
                  <TableCell sx={{ fontWeight: 500 }}>{p.name}</TableCell>
                  <TableCell align="right">
                    <Typography
                      variant="body2"
                      fontWeight={600}
                      color={p.stockQuantity === 0 ? 'error' : 'warning.main'}
                    >
                      {p.stockQuantity}
                    </Typography>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}
    </Paper>
  );
};

export default LowStockCard;
