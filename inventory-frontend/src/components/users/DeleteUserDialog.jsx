import { useLanguage } from '../../context/LanguageContext';
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
  const { t } = useLanguage();
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
        <span>{t('Confirm Delete')}</span>
      </DialogTitle>

      <DialogContent sx={{ pt: 1 }}>
        <DialogContentText>
          {t('Are you sure you want to delete')}{' '}
          <strong>{userName || t('this user')}</strong>?
        </DialogContentText>
      </DialogContent>

      <DialogActions sx={{ px: 3, pb: 2 }}>
        <Button
          onClick={onClose}
          disabled={loading}
          variant="outlined"
          color="inherit"
        >
          {t('Cancel')}
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
            t('Delete')
          )}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default DeleteUserDialog;
