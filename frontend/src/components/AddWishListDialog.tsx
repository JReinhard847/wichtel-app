import {WichtelParticipant, WichtelUser} from "../util/Utils.ts";
import {FormEvent, useState} from "react";
import {Box, Button, Dialog, DialogTitle, TextField} from "@mui/material";
import {useNotifications} from "@toolpad/core";
import axios from "axios";


type AddWishListDialogProps = {
    participant: WichtelUser,
    eventId: string,
    open: boolean,
    setOpen: (value: boolean) => void
}

export default function AddWishListDialog(props: AddWishListDialogProps) {
    const [participant, setParticipant] = useState<WichtelParticipant>({
        participant: props.participant,
        wishList: "",
        address: ""
    })
    const notifications = useNotifications()

    function handleClose() {
        props.setOpen(false)
    }

    function handleSubmit(e: FormEvent<HTMLFormElement>) {
        e.preventDefault()

        axios.put("/api/event/" + props.eventId + "/" + props.participant.id, participant)
            .then(() => notifications.show("Successfully added a wish list",
                {
                    autoHideDuration: 3000,
                    severity: "success"
                }))
            .catch(() => notifications.show("Failed to add a wish list",
                {
                    autoHideDuration: 3000,
                    severity: "error"
                }))
            .finally(() => {
                    handleClose()
                }
            )
    }

    return (
        <Dialog
            open={props.open}
            onClose={handleClose}
            PaperProps={{
                component: 'form',
                onSubmit: handleSubmit,
                sx: {
                    width: 400
                }
            }}
        >
            <DialogTitle>Update Event Data</DialogTitle>
            <TextField
                margin="dense"
                id="wishList"
                name="wishList"
                label="wish list"
                type="text"
                fullWidth
                variant="filled"
                multiline={true}
                minRows={3}
                maxRows={6}
                value={participant.wishList}
                onChange={(e) => setParticipant({
                        ...participant,
                        wishList: e.target.value
                    }
                )}
            />
            <TextField
                autoFocus
                margin="dense"
                id="address"
                name="address"
                label="Address (in case of remote wichteln)"
                type="text"
                fullWidth
                variant="filled"
                multiline={true}
                minRows={3}
                maxRows={6}
                value={participant.address}
                onChange={(e) => setParticipant({
                        ...participant,
                        address: e.target.value
                    }
                )}
            />
            <Box display="flex" justifyContent="space-around" alignItems="center" my={1}>
                <Button variant="contained" onClick={handleClose}>Cancel</Button>
                <Button variant="contained" color="secondary" type="submit">Save</Button>
            </Box>

        </Dialog>
    )
}