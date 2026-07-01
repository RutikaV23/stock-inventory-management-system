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
import { useLanguage } from '../../context/LanguageContext';

const ConfirmDialog = ({
  open,
  onClose,
  onConfirm,
  title: titleProp,
  message: messageProp,
  confirmLabel: confirmLabelProp,
  loading,
}) => {
  const { t } = useLanguage();
  const title = titleProp ?? t('Confirm Delete');
  const message = messageProp ?? t('Are you sure you want to delete this item?');
  const confirmLabel = confirmLabelProp ?? t('Delete');

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
        <span>{title}</span>
      </DialogTitle>

      <DialogContent sx={{ pt: 1 }}>
        <DialogContentText>{message}</DialogContentText>
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
            confirmLabel
          )}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default ConfirmDialog;
