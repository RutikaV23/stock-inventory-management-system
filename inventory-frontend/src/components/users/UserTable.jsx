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
  Chip,
  Button,
  Typography,
} from '@mui/material';
import {
  Visibility as ViewIcon,
  Delete as DeleteIcon,
} from '@mui/icons-material';

const UserTable = ({
  users = [],
  page,
  totalPages,
  loading,
  onPageChange,
  onView,
  onDelete,
}) => {
  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (users.length === 0) {
    return null;
  }

  const formatDate = (dateStr) => {
    if (!dateStr) return '-';
    return new Date(dateStr).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
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
      }}
    >
      <TableContainer>
        <Table sx={{ minWidth: 700 }}>
          <TableHead>
            <TableRow>
              <TableCell sx={{ fontWeight: 600, color: 'text.secondary' }}>
                Sr. No.
              </TableCell>
              <TableCell sx={{ fontWeight: 600, color: 'text.secondary' }}>
                Full Name
              </TableCell>
              <TableCell sx={{ fontWeight: 600, color: 'text.secondary' }}>
                Email
              </TableCell>
              <TableCell sx={{ fontWeight: 600, color: 'text.secondary' }}>
                Phone
              </TableCell>
              <TableCell sx={{ fontWeight: 600, color: 'text.secondary' }}>
                Role
              </TableCell>
              <TableCell sx={{ fontWeight: 600, color: 'text.secondary' }}>
                Status
              </TableCell>
              <TableCell sx={{ fontWeight: 600, color: 'text.secondary' }}>
                Last Login
              </TableCell>
              <TableCell
                align="center"
                sx={{ fontWeight: 600, color: 'text.secondary' }}
              >
                Actions
              </TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {users.map((user, index) => (
              <TableRow
                key={user.id}
                hover
                sx={{ '&:last-child td': { borderBottom: 0 } }}
              >
                <TableCell sx={{ fontWeight: 500 }}>
                  {page * 10 + index + 1}
                </TableCell>
                <TableCell sx={{ fontWeight: 500 }}>
                  {user.firstName} {user.lastName}
                </TableCell>
                <TableCell>{user.email}</TableCell>
                <TableCell>{user.phone || '-'}</TableCell>
                <TableCell>
                  <Chip
                    label={user.role?.replace(/_/g, ' ') || '-'}
                    size="small"
                    color="primary"
                    variant="outlined"
                  />
                </TableCell>
                <TableCell>
                  <Chip
                    label={user.status || '-'}
                    size="small"
                    color={
                      user.status === 'ACTIVE' ? 'success' :
                      user.status === 'INACTIVE' ? 'error' :
                      'default'
                    }
                    variant="outlined"
                  />
                </TableCell>
                <TableCell>{formatDate(user.lastLoginAt)}</TableCell>
                <TableCell align="center">
                  <IconButton
                    size="small"
                    color="primary"
                    onClick={() => onView(user)}
                  >
                    <ViewIcon fontSize="small" />
                  </IconButton>
                  <IconButton
                    size="small"
                    color="error"
                    onClick={() => onDelete(user)}
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
          Previous
        </Button>
        <Typography variant="body2" color="text.secondary">
          Page {totalPages > 0 ? page + 1 : 0} of {totalPages}
        </Typography>
        <Button
          size="small"
          variant="outlined"
          disabled={page >= totalPages - 1}
          onClick={() => onPageChange(page + 1)}
          sx={{ minWidth: 90 }}
        >
          Next
        </Button>
      </Box>
    </Paper>
  );
};

export default UserTable;
