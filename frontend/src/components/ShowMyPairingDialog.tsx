import {Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField, Typography} from "@mui/material";
import {WichtelParticipant} from "../util/Utils.ts";

type ShowMyPairingDialogProps = {
    open: boolean,
    setOpen: (value: boolean) => void,
    pairing: WichtelParticipant
}

export default function ShowMyPairingDialog(props: ShowMyPairingDialogProps) {

    function handleClose() {
        props.setOpen(false)
    }

    return (
        <Dialog
            open={props.open}
            onClose={handleClose}
            PaperProps={{
                sx: {
                    width: 500,
                    padding: 3,
                    borderRadius: 2,
                },
            }}
        >
            <DialogTitle>Your Pairing</DialogTitle>
            <DialogContent>
                <Typography variant="body1" gutterBottom>
                    You have been paired with{" "}
                    <Box component="span" fontWeight="fontWeightBold">
                        {props.pairing.participant.name}
                    </Box>{" "}
                    (aka {props.pairing.participant.oauthName} on {props.pairing.participant.oauthProvider})
                </Typography>

                <Typography variant="body2" color="textSecondary" gutterBottom>
                    Your pairing has left the following information on their wish list:
                </Typography>
                <TextField
                    value={props.pairing.wishList || "No wish list was given"}
                    variant="outlined"
                    fullWidth
                    multiline
                    disabled
                    sx={{ marginBottom: 2 }}
                />

                <Typography variant="body2" color="textSecondary" gutterBottom>
                    In case of remote gifting, please send your gift to this address:
                </Typography>
                <TextField
                    value={props.pairing.address || "No address was given"}
                    variant="outlined"
                    fullWidth
                    multiline
                    disabled
                    sx={{ marginBottom: 2 }}
                />
            </DialogContent>

            <DialogActions>
                <Button variant="contained" color="primary" onClick={handleClose}>
                    Close
                </Button>
            </DialogActions>
        </Dialog>
    )
}