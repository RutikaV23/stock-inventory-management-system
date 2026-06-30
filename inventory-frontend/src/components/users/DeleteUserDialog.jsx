import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  DialogActions,
  Button,
  CircularProgress,
} from '@mui/material';
import { WarningAmberOutlined } from '@mui/icons-material';

const DeleteUserDialog = ({
  open,
  onClose,
  onConfirm,
  userName,
  loading,
}) => {
  return (
    <Dialog
      open={open}
      onClose={loading ? undefined : onClose}
      maxWidth="xs"
      fullWidth
      PaperProps={{
        sx: { borderRadius: 3 },
      }}
    >
      <DialogTitle sx={{ display: 'flex', alignItems: 'center', gap: 1, pb: 1 }}>
        <WarningAmberOutlined color="warning" />
        <span>Confirm Delete</span>
      </DialogTitle>

      <DialogContent sx={{ pt: 1 }}>
        <DialogContentText>
          Are you sure you want to delete{' '}
          <strong>{userName || 'this user'}</strong>?
        </DialogContentText>
      </DialogContent>

      <DialogActions sx={{ px: 3, pb: 2 }}>
        <Button
          onClick={onClose}
          disabled={loading}
          variant="outlined"
          color="inherit"
        >
          Cancel
        </Button>
        <Button
          onClick={onConfirm}
          variant="contained"
          color="error"
          disabled={loading}
          sx={{ minWidth: 100 }}
        >
          {loading ? (
            <CircularProgress size={20} color="inherit" />
          ) : (
            'Delete'
          )}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default DeleteUserDialog;
