import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  IconButton,
  CircularProgress,
  Box,
  Tooltip,
  Chip,
  Button,
  Typography,
} from '@mui/material';
import {
  Visibility as ViewIcon,
  Delete as DeleteIcon,
} from '@mui/icons-material';

import formatCurrency from '../../utils/formatCurrency';
import { useLanguage } from '../../context/LanguageContext';


const ProductTable = ({
  products = [],
  page,
  totalPages,
  loading,
  onPageChange,
  onView,
  onDelete,
}) => {
  const { t } = useLanguage();

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (products.length === 0) {
    return null;
  }

  return (
    <Paper
      elevation={0}
      sx={{
        borderRadius: 3,
        border: '1px solid',
        borderColor: 'grey.200',
        overflow: 'hidden',
      }}
    >
      <TableContainer>
        <Table sx={{ minWidth: 700 }}>
          <TableHead>
            <TableRow>
              <TableCell sx={{ fontWeight: 600, color: 'text.secondary' }}>
                {t('Sr. No.')}
              </TableCell>
              <TableCell sx={{ fontWeight: 600, color: 'text.secondary' }}>
                {t('Product Name')}
              </TableCell>
              <TableCell sx={{ fontWeight: 600, color: 'text.secondary' }}>
                {t('Description')}
              </TableCell>
              <TableCell
                align="right"
                sx={{ fontWeight: 600, color: 'text.secondary' }}
              >
                {t('Price')}
              </TableCell>
              <TableCell
                align="right"
                sx={{ fontWeight: 600, color: 'text.secondary' }}
              >
                {t('Stock Quantity')}
              </TableCell>
              <TableCell sx={{ fontWeight: 600, color: 'text.secondary' }}>
                {t('Status')}
              </TableCell>
              <TableCell
                align="center"
                sx={{ fontWeight: 600, color: 'text.secondary' }}
              >
                {t('Actions')}
              </TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {products.map((product, index) => (
              <TableRow
                key={product.id}
                hover
                sx={{ '&:last-child td': { borderBottom: 0 } }}
              >
                <TableCell sx={{ fontWeight: 500 }}>
                  {page * 10 + index + 1}
                </TableCell>
                <TableCell sx={{ fontWeight: 500 }}>
                  {product.name}
                </TableCell>
                <TableCell
                  sx={{
                    maxWidth: 200,
                    overflow: 'hidden',
                    textOverflow: 'ellipsis',
                    whiteSpace: 'nowrap',
                  }}
                >
                  <Tooltip title={product.description || ''}>
                    <span>{product.description || '-'}</span>
                  </Tooltip>
                </TableCell>
                <TableCell align="right">
                  {formatCurrency(product.price)}
                </TableCell>
                <TableCell align="right">{product.stockQuantity ?? '-'}</TableCell>
                <TableCell>
                  <Chip
                    label={product.status || '-'}
                    size="small"
                    color={
                      product.status === 'ACTIVE' ? 'success' :
                      product.status === 'INACTIVE' ? 'default' :
                      'default'
                    }
                    variant="outlined"
                  />
                </TableCell>
                <TableCell align="center">
                  <IconButton
                    size="small"
                    color="primary"
                    onClick={() => onView(product)}
                  >
                    <ViewIcon fontSize="small" />
                  </IconButton>
                  <IconButton
                    size="small"
                    color="error"
                    onClick={() => onDelete(product)}
                    sx={{ ml: 1 }}
                  >
                    <DeleteIcon fontSize="small" />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
      <Box
        sx={{
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          gap: 2,
          py: 2,
          borderTop: '1px solid',
          borderColor: 'grey.200',
        }}
      >
        <Button
          size="small"
          variant="outlined"
          disabled={page === 0}
          onClick={() => onPageChange(page - 1)}
          sx={{ minWidth: 90 }}
        >
          {t('Previous')}
        </Button>
        <Typography variant="body2" color="text.secondary">
          {t('Page')} {totalPages > 0 ? page + 1 : 0} {t('of')} {totalPages}
        </Typography>
        <Button
          size="small"
          variant="outlined"
          disabled={page >= totalPages - 1}
          onClick={() => onPageChange(page + 1)}
          sx={{ minWidth: 90 }}
        >
          {t('Next')}
        </Button>
      </Box>
    </Paper>
  );
};

export default ProductTable;
