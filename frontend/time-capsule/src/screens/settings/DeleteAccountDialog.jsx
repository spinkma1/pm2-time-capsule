import React from 'react';
import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogContentText,
    DialogActions,
    Button,
    CircularProgress
} from '@mui/material';

const DeleteAccountDialog = ({ isOpen, onClose, onConfirm, isLoading }) => {
    return (
        <Dialog
            open={isOpen}
            onClose={onClose}
            maxWidth="sm"
            fullWidth
        >
            <DialogTitle sx={{ fontSize: '1.5rem', fontWeight: 'bold', color: 'rgb(17, 24, 39)' }}>
                Opravdu chcete smazat svůj účet?
            </DialogTitle>

            <DialogContent>
                <DialogContentText sx={{ color: 'rgb(75, 85, 99)' }}>
                    Tato akce je nevratná. Všechna vaše data budou označena jako smazaná a nebudete se moci přihlásit.
                    Pokud budete chtít svůj účet obnovit, budete muset kontaktovat podporu.
                </DialogContentText>
            </DialogContent>

            <DialogActions sx={{ padding: 2, gap: 1 }}>
                <Button
                    onClick={onClose}
                    disabled={isLoading}
                    variant="outlined"
                    sx={{
                        color: 'rgb(75, 85, 99)',
                        borderColor: 'rgb(229, 231, 235)',
                        '&:hover': {
                            backgroundColor: 'rgb(243, 244, 246)',
                            borderColor: 'rgb(209, 213, 219)'
                        }
                    }}
                >
                    Zrušit
                </Button>
                <Button
                    onClick={onConfirm}
                    disabled={isLoading}
                    variant="contained"
                    color="error"
                    startIcon={isLoading ? <CircularProgress size={20} color="inherit" /> : null}
                    sx={{
                        backgroundColor: 'rgb(220, 38, 38)',
                        '&:hover': {
                            backgroundColor: 'rgb(185, 28, 28)'
                        }
                    }}
                >
                    {isLoading ? 'Mazání...' : 'Smazat účet'}
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default DeleteAccountDialog;