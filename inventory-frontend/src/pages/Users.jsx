import { useState, useEffect, useCallback, startTransition } from 'react';
import {
  Box,
  Button,
  TextField,
  InputAdornment,
  Paper,
  Typography,
  Snackbar,
  Alert,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
} from '@mui/material';
import {
  Add as AddIcon,
  Search as SearchIcon,
  PeopleAltOutlined,
} from '@mui/icons-material';
import PageHeader from '../components/common/PageHeader';
import UserTable from '../components/users/UserTable';
import UserDialog from '../components/users/UserDialog';
import DeleteUserDialog from '../components/users/DeleteUserDialog';
import {
  getUsers,
  getUserById,
  createUser,
  updateUser,
  deleteUser,
} from '../api/userApi';

const Users = () => {
  const [users, setUsers] = useState([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(0);
  const [search, setSearch] = useState('');
  const [searchInput, setSearchInput] = useState('');
  const [statusFilter, setStatusFilter] = useState('');
  const [loading, setLoading] = useState(true);
  const [saveLoading, setSaveLoading] = useState(false);
  const [deleteLoading, setDeleteLoading] = useState(false);
  const [dialogLoading, setDialogLoading] = useState(false);

  const [formOpen, setFormOpen] = useState(false);
  const [editUser, setEditUser] = useState(null);

  const [deleteOpen, setDeleteOpen] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState(null);

  const totalPages = Math.ceil(total / 10) || 0;

  const [snackbar, setSnackbar] = useState({
    open: false,
    message: '',
    severity: 'success',
  });

  const fetchUsers = useCallback(async () => {
    setLoading(true);
    try {
      const params = { page, size: 10 };
      if (search.trim()) {
        params.keyword = search.trim();
      }
      if (statusFilter) {
        params.status = statusFilter;
      }
      const { data } = await getUsers(params);
      const responseData = data.data;
      if (responseData.content) {
        setUsers(responseData.content);
        setTotal(responseData.totalElements);
      } else if (Array.isArray(responseData)) {
        setUsers(responseData);
        setTotal(responseData.length);
      } else {
        setUsers([]);
        setTotal(0);
      }
    } catch {
      setSnackbar({
        open: true,
        message: 'Failed to load users',
        severity: 'error',
      });
    } finally {
      setLoading(false);
    }
  }, [page, search, statusFilter]);

  useEffect(() => {
    startTransition(() => {
      fetchUsers();
    });
  }, [fetchUsers]);

  const handleStatusFilter = (e) => {
    setStatusFilter(e.target.value);
    setPage(0);
  };

  const handleSearch = (e) => {
    e.preventDefault();
    setPage(0);
    setSearch(searchInput.trim());
  };

  const handleClearSearch = () => {
    setSearchInput('');
    setSearch('');
    setPage(0);
  };

  const handleChangePage = (newPage) => {
    setPage(newPage);
  };

  const handleOpenAdd = () => {
    setEditUser(null);
    setFormOpen(true);
  };

  const handleOpenView = async (user) => {
    setDialogLoading(true);
    try {
      const { data } = await getUserById(user.id);
      setEditUser(data.data || user);
      setFormOpen(true);
    } catch {
      setSnackbar({
        open: true,
        message: 'Failed to load user details',
        severity: 'error',
      });
    } finally {
      setDialogLoading(false);
    }
  };

  const handleCloseForm = () => {
    setFormOpen(false);
    setEditUser(null);
  };

  const handleSave = async (payload) => {
    setSaveLoading(true);
    try {
      if (editUser) {
        await updateUser(editUser.id, payload);
        setSnackbar({
          open: true,
          message: 'User updated successfully',
          severity: 'success',
        });
      } else {
        await createUser(payload);
        setSnackbar({
          open: true,
          message: 'User created successfully',
          severity: 'success',
        });
      }
      handleCloseForm();
      fetchUsers();
    } catch (err) {
      const message =
        err.response?.data?.message ||
        (err.message === 'Network Error'
          ? 'Unable to connect to server'
          : 'An unexpected error occurred');
      setSnackbar({ open: true, message, severity: 'error' });
    } finally {
      setSaveLoading(false);
    }
  };

  const handleOpenDelete = (user) => {
    setDeleteTarget(user);
    setDeleteOpen(true);
  };

  const handleCloseDelete = () => {
    setDeleteOpen(false);
    setDeleteTarget(null);
  };

  const handleConfirmDelete = async () => {
    if (!deleteTarget) return;
    setDeleteLoading(true);
    try {
      await deleteUser(deleteTarget.id);
      setSnackbar({
        open: true,
        message: 'User deleted successfully',
        severity: 'success',
      });
      handleCloseDelete();
      fetchUsers();
    } catch (err) {
      const message =
        err.response?.data?.message ||
        (err.message === 'Network Error'
          ? 'Unable to connect to server'
          : 'An unexpected error occurred');
      setSnackbar({ open: true, message, severity: 'error' });
    } finally {
      setDeleteLoading(false);
    }
  };

  const handleCloseSnackbar = () => {
    setSnackbar((prev) => ({ ...prev, open: false }));
  };

  return (
    <>
      <PageHeader
        title="Users"
        subtitle="Manage system users"
        action={
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={handleOpenAdd}
            sx={{ boxShadow: '0 4px 14px rgba(21,101,192,0.25)' }}
          >
            Add User
          </Button>
        }
      />

      <Paper
        elevation={0}
        sx={{
          p: 2,
          mb: 3,
          borderRadius: 2,
          border: '1px solid',
          borderColor: 'grey.200',
          display: 'flex',
          gap: 2,
          flexWrap: 'wrap',
          alignItems: 'center',
        }}
      >
        <FormControl size="small" sx={{ minWidth: 130 }}>
          <InputLabel>Status</InputLabel>
          <Select
            value={statusFilter}
            label="Status"
            onChange={handleStatusFilter}
          >
            <MenuItem value="">All</MenuItem>
            <MenuItem value="ACTIVE">Active</MenuItem>
            <MenuItem value="INACTIVE">Inactive</MenuItem>
          </Select>
        </FormControl>
        <Box
          component="form"
          onSubmit={handleSearch}
          sx={{ display: 'flex', gap: 1, flex: 1, minWidth: 280 }}
        >
          <TextField
            size="small"
            placeholder="Search by first name, last name, or email..."
            value={searchInput}
            onChange={(e) => setSearchInput(e.target.value)}
            sx={{ flex: 1, minWidth: 200 }}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <SearchIcon fontSize="small" color="action" />
                </InputAdornment>
              ),
            }}
          />
          <Button type="submit" variant="contained" size="small">
            Search
          </Button>
          {search && (
            <Button
              variant="outlined"
              size="small"
              color="inherit"
              onClick={handleClearSearch}
            >
              Clear
            </Button>
          )}
        </Box>
      </Paper>

      {loading && users.length === 0 ? (
        <UserTable loading />
      ) : users.length === 0 ? (
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
          <PeopleAltOutlined
            sx={{ fontSize: 64, color: 'text.disabled', mb: 2 }}
          />
          <Typography variant="h6" fontWeight={600} color="text.secondary" gutterBottom>
            No users found
          </Typography>
          <Typography variant="body2" color="text.disabled" sx={{ mb: 3 }}>
            {search
              ? 'No users match your search. Try a different search term.'
              : 'Get started by adding your first user.'}
          </Typography>
          {!search && (
            <Button
              variant="contained"
              startIcon={<AddIcon />}
              onClick={handleOpenAdd}
            >
              Add User
            </Button>
          )}
        </Paper>
      ) : (
        <UserTable
          users={users}
          page={page}
          totalPages={totalPages}
          loading={loading}
          onPageChange={handleChangePage}
          onView={handleOpenView}
          onDelete={handleOpenDelete}
        />
      )}

      <UserDialog
        open={formOpen}
        onClose={handleCloseForm}
        onSave={handleSave}
        user={editUser}
        loading={saveLoading || dialogLoading}
      />

      <DeleteUserDialog
        open={deleteOpen}
        onClose={handleCloseDelete}
        onConfirm={handleConfirmDelete}
        userName={
          deleteTarget
            ? `${deleteTarget.firstName} ${deleteTarget.lastName}`
            : ''
        }
        loading={deleteLoading}
      />

      <Snackbar
        open={snackbar.open}
        autoHideDuration={4000}
        onClose={handleCloseSnackbar}
        anchorOrigin={{ vertical: 'top', horizontal: 'right' }}
      >
        <Alert
          onClose={handleCloseSnackbar}
          severity={snackbar.severity}
          variant="filled"
          sx={{ width: '100%', borderRadius: 2 }}
        >
          {snackbar.message}
        </Alert>
      </Snackbar>
    </>
  );
};

export default Users;
