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

const RecentProductsCard = () => {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchRecent = async () => {
      try {
        const { data } = await getProducts({ page: 0, size: 5, sort: 'createdAt,desc' });
        setProducts(data.data?.content || []);
      } catch {
        setProducts([]);
      } finally {
        setLoading(false);
      }
    };
    fetchRecent();
  }, []);

  const formatDate = (dateStr) => {
    if (!dateStr) return '-';
    return new Date(dateStr).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    });
  };

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
          Recently Added Products
        </Typography>
        <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5, mb: 2 }}>
          Latest 5 products
        </Typography>
      </Box>

      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
          <CircularProgress size={28} />
        </Box>
      ) : products.length === 0 ? (
        <Typography variant="body2" color="text.disabled" sx={{ px: 3, pb: 3 }}>
          No products added yet.
        </Typography>
      ) : (
        <TableContainer>
          <Table size="small">
            <TableHead>
              <TableRow>
                <TableCell sx={{ fontWeight: 600, color: 'text.secondary' }}>
                  Product Name
                </TableCell>
                <TableCell sx={{ fontWeight: 600, color: 'text.secondary' }}>
                  Added Date
                </TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {products.map((p) => (
                <TableRow key={p.id} sx={{ '&:last-child td': { borderBottom: 0 } }}>
                  <TableCell sx={{ fontWeight: 500 }}>{p.name}</TableCell>
                  <TableCell>{formatDate(p.createdAt)}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}
    </Paper>
  );
};

export default RecentProductsCard;
