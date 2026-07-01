import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  CircularProgress,
  Box,
  Button,
  Typography,
} from '@mui/material';
import { useLanguage } from '../../context/LanguageContext';

const StockHistoryTable = ({
  columns = [],
  rows = [],
  page,
  totalPages,
  loading,
  onPageChange,
  emptyMessage: emptyMessageProp,
  emptyIcon: EmptyIcon,
}) => {
  const { t } = useLanguage();
  const emptyMessage = emptyMessageProp ?? t('No records found.');

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (rows.length === 0) {
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
        {EmptyIcon && (
          <EmptyIcon
            sx={{ fontSize: 64, color: 'text.disabled', mb: 2 }}
          />
        )}
        <Typography variant="h6" fontWeight={600} color="text.secondary" gutterBottom>
          {emptyMessage}
        </Typography>
      </Paper>
    );
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
              {columns.map((col) => (
                <TableCell
                  key={col.key}
                  align={col.align || 'left'}
                  sx={{ fontWeight: 600, color: 'text.secondary', whiteSpace: 'nowrap' }}
                >
                  {col.label}
                </TableCell>
              ))}
            </TableRow>
          </TableHead>
          <TableBody>
            {rows.map((row, index) => (
              <TableRow
                key={row.id || index}
                hover
                sx={{ '&:last-child td': { borderBottom: 0 } }}
              >
                {columns.map((col) => (
                  <TableCell
                    key={col.key}
                    align={col.align || 'left'}
                    sx={col.sx}
                  >
                    {col.render ? col.render(row, index) : row[col.key] ?? '-'}
                  </TableCell>
                ))}
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
      {totalPages > 0 && (
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
            {t('Page')} {page + 1} {t('of')} {totalPages}
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
      )}
    </Paper>
  );
};

export default StockHistoryTable;
